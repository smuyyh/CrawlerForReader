package org.diql.android.novel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qy.reader.common.base.BaseActivity;

public class VersionActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, VersionActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        initToolbar();
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.menu_version_title);
    }
}
