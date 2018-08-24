package com.qy.reader.common.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by quezhongsang on 2018/1/13.
 */
public class ListDialog {

    MaterialDialog materialDialog;

    public ListDialog(MaterialDialog materialDialog) {
        this.materialDialog = materialDialog;
    }

    public static class Builder {
        MaterialDialog realBuilder;
        Context context;

        public Builder(Context context) {
            this.context = context;
            realBuilder = new MaterialDialog(context);
        }

        public ListDialog.Builder setTitle(@Nullable CharSequence title) {
            realBuilder.setTitle(title);
            return this;
        }

        public ListDialog.Builder setList(final String[] titles, final OnItemClickListener onItemClickListener) {
            if (titles == null || titles.length <= 0)
                return this;
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.context, android.R.layout.simple_list_item_1);
            for (String title : titles) {
                arrayAdapter.add(title + "");
            }

            ListView listView = new ListView(context);
            listView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (8 * scale + 0.5f);
            listView.setPadding(0, dpAsPixels, 0, dpAsPixels);
            listView.setDividerHeight(0);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(realBuilder, position, titles[position]);
                    }
                    realBuilder.dismiss();
                }
            });

            realBuilder.setContentView(listView);
            realBuilder.setCanceledOnTouchOutside(true);
            return this;
        }

        public ListDialog show() {
            realBuilder.show();
            return new ListDialog(realBuilder);
        }
    }

    public void show() {
        if (materialDialog != null) {
            materialDialog.show();
        }
    }


    public void dismiss() {
        if (materialDialog != null)
            materialDialog.dismiss();
    }

    public interface OnItemClickListener {
        void onItemClick(MaterialDialog materialDialog, int position, String content);
    }
}
