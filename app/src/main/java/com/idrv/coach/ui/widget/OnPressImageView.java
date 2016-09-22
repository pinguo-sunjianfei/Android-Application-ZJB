package com.idrv.coach.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.utils.PixelUtil;

/**
 * time: 15/10/4
 * description:相册界面的item
 *
 * @author sunjianfei
 */
public class OnPressImageView extends ImageView {
    private boolean isSelected;
    private Paint mPaint;
    private float mStrokeWidth;

    public OnPressImageView(Context context) {
        super(context);
        initPaint();
    }

    public OnPressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public OnPressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.themes_main));
        mStrokeWidth = PixelUtil.dp2px(4.f);
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelected) {
            canvas.save();
            canvas.drawARGB(137, 0, 0, 0);
            int width = getWidth();
            int height = getHeight();
            float[] pixels = {0.f, 0.f, width, 0.f,
                    width, 0.f, width, height,
                    width, height, 0.f, height,
                    0.f, height, 0.f, 0.f};
            canvas.drawLines(pixels, mPaint);
            canvas.restore();
        }
    }
}
