package com.example.renxiaoxiao.coolweather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.renxiaoxiao.coolweather.gen.DaoMaster;

/**
 * Created by renxiaoxiao on 2017/2/16.
 */

public class DBHelper extends DaoMaster.OpenHelper {

    public static final String DB_NAME = "coolWeather.db";

    public DBHelper(Context context) {
        super(context, DB_NAME, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
