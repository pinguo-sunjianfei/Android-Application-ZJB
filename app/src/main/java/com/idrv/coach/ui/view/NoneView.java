package com.idrv.coach.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * time: 15/7/27
 * description:没有大小的view，主要用在解决listView未知类型item返回null会崩溃问题
 * 如果listView的类型未知，返回一个NoneView的实例对象
 *
 * @author crab
 */
public class NoneView extends View {
    public NoneView(Context context) {
        super(context, null);
    }

    public NoneView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public NoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (getVisibility() == VISIBLE) {
            setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //no size
        setMeasuredDimension(0, 0);
    }

    @Override
    public void draw(Canvas canvas) {
        //do nothing
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //no draw,这里执行不到，还是重写了
    }
}
