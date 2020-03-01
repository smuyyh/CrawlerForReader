package com.qy.reader;

import android.app.Application;

import com.qy.reader.common.Global;
import com.qy.reader.common.entity.book.SearchBook;

import java.util.List;

/**
 * Created by yuyuhang on 2018/1/8.
 */
public class App extends Application {

    private static App instance;

    private List<SearchBook> bookList = null;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        Global.init(this);
    }

    public static App getInstance() {
        return instance;
    }

    public List<SearchBook> getBookList() {
        return bookList;
    }

    public void setBookList(List<SearchBook> bookList) {
        this.bookList = bookList;
    }
}
