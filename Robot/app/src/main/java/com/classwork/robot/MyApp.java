package com.classwork.robot;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import io.realm.Realm;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        Realm realm = Realm.getDefaultInstance();
        Message message = realm.where(Message.class).findFirst();
        if (message == null) {
            realm.executeTransaction(_realm ->
                    _realm.copyToRealm(new Message("你好！我是机器人，会聊天的那种，我们聊点什么吧！", false)));
        }

        MessagePlayProfile profile = realm.where(MessagePlayProfile.class).findFirst();
        if (profile == null) {
            realm.executeTransaction(_realm ->
                    _realm.copyToRealm(new MessagePlayProfile(true,"xiaoyan")));
        }

        PronunciationNames.init();
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5cff673b");
    }
}
