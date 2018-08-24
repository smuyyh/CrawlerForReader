package com.qy.reader.crawler.xpath.util;

/**
 * 筛选作用域
 */
public enum ScopeEm {
    INCHILREN("/"),   //默认只在子代中筛选,有轴时由轴定义筛选域
    RECURSIVE("//"),  //向下递归查找
    CUR("./"),        //当前节点下
    CURREC(".//");    //当前节点向下递归
    private String val;

    private ScopeEm(String type) {
        this.val = type;
    }

    public String val() {
        return this.val;
    }
}
