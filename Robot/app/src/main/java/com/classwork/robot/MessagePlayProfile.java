package com.classwork.robot;

import io.realm.RealmObject;

public class MessagePlayProfile extends RealmObject {
    public MessagePlayProfile() {

    }

    MessagePlayProfile(boolean isLocal, String code) {
        this.isLocal = isLocal;
        this.code = code;
    }
    boolean isLocal = true;
    String code = "xiaoyan";
}
