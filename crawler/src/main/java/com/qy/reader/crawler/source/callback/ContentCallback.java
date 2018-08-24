package com.qy.reader.crawler.source.callback;

/**
 * Created by yuyuhang on 2018/1/8.
 */
public interface ContentCallback {

    void onResponse(String content);

    void onError(String msg);
}
