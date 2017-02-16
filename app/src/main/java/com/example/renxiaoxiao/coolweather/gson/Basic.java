package com.example.renxiaoxiao.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by renxiaoxiao on 2017/2/15.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;

    }

}
