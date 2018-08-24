package com.qy.reader.common.entity.book;

import com.qy.reader.common.entity.source.Source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果（搜索时，书名和作者名一样时，认为是同一本书，合并起来，同时增加一个源）
 * <p>
 * Created by yuyuhang on 2018/1/7.
 */
public class SearchBook implements Serializable{

    /**
     * 封面图
     */
    public String cover;

    /**
     * 书名
     */
    public String title;

    /**
     * 作者名
     */
    public String author;

    /**
     * 描述
     */
    public String desc;

    /**
     * 源&目录列表链接
     */
    public List<SL> sources = new ArrayList<>();

    /**
     * 源和对应链接
     */
    public static class SL implements Serializable{
        public String link;

        public Source source;

        public SL(String link, Source source) {
            this.link = link;
            this.source = source;
        }
    }
}
