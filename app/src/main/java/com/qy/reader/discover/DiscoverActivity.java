package com.qy.reader.discover;

import android.support.v4.app.Fragment;

import com.qy.reader.common.base.BaseTabActivity;

/**
 * Created by yuyuhang on 2018/1/9.
 */
public class DiscoverActivity extends BaseTabActivity {

    @Override
    protected int getCurrentIndex() {
        return DISCOVER_INDEX;
    }

    @Override
    protected Fragment fragmentInstance() {
        return new DiscoverFragment();
    }
}
