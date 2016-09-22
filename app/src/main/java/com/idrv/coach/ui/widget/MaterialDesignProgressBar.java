package com.idrv.coach.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.utils.PixelUtil;

/**
 * time:2016/7/22
 * description:匀加速的圆形进度条
 *
 * @author sunjianfei
 */
public class MaterialDesignProgressBar extends View {
    private Path mPath;
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private float mAnimatorValue;
    private Path mDst;
    private float mLength;
    ValueAnimator valueAnimator;

    boolean isInit;

    private int mStokeWidth;
    private int mStrokeColor;

    public MaterialDesignProgressBar(Context context) {
        super(context);
    }

    public MaterialDesignProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialDesignProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs, defStyleAttr);
    }

    private void initAttr(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MaterialDesignProgressBar, defStyle, 0);

        mStokeWidth = a.getDimensionPixelSize(R.styleable.MaterialDesignProgressBar_progress_stroke_width, (int) PixelUtil.dp2px(2));
        mStrokeColor = a.getColor(R.styleable.MaterialDesignProgressBar_progress_stroke_color, 0xff000000);
    }

    private void init(int width) {
        isInit = true;
        float size = width * 1.0f / 2;
        mPathMeasure = new PathMeasure();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(mStrokeColor);
        mPath = new Path();
        mPath.addCircle(size, size, size - mStokeWidth, Path.Direction.CW);
        mPathMeasure.setPath(mPath, true);
        mLength = mPathMeasure.getLength();
        mDst = new Path();

        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAnimatorValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setDuration(1500);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isInit) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            init(width);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDst.reset();
        // 硬件加速的BUG
        mDst.lineTo(0, 0);
        float stop = mLength * mAnimatorValue;
        float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * mLength));
        mPathMeasure.getSegment(start, stop, mDst, true);
        canvas.drawPath(mDst, mPaint);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        //如果View状态发生变化,开/关插值计算
        if (null != valueAnimator) {
            if (visibility == GONE) {
                valueAnimator.end();
            } else {
                valueAnimator.start();
            }
        }
    }
}
