package com.qy.reader.common.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.qy.reader.common.R;
import com.qy.reader.common.utils.Nav;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyuhang on 2018/1/9.
 */
public abstract class BaseTabActivity extends BaseActivity implements View.OnClickListener {

    protected static final int HOME_INDEX = 0;
    protected static final int SEARCH_INDEX = 1;
    protected static final int DISCOVER_INDEX = 2;
    protected static final int MINE_INDEX = 3;

    protected Fragment mFragment;

    protected TextView mTvHome, mTvSearch, mTvDiscover, mTvMine;

    private List<TAB> mTabList = new ArrayList<>();

    private static int currentIndex = -1;

    protected int pageIndex = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initContentView();
    }

    private void initContentView() {
        pageIndex = getCurrentIndex();
        mFragment = fragmentInstance();

        setContentView(R.layout.activity_base_tab);

        mTvHome = findViewById(R.id.tv_tab_home);
        mTvSearch = findViewById(R.id.tv_tab_search);
        mTvDiscover = findViewById(R.id.tv_tab_discover);
        mTvMine = findViewById(R.id.tv_tab_mine);

        mTvHome.setOnClickListener(this);
        mTvSearch.setOnClickListener(this);
        mTvDiscover.setOnClickListener(this);
        mTvMine.setOnClickListener(this);

        mTabList.clear();
        mTabList.add(new TAB(mTvHome, "qyreader://home"));
        mTabList.add(new TAB(mTvSearch, "qyreader://search"));
        mTabList.add(new TAB(mTvDiscover, "qyreader://discover"));
        mTabList.add(new TAB(mTvMine, "qyreader://mine"));

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_tab_content, mFragment).commitNowAllowingStateLoss();
    }

    protected abstract int getCurrentIndex();

    protected abstract Fragment fragmentInstance();

    @Override
    protected void onResume() {
        super.onResume();
        currentIndex = pageIndex;
        for (int i = 0; i < mTabList.size(); i++) {
            mTabList.get(i).tabView.setSelected(i == currentIndex);
        }
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < mTabList.size(); i++) {
            TAB tab = mTabList.get(i);
            if (tab.tabView == v) {
                if (currentIndex != i) {
                    Nav.from(this)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            .overridePendingTransition(0, 0)
                            .start(tab.url);
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public static class TAB {

        View tabView;

        String url;

        public TAB(View tabView, String url) {
            this.tabView = tabView;
            this.url = url;
        }
    }
}
