package com.example.renxiaoxiao.coolweather.utils;

import android.text.TextUtils;

import com.example.renxiaoxiao.coolweather.application.MyApplication;
import com.example.renxiaoxiao.coolweather.beans.City;
import com.example.renxiaoxiao.coolweather.beans.County;
import com.example.renxiaoxiao.coolweather.beans.Province;
import com.example.renxiaoxiao.coolweather.gen.CityDao;
import com.example.renxiaoxiao.coolweather.gen.CountyDao;
import com.example.renxiaoxiao.coolweather.gen.ProvinceDao;
import com.example.renxiaoxiao.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by renxiaoxiao on 2017/2/15.
 */

public class ParseUtil {

    public static boolean parseProvinceResponse(String response) {
        ProvinceDao provinceDao = MyApplication.getInstance().getDaoSession().getProvinceDao();
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0; i < allProvince.length(); i++) {
                    JSONObject jsonObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    provinceDao.save(province);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean parseCityResponse(String response, int provinceId) {
        CityDao cityDao = MyApplication.getInstance().getDaoSession().getCityDao();
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCity = new JSONArray(response);
                for (int i = 0; i < allCity.length(); i++) {
                    JSONObject jsonObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityName(jsonObject.getString("name"));
                    city.setCityCode(jsonObject.getInt("id"));
                    cityDao.save(city);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean parseCountyResponse(String response, int cityId) {
        CountyDao countyDao = MyApplication.getInstance().getDaoSession().getCountyDao();
        if (!TextUtils.isEmpty(response)) {
            try {
                System.out.println(response);
                JSONArray allCounty = new JSONArray(response);
                for (int i = 0; i < allCounty.length(); i++) {
                    JSONObject jsonObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    countyDao.save(county);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather parseWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
