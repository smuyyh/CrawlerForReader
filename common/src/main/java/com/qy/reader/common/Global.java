package com.qy.reader.common;

import android.app.Application;

/**
 * Created by yuyuhang on 2018/1/8.
 */
public class Global {

    private static Application application;

    public static void init(Application application) {
        Global.application = application;
    }

    public static Application getApplication() {
        return application;
    }
}
