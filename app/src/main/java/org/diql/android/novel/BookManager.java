package org.diql.android.novel;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.utils.AssetsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BookManager {
    private volatile static BookManager instance;

    private List<SearchBook> bookList = new ArrayList<>();

    private volatile boolean bookListLoaded;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private List<Callback> callbackList = new ArrayList<>(1);

    public static BookManager getInstance() {
        if (instance == null) {
            synchronized (BookManager.class) {
                if (instance == null) {
                    instance = new BookManager();
                }
            }
        }
        return instance;
    }

    private BookManager() {
    }

    public synchronized void load(Context context) {
        context = context.getApplicationContext();
        if (bookListLoaded) {
            notifyLoaded(bookList);
        } else {
            Type t = new TypeToken<ArrayList<SearchBook>>() {
            }.getType();
            String json = AssetsUtils.readAssetsTxt(context, "default_book_case.json");
            ArrayList<SearchBook> books = new Gson().fromJson(json, t);
            notifyLoaded(books);
            bookListLoaded = true;
        }
    }

    private void notifyLoaded(List<SearchBook> list) {
        if (list != this.bookList) {
            this.bookList.clear();
            this.bookList.addAll(list);
        }

        for (Callback callback : callbackList) {
            callback.onBookListLoaded(this.bookList);
        }
    }

    public void addCallback(Callback callback) {
        if (callback != null) {
            callbackList.add(callback);
        }
    }

    public void removeCallback(Callback callback) {
        if (callback != null) {
            callbackList.remove(callback);
        }
    }

    public interface Callback {
        void onBookListLoaded(List<SearchBook> bookList);
    }
}
