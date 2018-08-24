package com.qy.reader.crawler.xpath.exception;

/**
 * 使用不存在的轴语法则抛出此异常
 */
public class NoSuchAxisException extends Exception {
    public NoSuchAxisException(String msg) {
        super(msg);
    }
}
