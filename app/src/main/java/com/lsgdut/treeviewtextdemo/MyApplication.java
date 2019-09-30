package com.lsgdut.treeviewtextdemo;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;


public class MyApplication extends Application {

    public static int screenHeight;
    private static int screenWidth;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        mContext = getApplicationContext();
    }

    public static int getScreenWidth() {
        return screenWidth;
    }
    public static Context getContext() {
        return mContext;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

}
