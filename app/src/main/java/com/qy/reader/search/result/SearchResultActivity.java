package com.qy.reader.search.result;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.qy.reader.R;
import com.qy.reader.common.base.BaseActivity;
import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.utils.Nav;
import com.qy.reader.common.widgets.Sneaker;
import com.qy.reader.crawler.Crawler;
import com.qy.reader.crawler.source.callback.SearchCallback;
import com.qy.reader.book.BookInfoActivity;
import com.qy.reader.support.DividerItemDecoration;
import com.yuyh.easyadapter.recyclerview.EasyRVAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yuyuhang on 2018/1/9.
 */
public class SearchResultActivity extends BaseActivity {

    private RecyclerView mRvSearchResult;
    private SearchResultAdapter mAdapter;

    private List<SearchBook> mList = new ArrayList<>();

    @Override
    public String getToolbarTitle() {
        return "搜索结果";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initToolbar();

        String title = getIntent().getStringExtra("text");
        if (TextUtils.isEmpty(title)) {
            Sneaker.with(this)
                    .setTitle("搜索词不能为空哦！")
                    .sneakWarning();
            return;
        }

        mRvSearchResult = findViewById(R.id.rv_search_list);
        mRvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        mRvSearchResult.addItemDecoration(new DividerItemDecoration());

        mAdapter = new SearchResultAdapter(this, mList);
        mRvSearchResult.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new EasyRVAdapter.OnItemClickListener<SearchBook>() {
            @Override
            public void onItemClick(View view, int position, SearchBook item) {
                if (item == null)
                    return;

                Bundle bundle = new Bundle();
                bundle.putSerializable("search_book", item);
                Nav.from(SearchResultActivity.this).setExtras(bundle).start("qyreader://bookinfo");
            }
        });

        search(title);
    }

    private void search(final String title) {
        mAdapter.setTitle(title);
        Observable
                .create(new Observable.OnSubscribe<List<SearchBook>>() {
                    @Override
                    public void call(final Subscriber<? super List<SearchBook>> subscriber) {
                        Crawler.search(title, new SearchCallback() {
                            @Override
                            public void onResponse(String keyword, List<SearchBook> appendList) {
                                subscriber.onNext(appendList);
                            }

                            @Override
                            public void onFinish() {
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(String msg) {
                                subscriber.onError(new Throwable(msg));
                            }
                        });
                    }
                })
                .compose(this.<List<SearchBook>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SearchBook>>() {
                    @Override
                    public void onCompleted() {
                        Sneaker.with(SearchResultActivity.this)
                                .setTitle("搜索完毕")
                                .setMessage("共搜索到" + mList.size() + "本书")
                                .sneakSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Sneaker.with(SearchResultActivity.this)
                                .setTitle("搜索失败")
                                .setMessage(e.getMessage())
                                .sneakWarning();
                    }

                    @Override
                    public void onNext(List<SearchBook> appendList) {
                        if (appendList == null || appendList.isEmpty()) {
                            return;
                        }

                        for (SearchBook newBook : appendList) {
                            boolean exists = false;
                            for (SearchBook book : mList) {
                                if (TextUtils.equals(book.title, newBook.title)
                                        && !newBook.sources.isEmpty()) {
                                    if (TextUtils.isEmpty(book.cover) && !TextUtils.isEmpty(newBook.cover)) {
                                        book.cover = newBook.cover;
                                    }
                                    book.sources.add(newBook.sources.get(0));
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                mList.add(newBook);
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                });
    }
}
