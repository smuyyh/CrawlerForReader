package com.qy.reader.book.read;

import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.entity.chapter.Chapter;

/**
 * Created by yuyuhang on 2018/1/14.
 */
public interface ReadContract {

    interface Presenter {
        void getChapterContent(String bookNum, SearchBook.SL source, Chapter chapter);
    }

    interface View {
        void showContent(Chapter chapter, String content);
    }
}
