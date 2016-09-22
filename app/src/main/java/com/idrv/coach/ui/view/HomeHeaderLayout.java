package com.idrv.coach.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * time:2016/3/9
 * description:主界面的header部分
 *
 * @author sunjianfei
 */
public class HomeHeaderLayout extends RelativeLayout {

    public HomeHeaderLayout(Context context) {
        super(context);
    }

    public HomeHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        int width = View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY);
        int height = View.MeasureSpec.makeMeasureSpec(w * 3 / 5, View.MeasureSpec.EXACTLY);
        super.onMeasure(width, height);
    }
}
