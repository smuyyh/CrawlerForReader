package com.qy.reader.crawler.source.callback;

import com.qy.reader.common.entity.chapter.Chapter;

import java.util.List;

/**
 * Created by yuyuhang on 2018/1/8.
 */
public interface ChapterCallback {

    void onResponse(List<Chapter> chapters);

    void onError(String msg);
}
