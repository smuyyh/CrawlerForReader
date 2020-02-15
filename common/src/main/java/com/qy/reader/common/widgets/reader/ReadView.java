package com.qy.reader.common.widgets.reader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.qy.reader.common.entity.chapter.Chapter;
import com.qy.reader.common.entity.source.SourceID;
import com.qy.reader.common.utils.BitmapUtils;
import com.qy.reader.common.utils.ScreenUtils;
import com.qy.reader.common.widgets.reader.annotation.ChapterType;
import com.qy.reader.common.widgets.reader.annotation.DrawPageType;
import com.qy.reader.common.widgets.reader.annotation.SlideMode;

import java.util.List;

import static android.graphics.Bitmap.createBitmap;

/**
 * Created by yuyuhang on 2018/1/11.
 */
public class ReadView extends View {

    protected int mScreenWidth;
    protected int mScreenHeight;

    protected Bitmap mCurPageBitmap, mNextPageBitmap, mBgBitmap;
    protected Canvas mCurrentPageCanvas, mNextPageCanvas, mBgCanvas;

    private int slideMode = SlideMode.FOLLOW;
    private BaseSlider mSlider;

    private PageFactory mFactory = null;

    private OnPageStateChangedListener listener;

    private boolean isInit = false;

    public ReadView(Context context) {
        this(context, null);
    }

    public ReadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScreenWidth = ScreenUtils.getScreenWidth();
        mScreenHeight = ScreenUtils.getScreenHeight();

        mCurPageBitmap = createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        mNextPageBitmap = createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        mCurrentPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);

        setClickable(true);

        setSlideMode(slideMode);

        mFactory = new PageFactory();
    }

    /**
     * 切换横竖屏，由于宽高发生变化，需要重新计算画布大小
     */
    public void initScreenSize() {
        mScreenWidth = ScreenUtils.getScreenWidth();
        mScreenHeight = ScreenUtils.getScreenHeight();

        mCurPageBitmap = BitmapUtils.scaleBitmap(mCurPageBitmap, mScreenWidth, mScreenHeight);
        mNextPageBitmap = BitmapUtils.scaleBitmap(mNextPageBitmap, mScreenWidth, mScreenHeight);

        mCurrentPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);

        mSlider.initScreenSize();
        mFactory.initScreenSize();
        mFactory.pages(ChapterType.CURRENT);

        postInvalidate();
    }

    public void initChapterList(@SourceID int sourceId, String bookNum, List<Chapter> list, int chapterIndex, int pageIndex) {
        if (!isInit || mFactory.getBufferLen() < 1) { // 未初始化或读取章节失败，则重新初始化
            isInit = true;

            mFactory.initBook(sourceId, bookNum, list, chapterIndex, pageIndex);
            mFactory.openBook(chapterIndex, ChapterType.CURRENT);
        }

        postInvalidate();
    }

    public boolean isInit() {
        return isInit;
    }

    public PageFactory getPageFactory() {
        return mFactory;
    }

    public Bitmap getCurPageBitmap() {
        if (!mSlider.isPreparedCurPage) {
            mSlider.isPreparedCurPage = true;
            mFactory.drawCurPage(mCurrentPageCanvas, DrawPageType.DRAW_CUR_PAGE);
        }
        return mCurPageBitmap;
    }

    public Bitmap getNextPageBitmap() {
        if (!mSlider.isPreparedTempPage) {
            mSlider.isPreparedTempPage = true;
            mFactory.drawCurPage(mNextPageCanvas, DrawPageType.DRAW_NEXT_PAGE);
        }
        return mNextPageBitmap;
    }

    public Bitmap getPrePageBitmap() {
        if (!mSlider.isPreparedTempPage) {
            mSlider.isPreparedTempPage = true;
            mFactory.drawCurPage(mNextPageCanvas, DrawPageType.DRAW_PRE_PAGE);
        }
        return mNextPageBitmap;
    }

    public void nextPage() {
        mFactory.nextPage();
        postInvalidate();
    }

    public void prePage() {
        mFactory.prePage();
        postInvalidate();
    }

    public void preChapter() {
        mFactory.preChapter();
        postInvalidate();
    }

    public void nextChapter() {
        mFactory.nextChapter();
        postInvalidate();
    }

    public void jumpChapter(int chapter) {
        mFactory.jumpChapter(chapter);
        postInvalidate();
    }

    public void setOnPageStateChangedListener(OnPageStateChangedListener listener) {
        this.listener = listener;
        mSlider.initListener(listener);
        mFactory.initListener(listener);
    }

    /**
     * 设置滑动模式
     *
     * @param mode
     */
    public void setSlideMode(@SlideMode int mode) {
        if (mode == SlideMode.FOLLOW) {
            mSlider = new FollowSlider();
        } else {
            mSlider = new OverlapSlider();
        }
        mSlider.bind(this);
        mSlider.initListener(listener);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        mSlider.computeScroll();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mSlider.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mSlider.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public void postInvalidate() {
        mSlider.isPreparedCurPage = false;
        mSlider.isPreparedTempPage = false;
        super.postInvalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        recycler();
    }

    public void recycler() {

        mCurrentPageCanvas = null;
        mNextPageCanvas = null;
        mBgCanvas = null;

        BitmapUtils.recycler(mBgBitmap);
        BitmapUtils.recycler(mCurPageBitmap);
        BitmapUtils.recycler(mNextPageBitmap);

        mFactory.recycler();
        mFactory = null;
    }
}
