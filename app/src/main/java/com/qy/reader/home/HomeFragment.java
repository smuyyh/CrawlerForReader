package com.qy.reader.home;


import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.utils.AssetsUtils;
import com.qy.reader.common.utils.Nav;
import com.qy.reader.common.widgets.CornerImageView;

import org.diql.android.novel.R;
import org.diql.android.novel.util.Preconditions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyuhang on 2018/1/9.
 */
public class HomeFragment extends Fragment {

    private Context context;
    protected View rootView;
    protected View commonStatusBar;
    protected TextView toolbarBack;
    protected TextView toolbarTitle;
    protected Toolbar commonToolbar;
    protected RecyclerView rvBookCase;
    protected SwipeRefreshLayout srlBookCase;

    private List<SearchBook> dataList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Type t = new TypeToken<ArrayList<SearchBook>>() {
            }.getType();
            String json = AssetsUtils.readAssetsTxt(context, "default_book_case.json");
            ArrayList<SearchBook> books = new Gson().fromJson(json, t);
            dataList.addAll(books);
        }
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

        RecyclerView.Adapter<BookcaseViewHolder> adapter = new RecyclerView.Adapter<BookcaseViewHolder>() {

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

        tvDesc.setVisibility(View.INVISIBLE);
        tvSource.setVisibility(View.INVISIBLE);
    }

    public void setData(@NonNull SearchBook data) {
        book = Preconditions.checkNotNull(data);

        tvTitle.setText(book.title);
        tvAuthor.setText(book.author);
        Glide.with(context).load(book.cover).into(ivCover);
    }
}
