package com.qy.reader.discover;

import androidx.fragment.app.Fragment;

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
