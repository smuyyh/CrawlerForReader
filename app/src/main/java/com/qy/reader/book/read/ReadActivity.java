package com.qy.reader.book.read;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.qy.reader.common.base.BaseActivity;
import com.qy.reader.common.entity.book.SearchBook;
import com.qy.reader.common.entity.chapter.Chapter;
import com.qy.reader.common.widgets.Sneaker;
import com.qy.reader.common.widgets.reader.BookManager;
import com.qy.reader.common.widgets.reader.OnPageStateChangedListener;
import com.qy.reader.common.widgets.reader.ReadView;
import com.qy.reader.common.widgets.reader.SettingManager;

import org.diql.android.novel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyuhang on 2018/1/13.
 */
public class ReadActivity extends BaseActivity implements ReadContract.View {

    private ReadView mReadView;

    private RelativeLayout mRlTopBar;
    private LinearLayout mLLBottomBar;

    private Animation showReadBarAnim, hideReadBarAnim;

    private List<Chapter> mChapterList = new ArrayList<>();
    private SearchBook.SL mSource;
    private SearchBook mBook;
    private String mBookNum;

    private int currentChapter = 1, currentPage = 1;

    private ReadPresenter mPresenter;

    @Override
    public boolean enableStatusBarCompat() {
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 切换横竖屏，部分对象需要重新初始化
        if (mReadView != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
                mReadView.initScreenSize();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
                mReadView.initScreenSize();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        initData();

        initView();

        initChapter();
    }

    private void initData() {
        mPresenter = new ReadPresenter(this);

        showReadBarAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        hideReadBarAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out);
    }

    private void initView() {
        mReadView = findViewById(R.id.read_view);
        mReadView.setOnPageStateChangedListener(new PagerListener());
        mRlTopBar = findViewById(R.id.rl_book_read_top);
        mLLBottomBar = findViewById(R.id.ll_book_read_bottom);

        SettingManager.setTextSizeSP(22);
    }

    private void initChapter() {
        List<Chapter> list = (List<Chapter>) getIntent().getSerializableExtra("chapter_list");
        if (list == null) {
            Sneaker.with(this).setTitle("章节加载失败").setMessage("chapter_list不能为空").sneakWarning();
            return;
        }
        mChapterList.addAll(list);

        mBook = (SearchBook) getIntent().getSerializableExtra("book");
        mSource = (SearchBook.SL) getIntent().getSerializableExtra("source");
        mBookNum = BookManager.getInstance().getBookNum(mBook.title, mBook.author);

        Chapter chapter = (Chapter) getIntent().getSerializableExtra("chapter");

        int chapterIndex = 0;
        if (chapter != null) {
            for (int i = 0; i < mChapterList.size(); i++) {
                Chapter c = mChapterList.get(i);
                if (TextUtils.equals(c.link, chapter.link)) {
                    chapterIndex = i;
                    break;
                }
            }
        }
        currentChapter = chapterIndex + 1;

        getChapterContent(chapterIndex);
    }

    private void getChapterContent(int chapterIndex) {
        mPresenter.getChapterContent(mBookNum, mSource, mChapterList.get(chapterIndex));
    }

    @Override
    public void showContent(Chapter chapter, String content) {
        if (!mReadView.isInit() && currentChapter == mChapterList.indexOf(chapter) + 1) {
            int pageIndex = 1;
            mReadView.initChapterList(mSource.source.id, mBookNum, mChapterList, currentChapter, pageIndex);
        }
        mReadView.postInvalidate();
    }

    private void hideReadBar() {
        gone(mRlTopBar, mLLBottomBar);
        mRlTopBar.startAnimation(hideReadBarAnim);
        mLLBottomBar.startAnimation(hideReadBarAnim);
    }

    private void showReadBar() {
        visible(mRlTopBar, mLLBottomBar);
        mRlTopBar.startAnimation(showReadBarAnim);
        mLLBottomBar.startAnimation(showReadBarAnim);
    }

    private void toggleReadBar() {
        if (isVisible(mRlTopBar) || isVisible(mLLBottomBar)) {
            hideReadBar();
        } else {
            showReadBar();
        }
    }

    private class PagerListener implements OnPageStateChangedListener {

        @Override
        public void onCenterClick() {
            toggleReadBar();
        }

        @Override
        public void onChapterChanged(int currentChapter, int fromChapter, boolean fromUser) {
            ReadActivity.this.currentChapter = currentChapter;
            for (int i = currentChapter - 1; i <= mChapterList.size() && i <= currentChapter + 3; i++) {
                if (i > 0) {
                    getChapterContent(i - 1);
                }
            }
        }

        @Override
        public void onPageChanged(int currentPage, int currentChapter) {
            ReadActivity.this.currentPage = currentPage;
            if (isVisible(mRlTopBar)) {
                hideReadBar();
            }
        }

        @Override
        public void onChapterLoadFailure(int currentChapter) {
            int size = mChapterList.size();
            for (int i = currentChapter - 1; i <= size && i <= currentChapter + 3; i++) {
                if (i > 0) {
                    getChapterContent(i - 1);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.release();
    }
}
