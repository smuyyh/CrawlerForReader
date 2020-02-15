package com.qy.reader.search.source;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qy.reader.common.base.BaseActivity;
import com.qy.reader.common.entity.source.Source;
import com.qy.reader.crawler.source.SourceManager;
import com.qy.reader.support.DividerItemDecoration;

import org.diql.android.novel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyuhang on 2018/1/12.
 */
public class SourceSettingActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private List<Source> mList = new ArrayList<>();
    private SourceSettingAdapter mAdapter;

    @Override
    public String getToolbarTitle() {
        return "搜索源";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_source_setting);
        initToolbar();

        mRecyclerView = findViewById(R.id.rv_source_setting_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration());
        for (int i = 0; i < SourceManager.SOURCES.size(); i++) {
            mList.add(SourceManager.SOURCES.valueAt(i));
        }
        mAdapter = new SourceSettingAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SourceManager.saveSourceEnable(mAdapter.getCheckedMap());
    }
}
