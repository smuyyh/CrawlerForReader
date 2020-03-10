package com.qy.reader.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.qy.reader.common.base.BaseActivity;
import com.qy.reader.common.utils.Nav;
import com.qy.reader.common.utils.StatusBarCompat;

import org.diql.android.novel.R;

/**
 * Created by yuyuhang on 2018/1/8.
 */
public class SplashActivity extends BaseActivity {

    private final int requestCode = 1;
    private final int delayMillis = 500;
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

        String writeExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int permission = ContextCompat.checkSelfPermission(this, writeExternalStorage);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            mTvSkip.postDelayed(runnable, delayMillis);
        } else {
            mTvSkip.setVisibility(View.INVISIBLE);
            ActivityCompat.requestPermissions(this, new String[] {writeExternalStorage}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == this.requestCode) {
            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                end();
            } else {
                finish();
            }
        }
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
