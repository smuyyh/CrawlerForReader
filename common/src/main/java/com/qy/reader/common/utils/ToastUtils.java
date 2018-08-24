package com.qy.reader.common.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.qy.reader.common.Global;
import com.qy.reader.common.R;

/**
 * Toast工具类，防止多个Toast连续显示
 *
 * @author yuyh.
 * @date 17/2/10.
 */
public class ToastUtils {

    private static Toast mToast;

    /********************** 非连续弹出的Toast ***********************/
    public static void showSingleToast(int resId) { //R.string.**
        getSingleToast(resId, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleToast(String text) {
        getSingleToast(text, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleLongToast(int resId) {
        getSingleToast(resId, Toast.LENGTH_LONG).show();
    }

    public static void showSingleLongToast(String text) {
        getSingleToast(text, Toast.LENGTH_LONG).show();
    }

    /*********************** 连续弹出的Toast ************************/
    public static void showToast(int resId) {
        getToast(resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String text) {
        getToast(text, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(int resId) {
        getToast(resId, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(String text) {
        getToast(text, Toast.LENGTH_LONG).show();
    }

    public static Toast getSingleToast(int resId, int duration) { // 连续调用不会连续弹出，只是替换文本
        return getSingleToast(Global.getApplication().getResources().getText(resId).toString(), duration);
    }

    public static Toast getSingleToast(String text, int duration) {
        if (mToast == null) {
            mToast = new Toast(Global.getApplication());
            mToast.setView(LayoutInflater.from(Global.getApplication()).inflate(R.layout.common_toast_layout, null));
            mToast.setText(text);
            mToast.setGravity(Gravity.BOTTOM, 0, ScreenUtils.dpToPxInt(100));
        } else {
            mToast.setText(text);
        }
        return mToast;
    }

    public static Toast getToast(int resId, int duration) { // 连续调用会连续弹出
        return getToast(Global.getApplication().getResources().getText(resId).toString(), duration);
    }

    public static Toast getToast(String text, int duration) {
        return Toast.makeText(Global.getApplication(), text, duration);
    }
}
