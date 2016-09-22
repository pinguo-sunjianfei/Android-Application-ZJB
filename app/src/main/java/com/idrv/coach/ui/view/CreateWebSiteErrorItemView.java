package com.idrv.coach.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/6/2
 * description:
 *
 * @author sunjianfei
 */
public class CreateWebSiteErrorItemView extends LinearLayout {
    @InjectView(R.id.title_tv)
    TextView mTitleTv;
    @InjectView(R.id.subject_tv)
    TextView mSubjectTv;
    @InjectView(R.id.button)
    TextView mButton;

    public CreateWebSiteErrorItemView(Context context) {
        super(context);
    }

    public CreateWebSiteErrorItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setTitle(int resId) {
        mTitleTv.setText(resId);
    }

    public void setTitleDrawable(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        mTitleTv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public void setSubject(int resId) {
        mSubjectTv.setText(resId);
    }

    public void setButtonText(int resId) {
        mButton.setText(resId);
    }

    public void setOnclickListener(OnClickListener listener) {
        mButton.setOnClickListener(listener);
    }
}
