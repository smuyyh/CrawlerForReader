package com.qy.reader.common.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by yuyuhang on 2017/12/4.
 */
public class HandlerFactory {

    private static Handler sUIHandler;

    static {
        sUIHandler = new Handler(Looper.getMainLooper());
    }

    public static Handler getUIHandler() {
        return sUIHandler;
    }
}
