package com.qy.reader.common.widgets.reader.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        FontType.SIMPLIFIED,
        FontType.TRADITIONAL,

})
@Retention(RetentionPolicy.SOURCE)
public @interface FontType {

    /**
     * 简体
     */
    int TRADITIONAL = 0;

    /**
     * 繁体
     */
    int SIMPLIFIED = 1;
}