package com.zhongjh.wifilistdemo;

import android.app.Application;

public class App extends Application {

    private static App mBaseApp = null;

    public static App getInstance() {
        if (mBaseApp == null) {
            throw new IllegalStateException("Application is not created.");
        }
        return mBaseApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBaseApp = this;
    }

}
