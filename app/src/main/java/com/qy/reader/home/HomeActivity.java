package com.qy.reader.home;

import android.support.v4.app.Fragment;

import com.qy.reader.common.base.BaseTabActivity;

/**
 * Created by yuyuhang on 2018/1/9.
 */
public class HomeActivity extends BaseTabActivity {

    @Override
    protected int getCurrentIndex() {
        return HOME_INDEX;
    }

    @Override
    protected Fragment fragmentInstance() {
        return new HomeFragment();
    }
}
