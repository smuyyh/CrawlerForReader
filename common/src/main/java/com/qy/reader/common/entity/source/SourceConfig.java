package com.qy.reader.common.entity.source;

/**
 * 默认配置
 * 可能有部分源，比较复杂，需要多个xpath，那就继承重写
 * <p>
 * Created by quezhongsang on 2018/1/7.
 */
public class SourceConfig {

    @SourceID
    public int id;

    /**
     * 搜索
     */
    public Search search;

    /**
     * 小说目录内容
     */
    public Catalog catalog;

    /**
     * 小说内容
     */
    public Content content;

    public SourceConfig(@SourceID int id) {
        this.id = id;
    }

    @SourceID
    public int getId() {
        return id;
    }

    public void setId(@SourceID int id) {
        this.id = id;
    }

    public static class Search {

        public String charset;

        public String xpath;

        public String coverXpath;

        public String titleXpath;

        public String linkXpath;

        public String authorXpath;

        public String descXpath;
    }

    public static class Catalog {
        public String xpath;

        public String titleXpath;

        public String linkXpath;
    }

    public static class Content {
        public String xpath;
    }
}
