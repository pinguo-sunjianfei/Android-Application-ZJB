package com.idrv.coach.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.utils.PixelUtil;

/**
 * time:2016/3/14
 * description:仿笔记本效果的textView
 *
 * @author sunjianfei
 */
public class LinedTextView extends TextView {
    public LinedTextView(Context context) {
        super(context);
    }

    public LinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint mPaint = new Paint();
//       mPaint.setColor(0x80000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(getResources().getColor(R.color.h_divider_line_color));

        int right = getRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int height = getHeight();
        int lineHeight = getLineHeight();
        int spacingHeight = (int) PixelUtil.dp2px(3);
        if (Build.VERSION.SDK_INT >= 16) {
            spacingHeight = (int) getLineSpacingExtra();
        }
        height = height + spacingHeight;//把最后一个行间距也计算进去
        int count = (height - paddingTop - paddingBottom) / lineHeight;


        for (int i = 0; i < count; i++) {
            int baseline = lineHeight * (i + 1) + paddingTop - spacingHeight / 2;
            canvas.drawLine(0 + paddingLeft, baseline, right - paddingRight, baseline, mPaint);
        }
        super.onDraw(canvas);
    }
}
