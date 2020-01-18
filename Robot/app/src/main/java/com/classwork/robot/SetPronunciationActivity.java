package com.classwork.robot;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class SetPronunciationActivity extends AppCompatActivity {

    private Realm realm;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pronunciation);

        recyclerView = findViewById(R.id.name_list);

        realm = Realm.getDefaultInstance();
        MessagePlayProfile profile = realm.where(MessagePlayProfile.class).findFirst();
        if (profile == null) {
            return;
        }

        String[] codes = profile.isLocal ? PronunciationNames.Local.codeList : PronunciationNames.Remote.codeList;
        ArrayList<String> names = new ArrayList<>();
        for (String code : codes) {
            String name = PronunciationNames.codeAndName.get(code);
            names.add(name);
        }

        NameAdapter adapter = new NameAdapter(this, names, profile.isLocal);
        recyclerView.setAdapter(adapter);

        RadioButton offline = findViewById(R.id.rb_offline);
        RadioButton online = findViewById(R.id.rb_online);

        offline.setChecked(profile.isLocal);
        online.setChecked(!profile.isLocal);

        offline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ArrayList<String> localNames = new ArrayList<>();
                for (String code : PronunciationNames.Local.codeList) {
                    String name = PronunciationNames.codeAndName.get(code);
                    localNames.add(name);
                }
                adapter.changeData(localNames, true);
            }
        });

        online.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ArrayList<String> remoteName = new ArrayList<>();
                for (String code : PronunciationNames.Remote.codeList) {
                    String name = PronunciationNames.codeAndName.get(code);
                    remoteName.add(name);
                }
                adapter.changeData(remoteName, false);
            }
        });
    }

    class NameAdapter extends RecyclerView.Adapter<NameAdapter.NameViewHolder> {
        private List<String> data = new ArrayList<>();
        private LayoutInflater inflater;
        private boolean isLocal;

        NameAdapter(Context context, List<String> data, boolean isLocal) {
            this.isLocal = isLocal;
            this.inflater = LayoutInflater.from(context);
            this.data.addAll(data);
        }

        void changeData(List<String> data, boolean isLocal) {
            this.isLocal = isLocal;
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public NameViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new NameViewHolder(inflater.inflate(R.layout.view_name_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull NameViewHolder nameViewHolder, int i) {
            String name = data.get(i);

            MessagePlayProfile profile = realm.where(MessagePlayProfile.class).findFirst();
            if (profile == null) return;
            TextView nameView = nameViewHolder.itemView.findViewById(R.id.tv_name);
            nameView.setText(name);
            CheckBox cb = nameViewHolder.itemView.findViewById(R.id.cb);
            cb.setChecked(profile.code.equals(PronunciationNames.nameAndCode.get(name)));
            cb.setOnClickListener(v -> {
                if (cb.isChecked()) {
                    realm.executeTransaction(realm -> {
                        profile.code = PronunciationNames.nameAndCode.get(name);
                        profile.isLocal = isLocal;
                    });
                    notifyDataSetChanged();
                }
                cb.setChecked(true);
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class NameViewHolder extends RecyclerView.ViewHolder {

            NameViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
