package com.qy.reader.common.widgets.reader.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        ChapterType.PREVIOUS,
        ChapterType.CURRENT,
        ChapterType.NEXT
})
@Retention(RetentionPolicy.SOURCE)
public @interface ChapterType {

    /**
     * 上一章
     */
    int PREVIOUS = 1;
    /**
     * 当前章
     */
    int CURRENT = 2;
    /**
     * 下一章
     */
    int NEXT = 3;

}