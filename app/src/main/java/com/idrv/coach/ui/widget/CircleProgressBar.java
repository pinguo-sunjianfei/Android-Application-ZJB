package com.idrv.coach.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.idrv.coach.R;


/**
 * time: 2015/9/22
 * description:圆形的进度条
 *
 * @author sunjianfei
 */
public class CircleProgressBar extends ImageView {

    private Animation mAnimation;

    public CircleProgressBar(Context context) {
        super(context);
        initView(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        CircleProgressDrawable drawable = new CircleProgressDrawable();
        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, 0, 0);
            int arcColor = a.getColor(R.styleable.CircleProgressBar_color_arc, 0);
            int bgColor = a.getColor(R.styleable.CircleProgressBar_color_background, 0);
            float strokeWidth = a.getDimension(R.styleable.CircleProgressBar_stroke_width, 5.f);
            drawable.setStrokeWidth(strokeWidth);
            drawable.setArcColor(arcColor);
            drawable.setBackgroundColor(bgColor);
            a.recycle();
        }
        setImageDrawable(drawable);
        mAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setDuration(800);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(RotateAnimation.RESTART);
        startAnimation(mAnimation);
    }

    @Override
    public void setVisibility(int visibility) {
        if (GONE == visibility || INVISIBLE == visibility) {
            clearAnimation();
            super.setVisibility(visibility);
        } else {
            super.setVisibility(visibility);
            startAnimation(mAnimation);
        }

    }
}
