package com.qy.reader.common.widgets.reader.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        DensityLevel.LEVEL_1,
        DensityLevel.LEVEL_2,
        DensityLevel.LEVEL_3
})
@Retention(RetentionPolicy.SOURCE)
public @interface DensityLevel {
    int LEVEL_1 = 20;
    int LEVEL_2 = 14;
    int LEVEL_3 = 8;
}