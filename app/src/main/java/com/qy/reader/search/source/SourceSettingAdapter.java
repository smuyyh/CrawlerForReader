package com.qy.reader.search.source;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qy.reader.R;
import com.qy.reader.common.entity.source.Source;
import com.qy.reader.common.entity.source.SourceEnable;
import com.qy.reader.common.utils.SPUtils;
import com.qy.reader.crawler.source.SourceManager;
import com.yuyh.easyadapter.recyclerview.EasyRVAdapter;
import com.yuyh.easyadapter.recyclerview.EasyRVHolder;

import java.util.List;

/**
 * Created by yuyuhang on 2018/1/12.
 */
public class SourceSettingAdapter extends EasyRVAdapter<Source> {

    private SparseBooleanArray checkedMap = new SparseBooleanArray();

    public SourceSettingAdapter(Context context, List<Source> list) {
        super(context, list, R.layout.item_search_source_setting);
        checkedMap = SourceManager.getSourceEnableSparseArray();
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, final Source item) {
        CheckBox box = viewHolder.getView(R.id.cb_item_source_setting);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedMap.put(item.id, isChecked);
            }
        });
        box.setChecked(checkedMap.get(item.id));
        box.setText(item.name);
    }

    public SparseBooleanArray getCheckedMap() {
        return checkedMap;
    }
}
