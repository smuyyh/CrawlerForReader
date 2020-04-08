package com.qy.reader.common.widgets.reader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.qy.reader.common.Global;
import com.qy.reader.common.R;
import com.qy.reader.common.entity.chapter.Chapter;
import com.qy.reader.common.entity.source.SourceID;
import com.qy.reader.common.utils.BitmapUtils;
import com.qy.reader.common.utils.LogUtils;
import com.qy.reader.common.utils.ScreenUtils;
import com.qy.reader.common.utils.ZHConverter;
import com.qy.reader.common.widgets.reader.annotation.ChapterType;
import com.qy.reader.common.widgets.reader.annotation.DensityLevel;
import com.qy.reader.common.widgets.reader.annotation.DrawPageType;
import com.qy.reader.common.widgets.reader.annotation.FontType;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by yuyuhang on 2018/1/11.
 */
public class PageFactory {

    private int mScreenHeight, mScreenWidth;
    private int mVisibleHeight, mVisibleWidth;
    private int marginHeight, marginWidth;

    /**
     * title size;
     */
    private int mNumFontSize;

    /**
     * content size;
     */
    private int mFontSize;

    private int mTextColor;

    private int mPageLineCount;
    private int mLineSpace;

    private Paint mTitlePaint, mTextPaint;

    private List<Integer> mPageLocations = new ArrayList<>();
    private MappedByteBuffer mBuffer;
    private int mBufferLen;

    private List<Integer> mPrePageLocations = new ArrayList<>();
    private MappedByteBuffer mPreBuffer;
    private int mPreBufferLen;

    private List<Integer> mNextPageLocations = new ArrayList<>();
    private MappedByteBuffer mNextBuffer;
    private int mNextBufferLen;

    @SourceID
    private int sourceId;
    private String bookNum;
    private List<Chapter> chapters;
    private int currentPage = 1;
    private int currentChapter = 1;
    private int chapterSize = 0;

    private String time = "20:10";
    private int battery = 0;
    private ProgressBar batteryView;
    private Bitmap batteryBitmap;
    private Bitmap mBgBitmap;

    /**
     * background color.
     */
    private final int mBgColor = Color.parseColor("#d4edc6");

    /**
     * 绘制最底部部分时距离底部的距离;
     */
    private final int mTopToBottom = ScreenUtils.dpToPxInt(8);

    private OnPageStateChangedListener listener;
    private final Calendar calendar = Calendar.getInstance();

    public PageFactory() {
        mNumFontSize = ScreenUtils.dpToPxInt(12);
        marginWidth = ScreenUtils.dpToPxInt(15);
        marginHeight = ScreenUtils.dpToPxInt(15);

        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setColor(Color.parseColor("#999999"));

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor = Color.parseColor("#513620"));

