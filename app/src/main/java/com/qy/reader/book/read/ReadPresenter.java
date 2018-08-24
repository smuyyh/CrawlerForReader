package com.qy.reader.book.read;

import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.entity.chapter.Chapter;
import com.qy.reader.common.utils.HandlerFactory;
import com.qy.reader.common.widgets.reader.BookManager;
import com.qy.reader.crawler.Crawler;
import com.qy.reader.crawler.source.callback.ContentCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yuyuhang on 2018/1/14.
 */
public class ReadPresenter implements ReadContract.Presenter {

    private static final int mCorePoolSize = 1;
    private static final int mMaximumPoolSize = 1;
    private static final long mKeepAliveTime = 5L;
    private ThreadPoolExecutor mPool;

    private ReadContract.View view;

    public ReadPresenter(ReadContract.View view) {
        this.view = view;
        mPool = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, mKeepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void getChapterContent(final String bookNum, final SearchBook.SL source, final Chapter chapter) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Crawler.content(source, chapter.link, new ContentCallback() {
                    @Override
                    public void onResponse(final String content) {
                        BookManager.getInstance().saveContentFile(source.source.id, bookNum, chapter.title, content);
                        HandlerFactory.getUIHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                view.showContent(chapter, content);
                            }
                        });
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
            }
        };
        mPool.execute(runnable);
    }

    public void release() {
        if (mPool != null) {
            if (!mPool.isShutdown() || mPool.isTerminating()) {
                mPool.shutdownNow();
            }
            mPool.getQueue().clear();
        }
        mPool = null;
    }
}
