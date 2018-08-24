package com.qy.reader.search;

import android.support.v4.app.Fragment;

import com.qy.reader.common.base.BaseTabActivity;

/**
 * Created by yuyuhang on 2018/1/9.
 */
public class SearchActivity extends BaseTabActivity {

    @Override
    protected int getCurrentIndex() {
        return SEARCH_INDEX;
    }

    @Override
    protected Fragment fragmentInstance() {
        return new SearchFragment();
    }
}
