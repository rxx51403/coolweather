package com.example.renxiaoxiao.coolweather.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by renxiaoxiao on 2017/2/15.
 */

@Entity
public class County {

    @Id
    private Long id;

    @Property(nameInDb = "COUNTYNAME")
    private  String countyName;

    @Property(nameInDb = "WEATHERID")
    private String weatherId;

    @Property(nameInDb = "CITYID")
    private int cityId;

    @Generated(hash = 1088402961)
    public County(Long id, String countyName, String weatherId, int cityId) {
        this.id = id;
        this.countyName = countyName;
        this.weatherId = weatherId;
        this.cityId = cityId;
    }

    @Generated(hash = 1991272252)
    public County() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountyName() {
        return this.countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return this.weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return this.cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

}
