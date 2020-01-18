package com.classwork.robot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

@SuppressLint({"CheckResult", "ClickableViewAccessibility", "HandlerLeak"})
public class MainActivity extends AppCompatActivity {

    private MessageAdapter adapter;
    private boolean isPlayMessage = false;
    private RecyclerView recyclerView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            recyclerView.scrollToPosition(adapter.data.size() - 1);
        }
    };

    private Realm realm;

    private MessagePlayProfile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RxPermissions(this).request(
                Manifest.permission.RECORD_AUDIO
        ).subscribe(b -> {
            if (!b) MainActivity.this.finish();
        }, Throwable::printStackTrace);


        realm = Realm.getDefaultInstance();
        profile = realm.where(MessagePlayProfile.class).findFirst();

        initMessageList();
        initUI();
    }

    /**
     * 初始化页面相关控件
     */
    private void initUI() {
        final EditText messageInput = findViewById(R.id.et_message);
        final ImageView sendIv = findViewById(R.id.iv_send);
        final ImageView voiceIv = findViewById(R.id.iv_voice);
        final ImageView deleteIv = findViewById(R.id.iv_delete);
        final ImageView soundIv = findViewById(R.id.iv_sound);
        final ImageView menuIv = findViewById(R.id.iv_menu);

        messageInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                handler.sendEmptyMessageDelayed(0, 100);
            }
            return false;
        });

        //点击后打开讯飞语音识别进行语音识别
        voiceIv.setOnClickListener(v -> {
            RecognizerDialog mDialog = new RecognizerDialog(MainActivity.this, i -> {
            });

            mDialog.setListener(new RecognizerDialogListener() {
                Gson gson = new Gson();
                StringBuilder builder = new StringBuilder();

                @Override
                public void onResult(RecognizerResult recognizerResult, boolean b) {
                    String resultString = recognizerResult.getResultString();
                    //收集文字
                    if (resultString != null) {
                        XFResult result = gson.fromJson(resultString, XFResult.class);
                        builder.append(result.getWord());
                    }
                    //语音结束后将收集到的文字作为消息发出
                    if (b) {
                        sendMessage(builder.toString());
                    }
                }

                @Override
                public void onError(SpeechError speechError) {
                }
            });
            mDialog.show();
        });

        //机器人消息的语音播报开关
        soundIv.setOnClickListener(v -> {
            if (isPlayMessage) {
                soundIv.setImageDrawable(getDrawable(R.drawable.jinyin));
            } else {
                soundIv.setImageDrawable(getDrawable(R.drawable.shenyin));
            }
            isPlayMessage = !isPlayMessage;
        });

        //清空所有消息
        deleteIv.setOnClickListener(v -> new AlertDialog.Builder(MainActivity.this)
                .setTitle("删除消息")
                .setMessage("此操作将会删除所有消息")
                .setPositiveButton("删除", (dialog, which) -> {
                    realm.executeTransaction(_realm -> _realm.deleteAll());
                    adapter.clear();
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .create().show());

        menuIv.setOnClickListener(v -> {
            startActivity(new Intent(this, SetPronunciationActivity.class));
        });

        //发送文本消息
        sendIv.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                sendMessage(messageInput.getText().toString().trim());
                messageInput.setText("");
            }
        });
    }

    private void initMessageList() {
        recyclerView = findViewById(R.id.messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        RealmResults<Message> data = realm.where(Message.class).sort("date", Sort.ASCENDING).findAll();
        adapter = new MessageAdapter(this, realm.copyFromRealm(data));
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            //出现新消息时滚动到底部，保持列表始终显示最新的消息
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.scrollToPosition(adapter.data.size() - 1);
            }

        });
        //列表初始化完成后滚动到底部
        recyclerView.scrollToPosition(adapter.data.size() - 1);
    }

    private void sendMessage(String text) {
        //先保存自己发送的消息
        adapter.addMessage(new Message(text, true));

        //构造请求体发送消息
        TulingRequstBody body = new TulingRequstBody();
        body.perception.inputText.text = text;
        NetHelper.getInstance().sendMessage(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    TulingResultBody.Results results = result.results.get(0);

                    //获取返回的文本内容并保存消息，然后根据开关选择是否播报消息
                    if (results.resultType.equals("text")) {
                        adapter.addMessage(new Message(results.values.text, false));
                        if (isPlayMessage) {
                            playMessage(results.values.text);
                        }
                    }

                }, throwable -> Toast.makeText(MainActivity.this, "机器人没能收到这条消息", Toast.LENGTH_SHORT).show());
    }

    private void playMessage(String text) {
        SpeechSynthesizer speechSynthesizer = SpeechSynthesizer.createSynthesizer(this, null);
        speechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        if (!profile.isLocal) {
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, profile.code);

        } else {
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            String resourcePath = getResourcePath();
            speechSynthesizer.setParameter(ResourceUtil.TTS_RES_PATH, resourcePath);
            speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, profile.code);
        }
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, profile.code);
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        speechSynthesizer.setParameter(SpeechConstant.PITCH, "50");
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "50");
        speechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");
        speechSynthesizer.startSpeaking(text, null);
    }

    private String getResourcePath() {
        return ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet") +
                ";" +
                ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + profile.code + ".jet");
    }
}
