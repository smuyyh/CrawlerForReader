package com.qy.reader.common.utils;

import android.text.TextUtils;

/**
 * Created by quezhongsang on 2018/1/13.
 */
public class StringUtils {

    public static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    public static String getStr(String str) {
        return getStr(str, "");
    }

    public static String getStr(Object str, String def) {
        return str == null || StringUtils.isEmpty(str.toString()) ? def : str.toString();
    }

    public static String getStr(Object object) {
        return getStr(object, "");
    }


    /**
     * 新增增了中文的空格
     * @param str
     * @return
     */
    public static String trim(String str) {
        if (isEmpty(str))
            return "";

        int len = str.length();
        int st = 0;

        while ((st < len) && (str.charAt(st) <= ' ' || str.charAt(st) == '　')) {
            st++;
        }
        while ((st < len) && (str.charAt(len - 1) <= ' ' || str.charAt(st) == '　')) {
            len--;
        }
        return ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
    }
}
