package com.qy.reader.common.widgets.reader;

import android.text.TextUtils;

import com.qy.reader.common.entity.source.SourceID;
import com.qy.reader.common.utils.FileIOUtils;
import com.qy.reader.common.utils.FileUtils;

import java.io.File;

/**
 * Created by yuyuhang on 2018/1/11.
 */
public class BookManager {

    private static BookManager instance;

    public static BookManager getInstance() {
        if (instance == null) {
            synchronized (BookManager.class) {
                instance = new BookManager();
            }
        }
        return instance;
    }

    public String getBookNum(String title, String author) {
        StringBuilder result = new StringBuilder();
        if (!TextUtils.isEmpty(title)) {
            result.append(title.trim());
        }
        result.append("||");
        if (!TextUtils.isEmpty(author)) {
            result.append(author.trim());
        }
        return result.toString();
    }

    public File getContentFile(@SourceID int sourceId, String bookNum, String chapterName) {
        String path = FileUtils.createBookRootPath() + File.separator
                + sourceId + File.separator
                + bookNum + File.separator + chapterName;
        File file = new File(path);
        if (!file.exists()) {
            FileUtils.createFile(file);
        }

        return file;
    }

    public boolean saveContentFile(@SourceID int sourceId, String bookNum, String chapterName, String content) {
        File file = getContentFile(sourceId, bookNum, chapterName);
        return FileIOUtils.writeFileFromString(file, content);
    }
}
