package com.qy.reader.common.entity.source;

/**
 * Created by yuyuhang on 2018/1/12.
 */
public class SourceEnable {

    @SourceID
    public int id;

    public boolean enable;

    public SourceEnable(int id, boolean enable) {
        this.id = id;
        this.enable = enable;
    }
}
