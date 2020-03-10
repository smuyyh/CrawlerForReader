package com.qy.reader.common.base;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.qy.reader.common.R;
import com.qy.reader.common.utils.StatusBarCompat;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

/**
 * Created by yuyuhang on 2018/1/8.
 */
public class BaseActivity extends RxAppCompatActivity {

    protected BaseActivity mContext;

    protected TextView mTvBack;
    protected TextView mTvTitle;

    protected View statusBarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.mContext = this;
        super.onCreate(savedInstanceState);

        if (enableStatusBarCompat()) {
            statusBarView = StatusBarCompat.compat(this);
        }
    }

    protected void initToolbar() {
        mTvBack = findViewById(R.id.toolbar_back);
        if (enableBackIcon()) {
            mTvBack.setVisibility(View.VISIBLE);
            mTvBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            mTvBack.setVisibility(View.GONE);
        }

        mTvTitle = findViewById(R.id.toolbar_title);
        if (mTvTitle != null) {
            String title = getToolbarTitle();
            if (!TextUtils.isEmpty(title)) {
                mTvTitle.setText(title);
            }
        }
    }

    public boolean enableStatusBarCompat() {
        return true;
    }

    public boolean enableBackIcon() {
        return true;
    }

    public String getToolbarTitle() {
        return "";
    }

    protected void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        if (statusBarView != null) {
            statusBarView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    protected void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        if (statusBarView != null) {
            statusBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void invisible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
