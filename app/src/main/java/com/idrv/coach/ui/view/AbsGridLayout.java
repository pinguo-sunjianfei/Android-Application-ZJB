package com.idrv.coach.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.idrv.coach.R;
import com.idrv.coach.utils.ValidateUtil;

import java.util.List;

/**
 * time:2016/8/3
 * description:宫格的View抽象类
 *
 * @author sunjianfei
 */
public abstract class AbsGridLayout<T> extends ViewGroup {
    //item的position
    protected int position;
    OnGridItemClickListener mClickListener;
    //默认每一行的子View个数
    protected int itemRowViewCount = 4;
    //默认的间距
    protected int itemMargin = 4;
    //默认显示多少行
    protected int rowNum = 1;
    //item的背景
    protected int mDrawableRes;
    //如果item有文字,字体颜色
    protected int mTextColor;
    protected List<T> mData;

    public AbsGridLayout(Context context) {
        super(context);
    }

    public AbsGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GridLayout, defStyle, 0);
        itemRowViewCount = a.getInteger(R.styleable.GridLayout_item_row_view_count, 4);
        rowNum = a.getInteger(R.styleable.GridLayout_item_count, 2);
        itemMargin = a.getDimensionPixelSize(R.styleable.GridLayout_item_margin, 4);
        mDrawableRes = a.getResourceId(R.styleable.GridLayout_item_drawable, -1);
        mTextColor = a.getColor(R.styleable.GridLayout_item_text_color, Color.BLACK);
        a.recycle();
        addChildViews(itemRowViewCount * rowNum);
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int childWidth = (width - itemMargin * (itemRowViewCount - 1)) / itemRowViewCount;
        int parentHeight = itemMargin * (rowNum - 1) + childWidth * rowNum;
        int childSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        measureChildren(childSpec, childSpec);
        setMeasuredDimension(width, parentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < rowNum; i++) {
                for (int j = 0; j < itemRowViewCount; j++) {
                    int index = j + i * itemRowViewCount;
                    if (index > childCount - 1) {
                        return;
                    }
                    View view = getChildAt(index);

                    int left = j * (itemMargin + view.getMeasuredWidth());
                    int right = left + view.getMeasuredWidth();
                    int top = i * (itemMargin + view.getMeasuredHeight());
                    int bottom = top + view.getMeasuredHeight();
                    view.layout(left, top, right, bottom);
                }
            }
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 添加子View
     */
    protected abstract void addChildViews(int count);

    /**
     * 设置数据
     */
    public void setData(List<T> data) {
        mData = data;
        if (ValidateUtil.isValidate(data)) {
            setVisibility(VISIBLE);
            //重新计算行数
            rowNum = getRowNum(data.size());
            int childCount = getChildCount();
            int size = data.size();
            //判断是否有足够的子View，不够就加
            if (size > childCount) {
                addChildViews(size - childCount);
            }
        } else {
            setVisibility(GONE);
            return;
        }
    }

    /**
     * 计算行数
     *
     * @param count
     * @return
     */
    protected int getRowNum(int count) {
        if (count % itemRowViewCount == 0) {
            return count / itemRowViewCount;
        } else {
            return (count / itemRowViewCount) + 1;
        }
    }

    public int getDataSize() {
        return ValidateUtil.isValidate(mData) ? mData.size() : 0;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setClickListener(OnGridItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface OnGridItemClickListener {
        void onImageClick(int position, View v);
    }
}
