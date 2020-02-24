package com.qy.reader.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qy.reader.common.entity.book.BookDetail;
import com.qy.reader.common.widgets.CornerImageView;

import org.diql.android.novel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyuhang on 2018/1/9.
 */
public class HomeFragment extends Fragment {

    protected View rootView;
    protected View commonStatusBar;
    protected TextView toolbarBack;
    protected TextView toolbarTitle;
    protected Toolbar commonToolbar;
    protected RecyclerView rvBookCase;
    protected SwipeRefreshLayout srlBookCase;

    private List<BookDetail> bookDetailList = new ArrayList<>();

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
    }

    private void initView(View rootView) {
        commonStatusBar = (View) rootView.findViewById(R.id.common_status_bar);
        toolbarBack = (TextView) rootView.findViewById(R.id.toolbar_back);
        toolbarTitle = (TextView) rootView.findViewById(R.id.toolbar_title);
        commonToolbar = (Toolbar) rootView.findViewById(R.id.common_toolbar);
        srlBookCase = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_book_case);
        rvBookCase = (RecyclerView) rootView.findViewById(R.id.rv_book_case);

        toolbarTitle.setText(R.string.book_case);

        srlBookCase.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlBookCase.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlBookCase.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvBookCase.setLayoutManager(layoutManager);
        rvBookCase.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.item_book_case, parent, false);
                return new BookcaseViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return bookDetailList.size();
            }
        });
    }
}

class BookcaseViewHolder extends RecyclerView.ViewHolder {

    protected TextView tvSearchItemSource;
    protected TextView tvSearchItemDesc;
    protected TextView tvSearchItemAuthor;
    protected TextView tvSearchItemTitle;
    protected CornerImageView ivSearchItemCover;
    protected View rootView;

    public BookcaseViewHolder(@NonNull View itemView) {
        super(itemView);
        rootView = itemView;
        initView(itemView);
    }

    private void initView(View rootView) {
        ivSearchItemCover = (CornerImageView) rootView.findViewById(R.id.iv_search_item_cover);
        tvSearchItemTitle = (TextView) rootView.findViewById(R.id.tv_search_item_title);
        tvSearchItemAuthor = (TextView) rootView.findViewById(R.id.tv_search_item_author);
        tvSearchItemDesc = (TextView) rootView.findViewById(R.id.tv_search_item_desc);
        tvSearchItemSource = (TextView) rootView.findViewById(R.id.tv_search_item_source);
    }
}
