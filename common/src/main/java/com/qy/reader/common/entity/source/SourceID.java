package com.qy.reader.common.entity.source;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        SourceID.LIEWEN,
        SourceID.CHINESE81,
        SourceID.ZHUISHU,
        SourceID.BIQUG,
        SourceID.WENXUEMI,
        SourceID.CHINESEXIAOSHUO,
        SourceID.DINGDIAN,
        SourceID.BIQUGER,
        SourceID.CHINESEZHUOBI,
        SourceID.DASHUBAO,
        SourceID.CHINESEWUZHOU,
        SourceID.UCSHUMENG,
        SourceID.QUANXIAOSHUO,
        SourceID.YANMOXUAN,
        SourceID.AIQIWENXUE,
        SourceID.QIANQIANXIAOSHUO,
        SourceID.PIAOTIANWENXUE,
        SourceID.SUIMENGXIAOSHUO,
        SourceID.DAJIADUSHUYUAN,
        SourceID.SHUQIBA,
        SourceID.XIAOSHUO52,

})
@Retention(RetentionPolicy.SOURCE)
public @interface SourceID {

    /**
     * 猎文网
     */
    int LIEWEN = 1;

    /**
     * 81中文网
     */
    int CHINESE81 = 2;

    /**
     * 追书网
     */
    int ZHUISHU = 3;

    /**
     * 笔趣阁
     */
    int BIQUG = 4;

    /**
     * 文学迷
     */
    int WENXUEMI = 5;

    /**
     * 小说中文网
     */
    int CHINESEXIAOSHUO = 6;

    /**
     * 顶点小说
     */
    int DINGDIAN = 7;

    /**
     * 笔趣阁儿
     */
    int BIQUGER = 8;

    /**
     * 着笔中文网
     */
    int CHINESEZHUOBI = 9;

    /**
     * 大书包
     */
    int DASHUBAO = 10;

    /**
     * 梧州中文台
     */
    int CHINESEWUZHOU = 11;

    /**
     * UC书盟
     */
    int UCSHUMENG = 12;

    /**
     * 全小说
     */
    int QUANXIAOSHUO = 13;

    /**
     * 衍墨轩
     */
    int YANMOXUAN = 14;

    /**
     * 爱奇文学
     */
    int AIQIWENXUE = 15;

    /**
     * 千千小说
     */
    int QIANQIANXIAOSHUO = 16;

    /**
     * 飘天文学网
     */
    int PIAOTIANWENXUE = 17;

    /**
     * 随梦小说网
     */
    int SUIMENGXIAOSHUO = 18;

    /**
     * 大家读书苑
     */
    int DAJIADUSHUYUAN = 19;

    /**
     * 书旗吧
     */
    int SHUQIBA = 20;

    /**
     * 小说52
     */
    int XIAOSHUO52 = 21;
}