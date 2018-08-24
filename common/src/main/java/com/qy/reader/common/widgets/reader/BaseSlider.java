package com.qy.reader.common.widgets.reader;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Scroller;

import com.qy.reader.common.utils.ScreenUtils;
import com.qy.reader.common.utils.ToastUtils;

/**
 * @author yuyh.
 * @date 17/3/24.
 */
public abstract class BaseSlider implements Slider {

    /**
     * 手指移动的方向
     */
    static final int MOVE_TO_LEFT = 0;    // 向左
    static final int MOVE_TO_RIGHT = 1;   // 向右
    static final int MOVE_NO_RESULT = 4;  // 默认状态

    /**
     * 触摸的模式
     */
    static final int MODE_NONE = 0; // 无效
    static final int MODE_MOVE = 1; // 滑动

    /**
     * 自动滚动模式
     */
    static final int SCROLL_VALID = 1; // 有效，切换上下页
    static final int SCROLL_NONE = 0;      // 默认
    static final int SCROLL_INVALID = -1;  // 无效，归位

    boolean isPreparedCurPage = false;
    boolean isPreparedTempPage = false;

    ReadView mReadView;
    private Scroller mScroller;
    protected VelocityTracker mVelocityTracker;

    protected int mScreenWidth, mScreenHeight;
    protected int startX = 0, startY = 0;
    protected int distance = 0;
    protected int scrollDistance = 0;
    protected int mVelocityValue;
    protected long lastDownMillis = 0;
    /**
     * 触发自动滚动的最小距离
     */
    protected final int limitDistance = ScreenUtils.dpToPxInt(50);
    protected final int shadowSize = ScreenUtils.dpToPxInt(7);

    /**
     * 向左自动滚动状态
     */
    protected int autoScrollLeft = SCROLL_NONE;
    /**
     * 向右自动滚动状态
     */
    protected int autoScrollRight = SCROLL_NONE;

    /**
     * 最后触摸的结果方向
     */
    protected int mTouchResult = MOVE_NO_RESULT;
    /**
     * 一开始的方向
     */
    protected int mDirection = MOVE_NO_RESULT;
    /**
     * 模式
     */
    protected int mMode = MODE_NONE;

    private OnPageStateChangedListener listener;

    public BaseSlider() {
        initScreenSize();
    }

    public void initScreenSize() {
        mScreenWidth = ScreenUtils.getScreenWidth();
        mScreenHeight = ScreenUtils.getScreenHeight();
    }

    @Override
    public void bind(ReadView readView) {
        this.mReadView = readView;

        mScroller = new Scroller(mReadView.getContext());
    }

