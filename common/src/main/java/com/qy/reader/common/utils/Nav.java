package com.qy.reader.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.net.URI;
import java.net.URLDecoder;

/**
 * 页面路由跳转
 * <p>
 * Created by yuyuhang on 2018/1/13.
 */
public class Nav {

    private static final int INVALID = -1;

    private Context mContext;

    private Bundle mBundle;
    private int flags = INVALID;
    private int inAnim = INVALID, outAnim = INVALID;

    public Nav(Context context) {
        this.mContext = context;
        mBundle = new Bundle();
    }

    public static Nav from(Context context) {
        return new Nav(context);
    }

    public Nav setExtras(Bundle bundle) {
        if (bundle != null) {
            mBundle.putAll(bundle);
        }
        return this;
    }

    public Nav setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public Nav overridePendingTransition(int enterAnim, int exitAnim) {
        this.inAnim = enterAnim;
        this.outAnim = exitAnim;
        return this;
    }

    public void start(String url) {
        start(url, -1);
    }

    public void start(String url, int reqCode) {
        if (TextUtils.isEmpty(url)) {
            recycle();
            return;
        }

        try {
            URI uri = new URI(url);

            String schema = uri.getScheme();
            String query = uri.getQuery();
            if (!TextUtils.isEmpty(query)) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] kv = param.split("=");
                    if (kv.length == 2) {
                        String key = kv[0];
                        String value = kv[1];
                        mBundle.putString(key, URLDecoder.decode(value, "UTF-8"));
                    }
                }
            }

            if (schema.equals("http") || schema.equals("https")) {

            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.putExtras(mBundle);
                if (flags >= 0) {
                    intent.setFlags(flags);
                }
                if (reqCode >= 0 && mContext instanceof Activity) {
                    ((Activity) mContext).startActivityForResult(intent, reqCode);
                } else {
                    mContext.startActivity(intent);
                }
                if (mContext instanceof Activity && (inAnim >= 0 || outAnim >= 0)) {
                    ((Activity) mContext).overridePendingTransition(inAnim, outAnim);
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            recycle();
        }
    }

    private void recycle() {
        mContext = null;
    }
}
