package com.qy.reader;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.entity.chapter.Chapter;
import com.qy.reader.common.utils.StatusBarCompat;
import com.qy.reader.crawler.Crawler;
import com.qy.reader.crawler.source.SourceManager;
import com.qy.reader.crawler.source.callback.ChapterCallback;
import com.qy.reader.crawler.source.callback.ContentCallback;

import org.diql.android.novel.R;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarCompat.compat(this);

        setContentView(R.layout.activity_main);
        final TextView textView = findViewById(R.id.tv_Content);

        new Thread(new Runnable() {
            @Override
            public void run() {
//                Crawler.search("你好", new SearchCallback() {
//                    @Override
//                    public void onResponse(String keyword, List<SearchBook> appendList) {
//                        Log.e("key" + keyword, "" + appendList);
//                    }
//
//                    @Override
//                    public void onFinish() {
//
//                    }
//
//                    @Override
//                    public void onError(String msg) {
//
//                    }
//                });
                Crawler.catalog(new SearchBook.SL("https://www.liewen.cc/b/24/24934/", SourceManager.SOURCES.get(1)), new ChapterCallback() {
                    @Override
                    public void onResponse(List<Chapter> chapters) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                });

                Crawler.content(new SearchBook.SL("https://www.liewen.cc/b/24/24934/", SourceManager.SOURCES.get(1)), "/b/24/24934/12212511.html", new ContentCallback() {
                    @Override
                    public void onResponse(String content) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
            }
        }).start();


    }
}
