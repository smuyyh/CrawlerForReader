package com.qy.reader.common.entity.chapter;

import java.io.Serializable;

/**
 * 章节
 * <p>
 * Created by yuyuhang on 2018/1/7.
 */
public class Chapter implements Serializable {

    public String title;

    public String lastUpdateTime;

    public String link;

    @Override
    public String toString() {
        return "Chapter{" +
                "title='" + title + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
