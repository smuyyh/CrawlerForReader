package com.qy.reader.common.widgets.reader;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewConfiguration;

/**
 * @author yuyh.
 * @date 17/3/26.
 */
public class OverlapSlider extends BaseSlider {

    private Path mPath;

    private GradientDrawable mShadowDrawable;

    public OverlapSlider() {
        mPath = new Path();

        int[] mBackShadowColors = new int[]{0xaa666666, 0x666666};

        mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
        mShadowDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }

    /**
     * 绘制当前页
     *
     * @param canvas
     */
    void drawCurrentPageArea(Canvas canvas) {
        mPath.reset();

        canvas.save();

        // 手指滑动状态
        if (mMode == MODE_MOVE && mDirection != MOVE_NO_RESULT) { // 有效滑动（手指触摸滑动过程）
            mVelocityTracker.computeCurrentVelocity(1000, ViewConfiguration.getMaximumFlingVelocity());
            if (mDirection == MOVE_TO_LEFT) {
                mPath.addRect(0, 0, mScreenWidth - distance, mScreenHeight, Path.Direction.CW);
                canvas.clipPath(mPath);
                canvas.drawBitmap(mReadView.getCurPageBitmap(), -distance, 0, null);
            } else if (mDirection == MOVE_TO_RIGHT) {
                mPath.addRect(-distance, 0, mScreenWidth, mScreenHeight, Path.Direction.CW);
                canvas.clipPath(mPath);
                canvas.drawBitmap(mReadView.getCurPageBitmap(), 0, 0, null);
            }
        } else if (autoScrollLeft != SCROLL_NONE) { // 自动左滑
            mPath.addRect(0, 0, -scrollDistance, mScreenHeight, Path.Direction.CW);
            canvas.clipPath(mPath);
            canvas.drawBitmap(mReadView.getCurPageBitmap(), -mScreenWidth - scrollDistance, 0, null);
        } else if (autoScrollRight != SCROLL_NONE) { // 自动右滑
            mPath.addRect(-scrollDistance, 0, mScreenWidth, mScreenHeight, Path.Direction.CW);
            canvas.clipPath(mPath);
            canvas.drawBitmap(mReadView.getCurPageBitmap(), 0, 0, null);
        } else { // 初始状态或无效滑动（eg: 先往左滑，又往右滑到初始状态，继续往右就不动）
            mPath.addRect(0, 0, mScreenWidth, mScreenHeight, Path.Direction.CW);
            canvas.clipPath(mPath);
            canvas.drawBitmap(mReadView.getCurPageBitmap(), 0, 0, null);
        }
        canvas.restore();
    }

    /**
     * 绘制阴影
     *
     * @param canvas
     */
    void drawShadow(Canvas canvas) {
        canvas.save();
        if (mDirection != MOVE_NO_RESULT) {
            if (mDirection == MOVE_TO_LEFT) {
                mShadowDrawable.setBounds(mScreenWidth - distance, 0, mScreenWidth - distance + shadowSize, mScreenHeight);
            } else {
                mShadowDrawable.setBounds(-distance, 0, -distance + shadowSize, mScreenHeight);
            }
        } else {
            mShadowDrawable.setBounds(-scrollDistance, 0, -scrollDistance + shadowSize, mScreenHeight);
        }
        mShadowDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制临时页（类似绘制当前页，只有在滑动过程需要绘制，否则只绘制当前页）
     *
     * @param canvas
     */
    void drawTempPageArea(Canvas canvas) {
        mPath.reset();

        canvas.save();

        if (mMode == MODE_MOVE && mDirection != MOVE_NO_RESULT) {
            mVelocityTracker.computeCurrentVelocity(1000, ViewConfiguration.getMaximumFlingVelocity());
            if (mDirection == MOVE_TO_LEFT) {
                mPath.addRect(mScreenWidth - distance, 0, mScreenWidth, mScreenHeight, Path.Direction.CW);
                canvas.clipPath(mPath);
                canvas.drawBitmap(mReadView.getNextPageBitmap(), 0, 0, null);
            } else if (mDirection == MOVE_TO_RIGHT) {
                mPath.addRect(-mScreenWidth - distance, 0, -distance, mScreenHeight, Path.Direction.CW);
                canvas.clipPath(mPath);
                canvas.drawBitmap(mReadView.getPrePageBitmap(), -mScreenWidth - distance, 0, null);
            }
        } else {
            if (autoScrollLeft != SCROLL_NONE) {
                mPath.addRect(-scrollDistance, 0, mScreenWidth, mScreenHeight, Path.Direction.CW);
                canvas.clipPath(mPath);
                canvas.drawBitmap(mReadView.getNextPageBitmap(), 0, 0, null);
            } else if (autoScrollRight != SCROLL_NONE) {
                mPath.addRect(-mScreenWidth - scrollDistance, 0, -scrollDistance, mScreenHeight, Path.Direction.CW);
                canvas.clipPath(mPath);
                canvas.drawBitmap(mReadView.getPrePageBitmap(), -mScreenWidth - scrollDistance, 0, null);
            }
        }
        canvas.restore();
    }
}
