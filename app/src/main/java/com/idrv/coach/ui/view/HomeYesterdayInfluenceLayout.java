package com.idrv.coach.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/3/9
 * description:
 *
 * @author sunjianfei
 */
public class HomeYesterdayInfluenceLayout extends LinearLayout {
    @InjectView(R.id.num_text_view)
    TextView mNumTv;
    @InjectView(R.id.title_tv)
    TextView mTitleTv;


    public HomeYesterdayInfluenceLayout(Context context) {
        super(context);
    }

    public HomeYesterdayInfluenceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeYesterdayInfluenceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setTitle(int resId) {
        mTitleTv.setText(resId);
    }

    public void setNumValue(String num) {
        mNumTv.setText(num);
    }
}
