package com.example.renxiaoxiao.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.renxiaoxiao.coolweather.activity.WeatherActivity;
import com.example.renxiaoxiao.coolweather.gson.Weather;
import com.example.renxiaoxiao.coolweather.utils.HttpRequestAddress;
import com.example.renxiaoxiao.coolweather.utils.HttpUtil;
import com.example.renxiaoxiao.coolweather.utils.ParseUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updatePic();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + time;
        Intent newIntent = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, newIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherText = preferences.getString("weather", null);
        if (weatherText != null) {
            Weather weather = ParseUtil.parseWeatherResponse(weatherText);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = HttpRequestAddress.REQUEST_WEATHER + weatherId + "&key="
                    + HttpRequestAddress.WEATHER_KEY;
            HttpUtil.sendOkhttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather resWeather = ParseUtil.parseWeatherResponse(responseText);
                    if (resWeather != null && "ok".equals(resWeather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    private void updatePic() {
        HttpUtil.sendOkhttpRequest(HttpRequestAddress.REQUEST_BING_PIC, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bingPic", bingPic);
                editor.apply();
            }
        });
    }
}
