package org.diql.android.novel;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.entity.source.SourceID;
import com.qy.reader.common.utils.AssetsUtils;
import com.qy.reader.common.utils.FileIOUtils;
import com.qy.reader.common.utils.FileUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class BookListHelper {
    Type type = new TypeToken<ArrayList<SearchBook>>() {
    }.getType();

    public BookListHelper() {
    }

    public ArrayList<SearchBook> loadBookList(Context context) {
        String data = loadBookListFromFile();
        if (data == null || TextUtils.isEmpty(data)) {
            data = loadDefaultBookList(context);
        }
        return new Gson().fromJson(data, type);
    }

    private String loadDefaultBookList(Context context) {
        context = context.getApplicationContext();
        String fileName = "default_book_case.json";
        String json = AssetsUtils.readAssetsTxt(context, fileName);
        return json;
    }

    /**
     * 存储书架内容.
     * @param list 书架书单;
     * @return 成功/失败;
     */
    public boolean saveBookList(List<SearchBook> list) {
        File file = getBookListFile();
        return FileIOUtils.writeFileFromString(file, new Gson().toJson(list));
    }

    private String loadBookListFromFile() {
        File file = getBookListFile();
        return FileIOUtils.readFile2String(file);
    }

    public File getBookListFile() {
        String path = FileUtils.createBookRootPath() + File.separator
                + "bookcase";
        File file = new File(path);
        if (!file.exists()) {
            FileUtils.createFile(file);
        }
        return file;
    }
}
