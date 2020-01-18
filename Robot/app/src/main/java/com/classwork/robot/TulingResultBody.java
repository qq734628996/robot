package com.classwork.robot;

import java.util.List;

class TulingResultBody {
    Intent intent;
    List<Results> results;

    class Results {
        int groupType;
        String resultType;
        Values values;
    }

    class Values {
        String url;
        String text;
    }

    class Intent {
        int code;
        String intentName;
        String actionName;
        Parameters parameters;
    }

    class Parameters {
        String nearby_place;
    }
}
