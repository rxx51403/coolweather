package com.example.renxiaoxiao.coolweather.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.example.renxiaoxiao.coolweather.database.DBHelper;
import com.example.renxiaoxiao.coolweather.gen.DaoMaster;
import com.example.renxiaoxiao.coolweather.gen.DaoSession;

/**
 * Created by renxiaoxiao on 2017/2/16.
 */

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    private static MyApplication instance;

    private DBHelper mDBHelper;

    private SQLiteDatabase db;

    private DaoMaster mDaoMaster;

    private DaoSession mDaoSession;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDatabase();
    }

    private void initDatabase() {
        mDBHelper = new DBHelper(this);
        db = mDBHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
