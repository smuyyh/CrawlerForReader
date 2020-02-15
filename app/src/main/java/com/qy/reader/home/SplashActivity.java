package com.qy.reader.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.qy.reader.common.base.BaseActivity;
import com.qy.reader.common.utils.Nav;
import com.qy.reader.common.utils.StatusBarCompat;

import org.diql.android.novel.R;

/**
 * Created by yuyuhang on 2018/1/8.
 */
public class SplashActivity extends BaseActivity {

    private TextView mTvSkip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarCompat.compatTransNavigationBar(this);

        setContentView(R.layout.activity_splash);

        mTvSkip = findViewById(R.id.tv_splash_skip);
        mTvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end();
            }
        });

        mTvSkip.postDelayed(runnable, 500);

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            end();
        }
    };

    private void end() {
        Nav.from(this).start("novel://home");

        finish();
    }

    @Override
    public void finish() {
        mTvSkip.removeCallbacks(runnable);
        super.finish();
    }
}
