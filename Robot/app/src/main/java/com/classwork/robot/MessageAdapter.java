package com.classwork.robot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int ROBOT = 1;
    private static final int SELF = 2;

    private LayoutInflater inflater;

    List<Message> data = new ArrayList<>();

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

    MessageAdapter(Context context, List<Message> data) {
        this.inflater = LayoutInflater.from(context);
        this.data.addAll(data);
    }

    void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    void addMessage(Message message) {
        data.add(message);
        notifyItemInserted(data.size());
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(_realm -> _realm.copyToRealm(message));
        realm.close();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        int resId = type == ROBOT ? R.layout.view_message_robot_item : R.layout.view_message_self_item;
        return new MessageViewHolder(inflater.inflate(resId, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        Message message = data.get(i);
        TextView textView = messageViewHolder.itemView.findViewById(R.id.tv_text);
        TextView dateView = messageViewHolder.itemView.findViewById(R.id.tv_date);

        textView.setText(message.text);
        dateView.setText(format.format(message.date));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = data.get(position);
        return message.self ? SELF : ROBOT;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
