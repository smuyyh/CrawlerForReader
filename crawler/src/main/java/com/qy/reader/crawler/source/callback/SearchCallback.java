package com.qy.reader.crawler.source.callback;

import com.qy.reader.common.entity.book.SearchBook;

import java.util.List;

/**
 * 搜索回调
 * <p>
 * Created by yuyuhang on 2018/1/8.
 */
public interface SearchCallback {

    /**
     * @param keyword    搜索词
     * @param appendList 因为有很多源，所以分批回调回去，否则搜索等待时间很长
     */
    void onResponse(String keyword, List<SearchBook> appendList);

    /**
     * 所有源都查询完回调
     */
    void onFinish();

    void onError(String msg);
}
