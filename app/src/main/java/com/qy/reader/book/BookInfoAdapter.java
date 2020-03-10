package com.qy.reader.book;

import android.content.Context;
import android.widget.TextView;

import com.qy.reader.common.entity.chapter.Chapter;
import com.qy.reader.common.utils.StringUtils;
import com.yuyh.easyadapter.recyclerview.EasyRVAdapter;
import com.yuyh.easyadapter.recyclerview.EasyRVHolder;

import org.diql.android.novel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quezhongsang on 2018/1/13.
 */
public class BookInfoAdapter extends EasyRVAdapter<Chapter> {

    /**
     * 章节是否为正序;
     */
    private boolean mIsAsc = true;

    public BookInfoAdapter(Context context, List<Chapter> list) {
        super(context, list, R.layout.item_book_chapter_list);
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, Chapter item) {
        TextView textView = viewHolder.getView(R.id.tv_content);
        if (item != null) {
            textView.setText(StringUtils.getStr(item.title));
        } else {
            textView.setText("");
        }
    }

    public void orderByDesc() {
        if (mIsAsc) {
            transformData();
            mIsAsc = false;
        }
    }

    public void orderByAsc() {
        if (!mIsAsc) {
            transformData();
            mIsAsc = true;
        }

    }

    private void transformData() {
        if (mList != null) {
            List<Chapter> chapterList = new ArrayList<>(mList.size());
            for (int i = mList.size() - 1; i >= 0; i--) {
                chapterList.add(mList.get(i));
            }
            mList.clear();
            mList.addAll(chapterList);
            notifyDataSetChanged();
        }
    }

    public boolean isAsc() {
        return mIsAsc;
    }

    @Override
    public void clear() {
        clear(true);
    }

    public void clear(boolean asc) {
        mIsAsc = asc;
        super.clear();
    }
}
