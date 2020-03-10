package com.qy.reader.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.qy.reader.App;
import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.utils.Nav;
import com.qy.reader.common.widgets.CornerImageView;
import com.qy.reader.support.DividerItemDecoration;
import com.trello.rxlifecycle3.components.support.RxFragment;

import org.diql.android.novel.BookcaseObservableOnSubscribe;
import org.diql.android.novel.R;
import org.diql.android.novel.settings.SettingsActivity;
import org.diql.android.novel.util.Preconditions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yuyuhang on 2018/1/9.
 */
public class HomeFragment extends RxFragment {

    private Context context;
    protected View rootView;
    protected View commonStatusBar;
    protected TextView toolbarBack;
    protected TextView toolbarTitle;
    protected Toolbar commonToolbar;
    protected RecyclerView rvBookCase;
    protected SwipeRefreshLayout srlBookCase;

    private List<SearchBook> dataList = new ArrayList<>();
    private RecyclerView.Adapter<BookcaseViewHolder> adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(rootView);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(commonToolbar);
        activity.getSupportActionBar().setTitle(null);

        ObservableOnSubscribe<List<SearchBook>> source = new BookcaseObservableOnSubscribe(context);

        Observer<List<SearchBook>> observer = new Observer<List<SearchBook>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(List<SearchBook> searchBooks) {
                dataList.clear();
                dataList.addAll(searchBooks);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                srlBookCase.setRefreshing(false);
            }

            @Override
            public void onComplete() {
                srlBookCase.setRefreshing(false);
            }
        };
        Observable.create(source)
                .subscribeOn(Schedulers.io())
                .compose(this.<List<SearchBook>>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void initView(View rootView) {
        commonStatusBar = (View) rootView.findViewById(R.id.common_status_bar);
        commonToolbar = (Toolbar) rootView.findViewById(R.id.common_toolbar);
        toolbarTitle = rootView.findViewById(R.id.toolbar_title);
        srlBookCase = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_book_case);
        rvBookCase = (RecyclerView) rootView.findViewById(R.id.rv_book_case);

        toolbarTitle.setText(R.string.book_case);

        srlBookCase.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                List<SearchBook> nowList = App.getInstance().getBookList();
                if (nowList != null) {
                    dataList.clear();
                    dataList.addAll(nowList);
                    adapter.notifyDataSetChanged();
                    srlBookCase.setRefreshing(false);
                } else {
                    srlBookCase.setRefreshing(false);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvBookCase.setLayoutManager(layoutManager);
        rvBookCase.addItemDecoration(new DividerItemDecoration());

        adapter = new RecyclerView.Adapter<BookcaseViewHolder>() {

            @NonNull
            @Override
            public BookcaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.item_book_case, parent, false);
                return new BookcaseViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull final BookcaseViewHolder holder, int position) {
                SearchBook data = dataList.get(position);
                holder.setData(data);
            }

            @Override
            public int getItemCount() {
                return dataList.size();
            }
        };
        rvBookCase.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<SearchBook> bookList = App.getInstance().getBookList();
        if (bookList != null && !dataList.equals(bookList)) {
            dataList.clear();
            dataList.addAll(bookList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(context, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


class BookcaseViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    protected TextView tvSource;
    protected TextView tvDesc;
    protected TextView tvAuthor;
    protected TextView tvTitle;
    protected CornerImageView ivCover;
    protected View rootView;

    private SearchBook book;

    public BookcaseViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        rootView = itemView;
        initView(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (book == null) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("search_book", book);
                Nav.from(context).setExtras(bundle).start("novel://bookinfo");
            }
        });
    }

    private void initView(View rootView) {
        ivCover = rootView.findViewById(R.id.iv_search_item_cover);
        tvTitle = rootView.findViewById(R.id.tv_search_item_title);
        tvAuthor = rootView.findViewById(R.id.tv_search_item_author);
        tvDesc = rootView.findViewById(R.id.tv_search_item_desc);
        tvSource = rootView.findViewById(R.id.tv_search_item_source);

        tvSource.setVisibility(View.INVISIBLE);
    }

    public void setData(@NonNull SearchBook data) {
        book = Preconditions.checkNotNull(data);

        tvTitle.setText(book.title);
        tvAuthor.setText(book.author);
        tvDesc.setText(book.desc);
        Glide.with(context).load(book.cover).into(ivCover);
    }
}