        initScreenSize();
    }

    public void initScreenSize() {
        mScreenWidth = ScreenUtils.getScreenWidth();
        mScreenHeight = ScreenUtils.getScreenHeight();

        boolean cutout = ScreenUtils.getCutout() != null;
        if (!cutout) {
            // 非刘海屏情况;
            mScreenHeight -= ScreenUtils.getStatusBarHeight(Global.getApplication());
        }
        // 绘制情况：从上到下依次为 title, content, time;
        mVisibleHeight = mScreenHeight - (marginHeight << 1) - (mNumFontSize << 1) - (mLineSpace << 1) - mTopToBottom;
        mVisibleWidth = mScreenWidth - (marginWidth << 1);
    }

    public void initBook(@SourceID int sourceId, String bookNum, List<Chapter> chapters, int currentChapter, int currentPage) {
        this.sourceId = sourceId;
        this.bookNum = bookNum;
        this.chapters = chapters;
        this.currentChapter = currentChapter;
        this.currentPage = currentPage;

        this.chapterSize = this.chapters.size();

        initTextSize(SettingManager.getTextSizeSP());
        initLineSpace(SettingManager.getDensityLevel());
    }

    public void initTextSize(int textSize) {
        this.mFontSize = ScreenUtils.dpToPxInt(textSize);
        this.mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);

        mTextPaint.setTextSize(mFontSize);
        mTitlePaint.setTextSize(mNumFontSize);
    }

    public void initLineSpace(@DensityLevel int lineSpace) {
        this.mLineSpace = ScreenUtils.dpToPxInt(lineSpace);
        this.mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
    }

    public void initListener(OnPageStateChangedListener listener) {
        this.listener = listener;
    }

    public void openBook(int chapterIndex, @ChapterType int chapterType) {
        if (chapterIndex <= 0 || chapterIndex > chapters.size()) {
            return;
        }

        File file = BookManager.getInstance().getContentFile(sourceId, bookNum, chapters.get(chapterIndex - 1).title);
        if (file.length() > 10) {
            try {
                if (chapterType == ChapterType.PREVIOUS) {
                    mPreBufferLen = (int) file.length();
                    mPreBuffer = new RandomAccessFile(file, "r")
                            .getChannel()
                            .map(FileChannel.MapMode.READ_ONLY, 0, mPreBufferLen);
                } else if (chapterType == ChapterType.NEXT) {
                    mNextBufferLen = (int) file.length();
                    mNextBuffer = new RandomAccessFile(file, "r")
                            .getChannel()
                            .map(FileChannel.MapMode.READ_ONLY, 0, mNextBufferLen);
                } else {
                    mBufferLen = (int) file.length();
                    mBuffer = new RandomAccessFile(file, "r")
                            .getChannel()
                            .map(FileChannel.MapMode.READ_ONLY, 0, mBufferLen);
                }
            } catch (IOException e) {
                LogUtils.e(e.toString());
            }
            Log.i("TAGS", System.currentTimeMillis() + "");
            pages(chapterType);
            Log.i("TAGS", System.currentTimeMillis() + "");
        }
    }

    /**
     * 分页
     */
    public synchronized void pages(@ChapterType int chapterType) {
        int location = 0;
        int lines = 0;

        List<Integer> locations;
        int bufferLen;

        if (chapterType == ChapterType.PREVIOUS) {
            locations = mPrePageLocations;
            bufferLen = mPreBufferLen;
        } else if (chapterType == ChapterType.NEXT) {
            locations = mNextPageLocations;
            bufferLen = mNextBufferLen;
        } else {
            locations = mPageLocations;
            bufferLen = mBufferLen;
        }

        locations.clear();
        locations.add(location);

        int paraSpace = 0;
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);

        try {

            while (location < bufferLen) {

                byte[] paraBuffer = readParagraphForward(location, chapterType);
                location += paraBuffer.length;
                String paraStr = new String(paraBuffer, "UTF-8");
                String lineStr;

                while (paraStr.length() > 0) {
                    int paintSize = mTextPaint.breakText(paraStr, true, mVisibleWidth, null);

                    // 去掉空行
                    lineStr = paraStr.substring(0, paintSize)
                            .trim()
                            .replaceAll(" ", "")
                            .replaceAll("\u3000", "");
                    if (lineStr.length() > 0) {
                        lines++;
                    }

                    paraStr = paraStr.substring(paintSize);

                    if (lines >= mPageLineCount) {
                        lines = 0;
                        paraSpace = 0;
                        locations.add(location - paraStr.getBytes().length);
                        if (locations.size() > 1) { // 第一页分完，最后一个段落还有部分没处理，则需要重新计算页容纳大小，否则第二页可能无法满屏
                            mPageLineCount = (mVisibleHeight - paraSpace) / (mFontSize + mLineSpace);
                        }
                    }
                }

                paraSpace += (mLineSpace / 2);
                mPageLineCount = (mVisibleHeight - paraSpace) / (mFontSize + mLineSpace);
            }
            // 最后如果有空白行，可能出现一个空白页，剔除
            if (lines < 1 && locations.size() > 1) {
                locations.remove(locations.size() - 1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取下一段落
     *
     * @param curEndPos   当前页结束位置指针
     * @param chapterType
     * @return
     */
    private byte[] readParagraphForward(int curEndPos, @ChapterType int chapterType) {

        MappedByteBuffer buffer;
        int bufferLen;

        if (chapterType == ChapterType.PREVIOUS) {
            buffer = mPreBuffer;
            bufferLen = mPreBufferLen;
        } else if (chapterType == ChapterType.NEXT) {
            buffer = mNextBuffer;
            bufferLen = mNextBufferLen;
        } else {
            buffer = mBuffer;
            bufferLen = mBufferLen;
        }

        byte b0;
        int i = curEndPos;
        while (i < bufferLen) {
            b0 = buffer.get(i++);
            if (b0 == 0x0a) {
                break;
            }
        }
        int nParaSize = i - curEndPos;
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = buffer.get(curEndPos + i);
        }
        return buf;
    }

    public synchronized void drawCurPage(Canvas canvas, @DrawPageType int drawPage) {
        // 1. 绘制背景
        if (mBgBitmap != null) {
            canvas.drawBitmap(mBgBitmap, 0, 0, mTextPaint);
        } else {
            canvas.drawColor(mBgColor);
        }

        // 绘制底部电量时间等
        if (batteryBitmap != null && !batteryBitmap.isRecycled()) {
            canvas.drawBitmap(batteryBitmap, marginWidth,
                    mScreenHeight - ScreenUtils.dpToPxInt(17), mTitlePaint);
        }

        calendar.setTimeInMillis(System.currentTimeMillis());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        time = String.format(Locale.PRC,"%02d:%02d", hour, minute);
        int timeX = marginWidth + ScreenUtils.dpToPxInt(25);
        int timeY = mScreenHeight - mTopToBottom;
        canvas.drawText(time, timeX, timeY, mTitlePaint);

        if (chapters == null) {
            return;
        }

        String pageProgress = ""; // 页码

        // 2. 计算当前页字节长度
        int start = 0;
        int size = 0;
        byte[] bytes;

        @ChapterType
        int chapterType = ChapterType.CURRENT;

        if (drawPage == DrawPageType.DRAW_CUR_PAGE) {
            if (mPageLocations.size() < 1) {
                return;
            }
            if (currentPage < 1)
                currentPage = 1;
            if (currentPage > mPageLocations.size())
                currentPage = mPageLocations.size();
            pageProgress = currentPage + "/" + mPageLocations.size();
            start = mPageLocations.get(currentPage - 1);
            if (currentPage <= mPageLocations.size() - 1) {
                size = mPageLocations.get(currentPage) - start;
            } else if (currentPage == mPageLocations.size()) {
                size = mBufferLen - start;
            } else if (currentChapter < chapterSize) {
                // openBook(currentChapter + 1, ChapterType.CURRENT);
            } else {
                return;
            }
        } else if (drawPage == DrawPageType.DRAW_NEXT_PAGE) {
            if (currentPage < 1)
                currentPage = 1;
            if (currentPage <= mPageLocations.size() - 1) {
                start = mPageLocations.get(currentPage);
                if (currentPage <= mPageLocations.size() - 2) {
                    size = mPageLocations.get(currentPage + 1) - start;
                } else {
                    size = mBufferLen - start;
                }
                pageProgress = (currentPage + 1) + "/" + mPageLocations.size();
            } else if (currentChapter < chapterSize) {
                chapterType = ChapterType.NEXT;
                if (mNextBuffer == null) {
                    openBook(currentChapter + 1, chapterType);
                }

                if (mNextPageLocations.size() > 0) {
                    start = mNextPageLocations.get(0);

                    if (mNextPageLocations.size() > 1) {
                        size = mNextPageLocations.get(1) - start;
                    } else {
                        size = mNextBufferLen - start;
                    }
                } else {
                    return;
                }
            }
        } else if (drawPage == DrawPageType.DRAW_PRE_PAGE) {
            if (currentPage > mPageLocations.size())
                currentPage = mPageLocations.size();
            if (currentPage > 1) {
                start = mPageLocations.get(currentPage - 2);
                size = mPageLocations.get(currentPage - 1) - start;
                pageProgress = (currentPage - 1) + "/" + mPageLocations.size();
            } else if (currentChapter > 1) {
                chapterType = ChapterType.PREVIOUS;
                if (mPreBuffer == null) {
                    openBook(currentChapter - 1, chapterType);
                }

                if (mPrePageLocations.size() > 0) {
                    start = mPrePageLocations.get(mPrePageLocations.size() - 1);
                    size = mPreBufferLen - start;
                } else {
                    return;
                }
            }
        } else {
            return;
        }

        String title;
        MappedByteBuffer buffer;
        if (chapterType == ChapterType.NEXT) {
            buffer = mNextBuffer;
            pageProgress = "1/" + mNextPageLocations.size();
            title = chapters.get(currentChapter).title;
        } else if (chapterType == ChapterType.PREVIOUS) {
            buffer = mPreBuffer;
            pageProgress = mPrePageLocations.size() + "/" + mPrePageLocations.size();
            title = chapters.get(currentChapter - 2).title;
        } else {
            buffer = mBuffer;
            title = chapters.get(currentChapter - 1).title;
        }

        bytes = new byte[size];
        for (int i = 0, j = start; i < size; i++) {
            bytes[i] = buffer.get(i + j);
        }

        try {
            // 3. 字节转字符串
            String str = new String(bytes, "utf-8");
            if (SettingManager.isTraditionalFont()) { // 繁体转换
                str = ZHConverter.convert(str, FontType.TRADITIONAL);
            }

            // 4. 绘制标题
            int y = mNumFontSize * 2;
            canvas.drawText(title, marginWidth, y, mTitlePaint);
            y += mNumFontSize;

            // 5. 按行绘制
            String[] paras = str.split("\n");
            String line;

            for (String para : paras) {
                while (para.length() > 0) {
                    int paintSize = mTextPaint.breakText(para, true, mVisibleWidth, null);
                    line = para.substring(0, paintSize);

                    if (line.trim().replaceAll(" ", "").replaceAll("\u3000", "").length() > 0) {
                        y += mLineSpace + mFontSize;
                        canvas.drawText(line, marginWidth, y, mTextPaint);
                    }

                    para = para.substring(paintSize);
                }
                // 段落增加行距
                y += mLineSpace / 2;
            }

            // 6. 绘制进度
            canvas.drawText(pageProgress, mScreenWidth - marginWidth - mTitlePaint.measureText(pageProgress), mScreenHeight - mTopToBottom, mTitlePaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasNextPage() {
        if (currentPage >= mPageLocations.size()) {
            return hasNextChapter();
        }
        return currentPage < mPageLocations.size() || currentChapter < chapterSize;
    }

    public boolean hasPrePage() {
        if (currentPage <= 1) {
            return hasPreChapter();
        }
        return !(currentPage <= 1 && currentChapter <= 1);
    }

    public boolean hasNextChapter() {
        if (currentChapter >= chapterSize) {
            return false;
        }

        File file = BookManager.getInstance().getContentFile(sourceId, bookNum, chapters.get(currentChapter).title);
        if (file.length() > 10) {
            return true;
        } else {
            listener.onChapterLoadFailure(currentChapter);
            return false;
        }
    }

    public boolean hasPreChapter() {
        if (currentChapter <= 1) {
            return false;
        }

        File file = BookManager.getInstance().getContentFile(sourceId, bookNum, chapters.get(currentChapter - 2).title);
        if (file.length() > 10) {
            return true;
        } else {
            listener.onChapterLoadFailure(currentChapter - 2);
            return false;
        }
    }

    public void nextPage() {
        if (currentPage < mPageLocations.size()) {
            currentPage++;
            if (listener != null) {
                listener.onPageChanged(currentPage, currentChapter);
            }
        } else if (currentChapter < chapterSize) {
            currentChapter++;

            if (mNextBuffer == null || mNextPageLocations.size() < 1) {
                openBook(currentChapter, ChapterType.NEXT);
            }

            currentPage = 1;

            if (listener != null) {
                listener.onPageChanged(currentPage, currentChapter);
            }

            // 当前章节 切换为下一章
            mPageLocations = new ArrayList<>(mNextPageLocations);
            mBuffer = mNextBuffer;
            mBufferLen = mNextBufferLen;

            if (listener != null) {
                listener.onChapterChanged(currentChapter, currentChapter - 1, false);
            }

            clearTempBuffer();
        }
    }

    public void prePage() {
        if (currentPage > 1) {
            currentPage--;
            if (listener != null) {
                listener.onPageChanged(currentPage, currentChapter);
            }
        } else if (currentChapter > 1) {
            currentChapter--;

            if (mPreBuffer == null || mPrePageLocations.size() < 1) {
                openBook(currentChapter, ChapterType.PREVIOUS);
            }

            currentPage = mPrePageLocations.size();
            if (listener != null) {
                listener.onPageChanged(currentPage, currentChapter);
            }

            // 当前章节 切换为上一章
            mPageLocations = new ArrayList<>(mPrePageLocations);
            mBuffer = mPreBuffer;
            mBufferLen = mPreBufferLen;

            if (listener != null) {
                listener.onChapterChanged(currentChapter, currentChapter + 1, false);
            }

            clearTempBuffer();
        }
    }

    public void preChapter() {
        if (currentChapter > 1) {
            currentChapter--;
            currentPage = 1;

            clearTempBuffer();
            openBook(currentChapter, ChapterType.CURRENT);

            if (listener != null) {
                listener.onChapterChanged(currentChapter, currentChapter + 1, true);
            }
        }
    }

    public void nextChapter() {
        if (currentChapter < chapterSize) {
            currentChapter++;
            currentPage = 1;

            clearTempBuffer();
            openBook(currentChapter, ChapterType.CURRENT);

            if (listener != null) {
                listener.onChapterChanged(currentChapter, currentChapter - 1, true);
            }
        }
    }

    public void jumpChapter(int chapter) {
        if (chapters != null && chapter >= 1 && chapter <= chapters.size()) {
            if (chapter == currentChapter)
                return;

            int fromChapter = currentChapter;
            currentChapter = chapter;
            clearTempBuffer();
            currentPage = 1;
            openBook(currentChapter, ChapterType.CURRENT);

            if (listener != null) {
                listener.onChapterChanged(currentChapter, fromChapter, true);
            }
        }
    }

    private void clearTempBuffer() {
        // 清空缓存章节
        mNextPageLocations.clear();
        mPrePageLocations.clear();
        mPreBuffer = null;
        mNextBuffer = null;
    }

    public void setTextSize(int textSize) {
        initTextSize(textSize);

        pages(ChapterType.CURRENT);

        if (currentPage > mPageLocations.size()) {
            currentPage = mPageLocations.size();
        }

        mPreBuffer = null;
        mNextBuffer = null;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setBattery(int value) {
        this.battery = value;
        if (batteryView == null) {
            batteryView = (ProgressBar) LayoutInflater.from(Global.getApplication())
                    .inflate(R.layout.layout_read_power, null);
            batteryView.measure(View.MeasureSpec.makeMeasureSpec(ScreenUtils.dpToPxInt(20), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(ScreenUtils.dpToPxInt(9), View.MeasureSpec.EXACTLY));
            batteryView.layout(0, 0, batteryView.getMeasuredWidth(), batteryView.getMeasuredHeight());
        }

        BitmapUtils.recycler(batteryBitmap);

        batteryView.setProgress(battery);
        batteryView.setDrawingCacheEnabled(true);
        batteryView.buildDrawingCache();
        batteryBitmap = Bitmap.createBitmap(batteryView.getDrawingCache());
        batteryView.setDrawingCacheEnabled(false);
        batteryView.destroyDrawingCache();
    }

    public void setBackgroundBitmap(Bitmap bitmap) {
        mBgBitmap = bitmap;
    }

    public void setListener(OnPageStateChangedListener listener) {
        this.listener = listener;
    }

    public int getBufferLen() {
        return mBufferLen;
    }

    public void recycler() {
        mBuffer = null;
        mPageLocations.clear();
        mPageLocations = null;

        mPreBuffer = null;
        mPrePageLocations.clear();
        mPrePageLocations = null;

        mNextBuffer = null;
        mNextPageLocations.clear();
        mNextPageLocations = null;

        BitmapUtils.recycler(batteryBitmap);
    }
}
