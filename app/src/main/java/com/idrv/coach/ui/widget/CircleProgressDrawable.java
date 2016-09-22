package com.idrv.coach.ui.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * time: 2015/9/22
 * description:
 *
 * @author sunjianfei
 */
public class CircleProgressDrawable extends Drawable {
    private Paint mBackgroundPaint;
    private Paint mRotatePaint;
    private RectF fBounds;
    private float mBorderWidth = 5.f;
    private int mArcColor = 0xfffdd600;
    private int mBgColor = 0xffd7d7d7;


    public CircleProgressDrawable() {
        this.fBounds = new RectF();
    }

    public void setArcColor(int color) {
        this.mArcColor = color;
    }

    public void setBackgroundColor(int color) {
        this.mBgColor = color;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mBorderWidth = strokeWidth;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.fBounds.left = (float) bounds.left + this.mBorderWidth / 2.0F + 0.5F;
        this.fBounds.right = (float) bounds.right - this.mBorderWidth / 2.0F - 0.5F;
        this.fBounds.top = (float) bounds.top + this.mBorderWidth / 2.0F + 0.5F;
        this.fBounds.bottom = (float) bounds.bottom - this.mBorderWidth / 2.0F - 0.5F;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        initPaints();
        //1.绘制一个圆形
        canvas.drawArc(this.fBounds, 0.f, 360.f, false, this.mBackgroundPaint);
        //2.绘制一个圆环
        canvas.drawArc(this.fBounds, 0, -60f, false, this.mRotatePaint);
        canvas.restore();
    }

    private void initPaints() {
        //1.创建绘制背景的画笔
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xff000000 | mBgColor);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(mBorderWidth);
        //2.创建绘制圆环的画笔
        mRotatePaint = new Paint();
        mRotatePaint.setColor(0xff000000 | mArcColor);
        mRotatePaint.setAntiAlias(true);
        mRotatePaint.setStyle(Paint.Style.STROKE);
        mRotatePaint.setStrokeWidth(mBorderWidth);
    }

    @Override
    public void setAlpha(int alpha) {
        mRotatePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mRotatePaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
