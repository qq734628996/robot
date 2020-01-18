package com.classwork.robot;

import java.util.List;

public class XFResult {
    private int sn;

    private boolean ls;

    private int bg;

    private int ed;

    List<Ws> ws;

    class Ws {
        private int bg;

        List<Cw> cw;
    }

    class Cw {
        private int sc;

        private String w;
    }

    String getWord() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ws.size(); i++) {
            Ws ws = this.ws.get(i);
            for (int j = 0; j < ws.cw.size(); j++) {
                Cw cw = ws.cw.get(j);
                builder.append(cw.w);
            }
        }
        return builder.toString();
    }
}
