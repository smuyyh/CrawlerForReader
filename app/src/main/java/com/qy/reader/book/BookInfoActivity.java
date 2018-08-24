package com.qy.reader.book;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qy.reader.R;
import com.qy.reader.common.base.BaseActivity;
import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.entity.chapter.Chapter;
import com.qy.reader.common.entity.source.Source;
import com.qy.reader.common.utils.Nav;
import com.qy.reader.common.utils.StringUtils;
import com.qy.reader.common.widgets.ListDialog;
import com.qy.reader.common.widgets.Sneaker;
import com.qy.reader.crawler.Crawler;
import com.qy.reader.crawler.source.callback.ChapterCallback;
import com.yuyh.easyadapter.recyclerview.EasyRVAdapter;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by quezhongsang on 2018/1/13.
 */

public class BookInfoActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private BookInfoAdapter mBookInfoAdapter;
    private ArrayList<Chapter> mChapterList = new ArrayList<>();

    private List<SearchBook.SL> mSourceList = new ArrayList<>();

    private TextView mTvBookSource;
    private TextView mTvBookNewestChapter;
    private TextView mTvBookOrderBy;

    private String mStrDesc = "倒序";
    private String mStrAsc = "正序";

    private SearchBook mSearchBook;

    private int mCurrentSourcePosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        initToolbar();

        mSearchBook = (SearchBook) getIntent().getSerializableExtra("search_book");
        if (mSearchBook == null) {
            Sneaker.with(mContext)
                    .setTitle("加载失败")
                    .setMessage("search_book不能为空")
                    .sneakWarning();
            return;
        }

        initView();
    }

    @Override
    public String getToolbarTitle() {
        return "书籍详情";
    }

    private void initView() {
        if (mSearchBook.sources != null) {
            mSourceList.addAll(mSearchBook.sources);
        }

        mRecyclerView = findViewById(R.id.rv_search_list);
        //book
        Glide.with(this).load(mSearchBook.cover).into((ImageView) findViewById(R.id.iv_book_cover));
        ((TextView) findViewById(R.id.tv_book_title)).setText(StringUtils.getStr(mSearchBook.title));
        ((TextView) findViewById(R.id.tv_book_author)).setText(StringUtils.getStr(mSearchBook.author));
        ((TextView) findViewById(R.id.tv_book_desc)).setText(StringUtils.getStr(mSearchBook.desc));

        mTvBookNewestChapter = findViewById(R.id.tv_book_newest_chapter);
        mTvBookSource = findViewById(R.id.tv_book_source);
        mTvBookOrderBy = findViewById(R.id.tv_order_by);

        //list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBookInfoAdapter = new BookInfoAdapter(mContext, mChapterList);
        mRecyclerView.setAdapter(mBookInfoAdapter);
        mBookInfoAdapter.setOnItemClickListener(new EasyRVAdapter.OnItemClickListener<Chapter>() {
            @Override
            public void onItemClick(View view, int position, Chapter item) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("book", mSearchBook);
                bundle.putSerializable("chapter_list", mChapterList);
                bundle.putSerializable("chapter", item);
                bundle.putSerializable("source", mSourceList.get(mCurrentSourcePosition));
                Nav.from(mContext).setExtras(bundle).start("qyreader://read");
            }
        });

        findViewById(R.id.tv_book_source).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSourceList.size() == 0)
                    return;
                String[] items = new String[mSourceList.size()];
                for (int i = 0; i < items.length; i++) {
                    items[i] = mSourceList.get(i).source.name;
                }
                new ListDialog.Builder(mContext)
                        .setList(items, new ListDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(MaterialDialog materialDialog, int position, String content) {
                                if (mCurrentSourcePosition != position) {
                                    mCurrentSourcePosition = position;
                                    requestNet();
                                }
                            }
                        })
                        .show();
            }
        });

        mTvBookOrderBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mTvBookOrderBy.getText().toString();
                if (mStrAsc.equalsIgnoreCase(str)) {//倒序
                    mBookInfoAdapter.orderByDesc();
                    mTvBookOrderBy.setText(mStrDesc);
                } else if (mStrDesc.equalsIgnoreCase(str)) {//正序
                    mBookInfoAdapter.orderByAsc();
                    mTvBookOrderBy.setText(mStrAsc);
                }
            }
        });

        requestNet();
    }

    private void requestNet() {
        final SearchBook.SL sl = getSL();
        if (sl == null) {
            return;
        }
        mTvBookSource.setText(getSourceStr());

        mBookInfoAdapter.clear();
        mTvBookOrderBy.setText("加载中...");
        Observable
                .create(new Observable.OnSubscribe<List<Chapter>>() {
                    @Override
                    public void call(final Subscriber<? super List<Chapter>> subscriber) {
                        Crawler.catalog(sl, new ChapterCallback() {
                            @Override
                            public void onResponse(List<Chapter> chapters) {
                                subscriber.onNext(chapters);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(String msg) {
                                subscriber.onError(new Throwable(msg));
                            }
                        });
                    }
                })
                .compose(this.<List<Chapter>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Chapter>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Sneaker.with(mContext)
                                .setTitle("加载失败")
                                .setMessage(e.getMessage())
                                .sneakWarning();

                        mTvBookOrderBy.setText("加载失败");
                    }

                    @Override
                    public void onNext(List<Chapter> chapters) {
                        if (chapters == null || chapters.isEmpty()) {
                            return;
                        }
                        mBookInfoAdapter.addAll(chapters);

                        //set  最新章节
                        Chapter lastChapter = chapters.get(chapters.size() - 1);
                        if (lastChapter != null) {
                            mTvBookNewestChapter.setText("最新章节：" + StringUtils.getStr(lastChapter.title));
                        }

                        //set 排序名字
                        if (mBookInfoAdapter.isAsc()) {
                            mTvBookOrderBy.setText(mStrAsc);
                        } else {
                            mTvBookOrderBy.setText(mStrDesc);
                        }
                    }
                });
    }


    private String getSourceStr() {
        String sourceStr = "来源(" + mSearchBook.sources.size() + ")：";
        if (mCurrentSourcePosition < mSearchBook.sources.size()) {
            SearchBook.SL sl = mSearchBook.sources.get(mCurrentSourcePosition);
            if (sl != null && sl.source != null) {
                sourceStr += StringUtils.getStr(sl.source.name);
            }
        }

        return sourceStr;
    }

    private SearchBook.SL getSL() {
        if (mCurrentSourcePosition < mSearchBook.sources.size()) {
            return mSearchBook.sources.get(mCurrentSourcePosition);
        }
        return null;
    }
}
