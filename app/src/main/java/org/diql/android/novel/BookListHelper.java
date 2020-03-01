package org.diql.android.novel;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.utils.AssetsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class BookListHelper {

    public BookListHelper() {
    }

    public ArrayList<SearchBook> loadDefaultBookList(Context context) {
        context = context.getApplicationContext();
        Type t = new TypeToken<ArrayList<SearchBook>>() {
        }.getType();
        String fileName = "default_book_case.json";
        String json = AssetsUtils.readAssetsTxt(context, fileName);
        return new Gson().fromJson(json, t);
    }
}
