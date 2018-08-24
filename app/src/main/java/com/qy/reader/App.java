package com.qy.reader;

import android.app.Application;

import com.qy.reader.common.Global;

/**
 * Created by yuyuhang on 2018/1/8.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Global.init(this);
    }
}
