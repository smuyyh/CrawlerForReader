package com.qy.reader.crawler.xpath.core;

import com.qy.reader.crawler.xpath.model.Node;

import java.util.LinkedList;

public class XContext {
    public LinkedList<Node> xpathTr;

    public XContext() {
        if (xpathTr == null) {
            xpathTr = new LinkedList<Node>();
        }
    }
}
