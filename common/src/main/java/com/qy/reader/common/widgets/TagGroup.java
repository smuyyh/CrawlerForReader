package com.qy.reader.common.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.qy.reader.common.utils.ScreenUtils;

/**
 * Created by yuyuhang on 2018/1/9.
 */
public class TagGroup extends ViewGroup {

    private static final int SIDE_MARGIN = ScreenUtils.dpToPxInt(10);//布局边距
    private static final int VERTICAL_SPACING = ScreenUtils.dpToPxInt(6);
    private static final int HORIZONTAL_SPACING = ScreenUtils.dpToPxInt(8);

    private int verticalSpacing = VERTICAL_SPACING;
    private int horizontalSpacing = HORIZONTAL_SPACING;

    public TagGroup(Context context) {
        this(context, null);
    }

    public TagGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x = SIDE_MARGIN;//横坐标
        int y = 0;//纵坐标
        int rows = 1;//总行数

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int actualWidth = specWidth - SIDE_MARGIN * 2;//实际宽度;
        int childCount = getChildCount();

        // 处理宽高都为 wrap_content 的情况
        if (widthSpecMode == MeasureSpec.AT_MOST) {
            for (int index = 0; index < childCount; index++) {
                View child = getChildAt(index);
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                x += width + horizontalSpacing;
                if (x > actualWidth) {//换行
                    x = width;
                    rows++;
                }
                y = rows * (height + verticalSpacing);
            }
            if (rows == 1) {
                actualWidth = x;
            }
        } else {
            for (int index = 0; index < childCount; index++) {
                View child = getChildAt(index);
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                x += width + horizontalSpacing;
                if (x > actualWidth) {//换行
                    x = width;
                    rows++;
                }
                y = rows * (height + verticalSpacing);
            }
        }
        setMeasuredDimension(actualWidth, y);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int autualWidth = r - l;
        int x = SIDE_MARGIN;// 横坐标开始
        int y = 0;//纵坐标开始
        int rows = 1;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            x += width;
            if (i != 0)
                x += horizontalSpacing;
            if (x > autualWidth) {
                x = width + SIDE_MARGIN;
                rows++;
            }
            y = rows * (height + verticalSpacing);
            view.layout(x - width, y - height, x, y);
        }
    }

    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
    }
}