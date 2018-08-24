package com.qy.reader.crawler.xpath.core;


public class SingletonProducer {
    private static SingletonProducer producer = new SingletonProducer();
    private AxisSelector axisSelector = new AxisSelector();
    private Functions functions = new Functions();

    private SingletonProducer() {
    }

    public static SingletonProducer getInstance() {
        return producer;
    }

    public AxisSelector getAxisSelector() {
        return axisSelector;
    }

    public Functions getFunctions() {
        return functions;
    }
}
