package com.idrv.coach.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.idrv.coach.R;

/**
 * time: 15/7/27
 * description:小红点View
 *
 * @author sunjianfei
 */
public class RedPointView extends TextView {
    private int defaultSize = 30;
    private Paint mRedPaint;
    private int mWidth, mHeight;
    private float mRadius;

    public RedPointView(Context context) {
        super(context, null);
    }

    public RedPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RedPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRedPaint = new Paint();
        mRedPaint.setColor(getResources().getColor(R.color.themes_main));
        mRedPaint.setStyle(Paint.Style.FILL);
        mRedPaint.setAntiAlias(true);
    }

    public void setUnRedNumText(int num) {
        if (num <= 0) {
            setVisibility(GONE);
        } else {

            setVisibility(VISIBLE);
            num = num >= 99 ? 99 : num;
            setText(String.valueOf(num));
        }
    }

    public void setSize(int size) {
        defaultSize = size;
        setText("");
        requestLayout();
    }

    public void setShowStatus(boolean isShow) {
        setVisibility(isShow ? GONE : VISIBLE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        boolean measureDefault = false;
        if (widthMode != MeasureSpec.EXACTLY) {
            measureDefault = true;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            measureDefault = true;
        }
        if (measureDefault) {
            setMeasuredDimension(defaultSize, defaultSize);
        } else {
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mWidth = right - left;
            mHeight = bottom - top;
            int size = Math.min(mWidth, mHeight);
            mRadius = size * 1.0f / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float cx = mWidth * 1.0f / 2;
        float cy = mHeight * 1.0f / 2;
        canvas.drawCircle(cx, cy, mRadius, mRedPaint);
        super.onDraw(canvas);
    }
}

