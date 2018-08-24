package com.qy.reader.common.widgets.reader;

import android.graphics.Canvas;
import android.graphics.Path;
import android.view.ViewConfiguration;

/**
 * 滑动翻页
 *
 * @author yuyh.
 * @date 17/3/25.
 */
public class FollowSlider extends BaseSlider {

    private Path mPath;

    public FollowSlider() {
        mPath = new Path();
    }

    @Override
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
                canvas.drawBitmap(mReadView.getCurPageBitmap(), -distance, 0, null);
            }
        } else if (autoScrollLeft != SCROLL_NONE) { // 自动左滑
            mPath.addRect(0, 0, -scrollDistance, mScreenHeight, Path.Direction.CW);
            canvas.clipPath(mPath);
            canvas.drawBitmap(mReadView.getCurPageBitmap(), -mScreenWidth - scrollDistance, 0, null);
        } else if (autoScrollRight != SCROLL_NONE) { // 自动右滑
            mPath.addRect(-scrollDistance, 0, mScreenWidth, mScreenHeight, Path.Direction.CW);
            canvas.clipPath(mPath);
            canvas.drawBitmap(mReadView.getCurPageBitmap(), -scrollDistance, 0, null);
        } else { // 初始状态或无效滑动（eg: 先往左滑，又往右滑到初始状态，继续往右就不动）
            mPath.addRect(0, 0, mScreenWidth, mScreenHeight, Path.Direction.CW);
            canvas.clipPath(mPath);
            canvas.drawBitmap(mReadView.getCurPageBitmap(), 0, 0, null);
        }
        canvas.restore();
    }

    @Override
    void drawTempPageArea(Canvas canvas) {
        mPath.reset();

        canvas.save();

        if (mMode == MODE_MOVE && mDirection != MOVE_NO_RESULT) {
            mVelocityTracker.computeCurrentVelocity(1000, ViewConfiguration.getMaximumFlingVelocity());
            if (mDirection == MOVE_TO_LEFT) {
                mPath.addRect(mScreenWidth - distance, 0, mScreenWidth, mScreenHeight, Path.Direction.CW);
                canvas.clipPath(mPath);
                canvas.drawBitmap(mReadView.getNextPageBitmap(), mScreenWidth - distance, 0, null);
            } else if (mDirection == MOVE_TO_RIGHT) {
                mPath.addRect(-mScreenWidth - distance, 0, -distance, mScreenHeight, Path.Direction.CW);
                canvas.clipPath(mPath);
                canvas.drawBitmap(mReadView.getPrePageBitmap(), -mScreenWidth - distance, 0, null);
            }
        } else {
            if (autoScrollLeft != SCROLL_NONE) {
                mPath.addRect(-scrollDistance, 0, mScreenWidth, mScreenHeight, Path.Direction.CW);
                canvas.clipPath(mPath);
                canvas.drawBitmap(mReadView.getNextPageBitmap(), -scrollDistance, 0, null);
            } else if (autoScrollRight != SCROLL_NONE) {
                mPath.addRect(-mScreenWidth - scrollDistance, 0, -scrollDistance, mScreenHeight, Path.Direction.CW);
                canvas.clipPath(mPath);
                canvas.drawBitmap(mReadView.getPrePageBitmap(), -mScreenWidth - scrollDistance, 0, null);
            }
        }
        canvas.restore();
    }

    @Override
    void drawShadow(Canvas canvas) {

    }
}
