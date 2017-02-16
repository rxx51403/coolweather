package com.example.renxiaoxiao.coolweather.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by renxiaoxiao on 2017/2/15.
 */

@Entity
public class Province {

    @Id
    private Long id;

    @Property(nameInDb = "PROVINCENAME")
    private String provinceName;

    @Property(nameInDb = "PROVINCECODE")
    private int provinceCode;

    @Generated(hash = 1695957187)
    public Province(Long id, String provinceName, int provinceCode) {
        this.id = id;
        this.provinceName = provinceName;
        this.provinceCode = provinceCode;
    }

    @Generated(hash = 1309009906)
    public Province() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvinceName() {
        return this.provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return this.provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
    
}
