package com.classwork.robot;

import java.util.Date;

import io.realm.RealmObject;

public class Message extends RealmObject {

    public Message(){}

    public Message(String text, boolean self) {
        this.text = text;
        this.self = self;
    }

    public String text;
    public Date date = new Date();
    public boolean self;
}
