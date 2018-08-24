package com.qy.reader.mine;

import android.support.v4.app.Fragment;

import com.qy.reader.common.base.BaseTabActivity;

/**
 * Created by yuyuhang on 2018/1/9.
 */
public class MineActivity extends BaseTabActivity {

    @Override
    protected int getCurrentIndex() {
        return MINE_INDEX;
    }

    @Override
    protected Fragment fragmentInstance() {
        return new MineFragment();
    }
}
