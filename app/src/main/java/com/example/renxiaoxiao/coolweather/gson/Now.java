package com.example.renxiaoxiao.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by renxiaoxiao on 2017/2/15.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
