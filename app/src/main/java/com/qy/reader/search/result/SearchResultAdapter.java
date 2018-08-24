package com.qy.reader.search.result;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qy.reader.R;
import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.entity.source.Source;
import com.qy.reader.crawler.source.SourceManager;
import com.yuyh.easyadapter.recyclerview.EasyRVAdapter;
import com.yuyh.easyadapter.recyclerview.EasyRVHolder;

import java.util.List;

/**
 * Created by yuyuhang on 2018/1/11.
 */
public class SearchResultAdapter extends EasyRVAdapter<SearchBook> {

    private String title;

    public SearchResultAdapter(Context context, List<SearchBook> list) {
        super(context, list, R.layout.item_search_list);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, SearchBook item) {

        TextView tvTitle = viewHolder.getView(R.id.tv_search_item_title);
        tvTitle.setText(changeTxtColor(item.title, title, 0xFFF08080));

        TextView tvAuthor = viewHolder.getView(R.id.tv_search_item_author);
        tvAuthor.setText(changeTxtColor(item.author, title, 0xFFF08080));

        TextView tvDesc = viewHolder.getView(R.id.tv_search_item_desc);
        tvDesc.setText(changeTxtColor(item.desc, title, 0xFFF08080));

        TextView tvSource = viewHolder.getView(R.id.tv_search_item_source);
        StringBuilder builder = new StringBuilder();
        for (SearchBook.SL sl : item.sources) {
            if (sl.source != null) {
                Source source = SourceManager.SOURCES.get(sl.source.id);
                if (source != null) {
                    if (builder.length() > 0) {
                        builder.append(" | ");
                    }
                    builder.append(source.name);
                }
            }
        }
        tvSource.setText(builder.toString());

        ImageView ivCover = viewHolder.getView(R.id.iv_search_item_cover);
        Glide.with(mContext).load(item.cover).into(ivCover);
    }

    public SpannableString changeTxtColor(String content, String splitText, int color) {
        int start = 0, end;
        SpannableString result = new SpannableString(content = (content == null ? "" : content));
        if (TextUtils.isEmpty(splitText)) {
            return result;
        }
        if (!TextUtils.isEmpty(splitText) && (content.length() >= splitText.length())) {
            while ((start = content.indexOf(splitText, start)) >= 0) {
                end = start + splitText.length();
                result.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                start = end;
            }
        }
        return result;
    }
}
