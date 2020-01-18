package com.classwork.robot;

class TulingRequstBody {
    private int reqType = 0;
    Perception perception = new Perception();
    private  UserInfo userInfo = new UserInfo();

    private class UserInfo {
        private  String apiKey = "02ad6413903c44eabd7663b602f052c6";
        private String userId = "460081";
    }

    class Perception {
        InputText inputText = new InputText();
        private SelfInfo selfInfo = new SelfInfo();
    }

    private class SelfInfo {
        Location location = new Location();
    }

    private class Location {
        String city = "北京";
        String province = "北京";
        String street = "天安门";
    }

    class InputText {
        String text;
    }
}
