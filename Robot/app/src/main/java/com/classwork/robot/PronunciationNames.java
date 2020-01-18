package com.classwork.robot;

import java.util.HashMap;

import static com.classwork.robot.PronunciationNames.Local.*;
import static com.classwork.robot.PronunciationNames.Local.XIAO_YAN;
import static com.classwork.robot.PronunciationNames.Remote.*;

class PronunciationNames {

    static final HashMap<String, String> codeAndName = new HashMap<>();
    static final HashMap<String, String> nameAndCode = new HashMap<>();

    static void init() {
        codeAndName.put(XIAO_YAN, "小燕(普通话)");
        codeAndName.put(XIAO_FENG, "小锋(普通话)");

        codeAndName.put(XU_XIAO_BAO, "许小宝(普通话)");
        codeAndName.put(CHNEG_CHENG, "程程(普通话)");
        codeAndName.put(XIAO_RONG, "小蓉(四川话)");
        codeAndName.put(XIAO_MEI, "小梅(广东话)");
        codeAndName.put(JOHN, "John(英语)");


        nameAndCode.put("小燕(普通话)", XIAO_YAN);
        nameAndCode.put("小锋(普通话)", XIAO_FENG);

        nameAndCode.put("许小宝(普通话)", XU_XIAO_BAO);
        nameAndCode.put("程程(普通话)", CHNEG_CHENG);
        nameAndCode.put("小蓉(四川话)", XIAO_RONG);
        nameAndCode.put("小梅(广东话)", XIAO_MEI);
        nameAndCode.put("John(英语)", JOHN);
    }

    static class Local {
        static final String XIAO_YAN = "xiaoyan";
        static final String XIAO_FENG = "xiaofeng";
        static final String[] codeList = {XIAO_YAN, XIAO_FENG};
    }

    static class Remote {
        static final String XU_XIAO_BAO = "aisbabyxu";
        static final String CHNEG_CHENG = "x_chengcheng";

        static final String XIAO_RONG = "x_xiaorong";
        static final String XIAO_MEI = "x_xiaomei";
        static final String JOHN = "x_john";
        static final String[] codeList = {XU_XIAO_BAO, CHNEG_CHENG, XIAO_RONG, XIAO_MEI, JOHN};
    }
}
