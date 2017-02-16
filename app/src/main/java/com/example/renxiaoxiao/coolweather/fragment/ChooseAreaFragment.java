package com.example.renxiaoxiao.coolweather.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renxiaoxiao.coolweather.application.MyApplication;
import com.example.renxiaoxiao.coolweather.R;
import com.example.renxiaoxiao.coolweather.activity.MainActivity;
import com.example.renxiaoxiao.coolweather.activity.WeatherActivity;
import com.example.renxiaoxiao.coolweather.beans.City;
import com.example.renxiaoxiao.coolweather.beans.County;
import com.example.renxiaoxiao.coolweather.beans.Province;
import com.example.renxiaoxiao.coolweather.gen.CityDao;
import com.example.renxiaoxiao.coolweather.gen.CountyDao;
import com.example.renxiaoxiao.coolweather.gen.DaoSession;
import com.example.renxiaoxiao.coolweather.utils.HttpRequestAddress;
import com.example.renxiaoxiao.coolweather.utils.HttpUtil;
import com.example.renxiaoxiao.coolweather.utils.ParseUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAreaFragment extends Fragment {

    private static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog mProgressDialog;

    private TextView mTvTitle;

    private Button mBtnBack;

    private ListView mLvData;

    private ArrayAdapter<String> mAdapter;

    private List<String> mDataList = new ArrayList<>();

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;

    private Province selectedProvince;

    private City selectedCity;

    private int currentLevel;

    private DaoSession mDaoSession = MyApplication.getInstance().getDaoSession();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        mTvTitle = (TextView) view.findViewById(R.id.title_text);
        mBtnBack = (Button) view.findViewById(R.id.back_button);
        mLvData = (ListView) view.findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mDataList);
        mLvData.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCity();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounty();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.mWeatherId = weatherId;
                        activity.mDlChooseArea.closeDrawers();
                        activity.mSrlRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_CITY) {
                    queryProvince();
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryCity();
                }
            }
        });
        queryProvince();
    }

    private void queryProvince() {
        mTvTitle.setText(getString(R.string.china));
        mBtnBack.setVisibility(View.GONE);
        provinceList = mDaoSession.getProvinceDao().loadAll();
        if (provinceList.size() > 0) {
            mDataList.clear();
            for (Province province: provinceList) {
                mDataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mLvData.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(HttpRequestAddress.REQUEST_DATA, "province");
        }
    }

    private void queryCity() {
        mTvTitle.setText(selectedProvince.getProvinceName());
        mBtnBack.setVisibility(View.VISIBLE);
        CityDao cityDao = mDaoSession.getCityDao();
        cityList = cityDao.queryBuilder()
                .where(CityDao.Properties.ProvinceId.eq(selectedProvince.getProvinceCode()))
                .build().list();
        if (cityList.size() > 0) {
            mDataList.clear();
            for (City city: cityList) {
                mDataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mLvData.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = HttpRequestAddress.REQUEST_DATA + "/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    private void queryCounty() {
        mTvTitle.setText(selectedCity.getCityName());
        mBtnBack.setVisibility(View.VISIBLE);
        CountyDao countyDao = mDaoSession.getCountyDao();
        countyList = countyDao.queryBuilder()
                .where(CountyDao.Properties.CityId.eq(selectedCity.getCityCode()))
                .build().list();
        if (countyList.size() > 0) {
            mDataList.clear();
            for (County county : countyList) {
                mDataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mLvData.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = HttpRequestAddress.REQUEST_DATA + "/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(final String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.query_fail), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = ParseUtil.parseProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = ParseUtil.parseCityResponse(responseText, selectedProvince.getProvinceCode());
                } else if ("county".equals(type)) {
                    result = ParseUtil.parseCountyResponse(responseText, selectedCity.getCityCode());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            if ("province".equals(type)) {
                                queryProvince();
                            } else if ("city".equals(type)) {
                                queryCity();
                            } else if ("county".equals(type)) {
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("正在加载…");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