    @Override
    public void initListener(OnPageStateChangedListener listener) {
        this.listener = listener;
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        obtainVelocityTracker(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                lastDownMillis = System.currentTimeMillis();
                startX = (int) event.getX();
                startY = (int) event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (!mScroller.isFinished()) {
                    return false;
                }
                if (startX == 0) {
                    startX = (int) event.getX();
                }

                // 1. 计算滑动距离
                distance = startX - (int) event.getX();

                // 2. 根据滑动距离 判断滑动方向
                if (mDirection == MOVE_NO_RESULT) {
                    if (distance > 0) {
                        if (mReadView.getPageFactory().hasNextPage()) {
                            mDirection = MOVE_TO_LEFT;
                        } else {
                            ToastUtils.showSingleToast("没有下一页了");
                        }
                    } else if (distance < 0) {
                        if (mReadView.getPageFactory().hasPrePage()) {
                            mDirection = MOVE_TO_RIGHT;
                        } else {
                            ToastUtils.showSingleToast("没有上一页了");
                        }
                    }
                }

                // 3. 开启滑动模式
                if (mMode == MODE_NONE
                        && ((mDirection == MOVE_TO_LEFT && mReadView.getPageFactory().hasNextPage())
                        || (mDirection == MOVE_TO_RIGHT && mReadView.getPageFactory().hasPrePage()))) {
                    mMode = MODE_MOVE;
                }

                // 4. 若滑动过程中往反方向滑动，则关闭滑动模式
                if (mMode == MODE_MOVE) {
                    if ((mDirection == MOVE_TO_LEFT && distance <= 0) || (mDirection == MOVE_TO_RIGHT && distance >= 0)) {
                        mMode = MODE_NONE;
                    }
                }

                // 5. 处理滑动结果
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                mVelocityValue = (int) mVelocityTracker.getXVelocity();

                int time = mScreenWidth - Math.abs(distance);
                if (mMode == MODE_MOVE && mDirection == MOVE_TO_LEFT) {
                    if (Math.abs(distance) > limitDistance || mVelocityValue < -500) {
                        // 手指向左移动，可以翻屏幕
                        mTouchResult = MOVE_TO_LEFT;
                        mScroller.startScroll(-(mScreenWidth - distance), 0, mScreenWidth - distance, 0, time);
                        autoScrollLeft = SCROLL_VALID;
                    } else {
                        // 手指向左移动，距离不足且速度不够，右滑归位
                        mTouchResult = MOVE_TO_RIGHT;
                        mScroller.startScroll(-(mScreenWidth - distance), 0, -distance, 0, time);
                        autoScrollLeft = SCROLL_INVALID;
                    }
                } else if (mMode == MODE_MOVE && mDirection == MOVE_TO_RIGHT) {
                    if (Math.abs(distance) > limitDistance || mVelocityValue > 500) {
                        // 手指向右移动，可以翻屏幕
                        mTouchResult = MOVE_TO_RIGHT;
                        mScroller.startScroll(distance, 0, -(mScreenWidth + distance), 0, time);
                        autoScrollRight = SCROLL_VALID;
                    } else {
                        // 手指向右移动，距离不足且速度不够，左滑归位
                        mTouchResult = MOVE_TO_LEFT;
                        mScroller.startScroll(distance, 0, -distance, 0, time);
                        autoScrollRight = SCROLL_INVALID;
                    }
                } else if (System.currentTimeMillis() - lastDownMillis < 500) {
                    if (startX > ScreenUtils.getScreenWidth() / 3 && startX < ScreenUtils.getScreenWidth() / 1.5f) {
                        // 点击中间位置
                        if (listener != null) {
                            listener.onCenterClick();
                        }
                    } else if (startX < mScreenWidth >> 1 && mReadView.getPageFactory().hasPrePage()) {// 单击。动画时间为宽度的一半
                        mScroller.startScroll(0, 0, -mScreenWidth, 0, mScreenWidth >> 1);
                        autoScrollRight = SCROLL_VALID;
                        mTouchResult = MOVE_TO_RIGHT;
                    } else if (startX >= mScreenWidth >> 1 && mReadView.getPageFactory().hasNextPage()) {
                        mScroller.startScroll(-mScreenWidth, 0, mScreenWidth, 0, mScreenWidth >> 1);
                        autoScrollLeft = SCROLL_VALID;
                        mTouchResult = MOVE_TO_LEFT;
                    }
                } else {
                    isPreparedCurPage = false;
                    isPreparedTempPage = false;
                }
                resetVariables();
                invalidate();
                break;
        }
        return false;
    }

    private void resetVariables() {
        mDirection = MOVE_NO_RESULT;
        mMode = MODE_NONE;
        startX = 0;
        releaseVelocityTracker();
    }

    private void invalidate() {
        mReadView.invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            scrollDistance = (int) x;
            invalidate();
        } else {
            // 滑动完成，判断是否完成上下页的切换
            if (autoScrollLeft == SCROLL_VALID) {
                mReadView.nextPage();
            }
            autoScrollLeft = SCROLL_NONE;

            if (autoScrollRight == SCROLL_VALID) {
                mReadView.prePage();
            }
            autoScrollRight = SCROLL_NONE;

            if (mScroller.isFinished() && mTouchResult != MOVE_NO_RESULT) {
                isPreparedCurPage = false;
                isPreparedTempPage = false;
                mTouchResult = MOVE_NO_RESULT;
                invalidate();
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        // 适应覆盖翻页 上下层关系
        if (mDirection == MOVE_TO_LEFT
                || (mDirection == MOVE_NO_RESULT && autoScrollLeft == SCROLL_NONE && autoScrollRight == SCROLL_NONE)
                || autoScrollLeft != SCROLL_NONE) {
            // 往左滑。先绘制下一页，在绘制当前页（当前页在上层）
            drawTempPageArea(canvas);
            drawCurrentPageArea(canvas);
        } else {
            // 往右滑。先绘制当前页，在绘制上一页（上一页在上层）
            drawCurrentPageArea(canvas);
            drawTempPageArea(canvas);
        }
        drawShadow(canvas);
    }

    abstract void drawCurrentPageArea(Canvas canvas);

    abstract void drawTempPageArea(Canvas canvas);

    abstract void drawShadow(Canvas canvas);
}
