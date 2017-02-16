package com.example.renxiaoxiao.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.renxiaoxiao.coolweather.R;
import com.example.renxiaoxiao.coolweather.gson.Forecast;
import com.example.renxiaoxiao.coolweather.gson.Weather;
import com.example.renxiaoxiao.coolweather.service.AutoUpdateService;
import com.example.renxiaoxiao.coolweather.utils.HttpRequestAddress;
import com.example.renxiaoxiao.coolweather.utils.HttpUtil;
import com.example.renxiaoxiao.coolweather.utils.ParseUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public SwipeRefreshLayout mSrlRefresh;

    private Button mBtnNav;

    public DrawerLayout mDlChooseArea;

    private ScrollView mSvWeather;

    private TextView mTvTitleCity;

    private TextView mTvTitleUpdateTime;

    private TextView mTvDegree;

    private TextView mTvWeatherInfo;

    private LinearLayout mLlForecast;

    private TextView mTvAqi;

    private TextView mTvPm25;

    private TextView mTvComfort;

    private TextView mTvCarWash;

    private TextView mTvSport;

    private ImageView mIvPicImg;

    public String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        setUiVisibility();
        initLayout();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        String bingPic = preferences.getString("bingPic", null);
        if (weatherString != null) {
            Weather weather = ParseUtil.parseWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            mWeatherId = getIntent().getStringExtra("weather_id");
            mSvWeather.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(mIvPicImg);
        } else {
            loadBingPic();
        }
        mSrlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!TextUtils.isEmpty(mWeatherId)) {
                    requestWeather(mWeatherId);
                }
            }
        });
        mBtnNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDlChooseArea.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initLayout() {
        mSvWeather = (ScrollView) findViewById(R.id.weather_layout);
        mTvTitleCity = (TextView) findViewById(R.id.title_city);
        mTvTitleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        mTvDegree = (TextView) findViewById(R.id.degree_text);
        mTvWeatherInfo = (TextView) findViewById(R.id.weather_info_text);
        mLlForecast = (LinearLayout) findViewById(R.id.forecast_layout);
        mTvAqi = (TextView) findViewById(R.id.aqi_text);
        mTvPm25 = (TextView) findViewById(R.id.pm25_text);
        mTvComfort = (TextView) findViewById(R.id.comfort_text);
        mTvCarWash = (TextView) findViewById(R.id.car_wash_text);
        mTvSport = (TextView) findViewById(R.id.sport_text);
        mIvPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        mSrlRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mDlChooseArea = (DrawerLayout) findViewById(R.id.drawer_layout);
        mBtnNav = (Button) findViewById(R.id.nav_button);

        mSrlRefresh.setColorSchemeResources(R.color.colorPrimary);
    }

    private void setUiVisibility() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void loadBingPic() {
        HttpUtil.sendOkhttpRequest(HttpRequestAddress.REQUEST_BING_PIC, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bingPic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(mIvPicImg);
                    }
                });
            }
        });
    }

    public void requestWeather(String weatherId) {

        System.out.println("weatherId = " + weatherId);

        String weatherUrl = HttpRequestAddress.REQUEST_WEATHER + weatherId + "&key="
                + HttpRequestAddress.WEATHER_KEY;
        System.out.println("url:" + weatherUrl);
        HttpUtil.sendOkhttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("e:" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, getString(R.string.get_weather_fail),
                                Toast.LENGTH_SHORT).show();
                        mSrlRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                System.out.println("responseText = " + responseText);
                final Weather weather = ParseUtil.parseWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, getString(R.string.get_weather_fail),
                                    Toast.LENGTH_SHORT).show();
                        }
                        mSrlRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        System.out.println("llweatherInfo:" + weatherInfo);
        mTvTitleCity.setText(cityName);
        mTvTitleUpdateTime.setText(updateTime);
        mTvDegree.setText(degree);
        mTvWeatherInfo.setText(weatherInfo);
        mLlForecast.removeAllViews();
        for (Forecast forecast: weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mLlForecast, false);
            TextView dateTv = (TextView) view.findViewById(R.id.date_text);
            TextView infoTv = (TextView) view.findViewById(R.id.info_text);
            TextView maxTv = (TextView) view.findViewById(R.id.max_text);
            TextView minTv = (TextView) view.findViewById(R.id.min_text);
            dateTv.setText(forecast.date);
            infoTv.setText(forecast.more.info);
            maxTv.setText(forecast.temperature.max);
            minTv.setText(forecast.temperature.min);
            mLlForecast.addView(view);
        }
        if (weather.aqi != null) {
            mTvAqi.setText(weather.aqi.city.aqi);
            mTvPm25.setText(weather.aqi.city.pm25);
        }
        String comfort = getString(R.string.comfort) + weather.suggestion.comfort.info;
        String carWash = getString(R.string.car_wash) + weather.suggestion.carWash.info;
        String sport = getString(R.string.sport_suggestion) + weather.suggestion.sport.info;
        mTvComfort.setText(comfort);
        mTvCarWash.setText(carWash);
        mTvSport.setText(sport);
        mSvWeather.setVisibility(View.VISIBLE);
    }
}
