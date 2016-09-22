package com.idrv.coach.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/4/20
 * description:
 *
 * @author sunjianfei
 */
public class WebSiteItemView extends LinearLayout {
    @InjectView(R.id.main_text_view)
    TextView mMainTextView;
    @InjectView(R.id.sub_text_view)
    TextView mSubTextView;

    public WebSiteItemView(Context context) {
        super(context);
    }

    public WebSiteItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebSiteItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        int width = View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY);
        int height = View.MeasureSpec.makeMeasureSpec(w * 3 / 4, View.MeasureSpec.EXACTLY);
        super.onMeasure(width, height);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setIcon(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        mMainTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }

    public void setTitle(int resId) {
        mMainTextView.setText(resId);
    }

    public void setSubText(String text) {
        mSubTextView.setText(text);
    }

}
