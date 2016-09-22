package com.idrv.coach.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
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
public class HomeInfluenceLayout extends LinearLayout {
    @InjectView(R.id.left_image)
    ImageView mImageView;
    @InjectView(R.id.num_tv)
    TextView mNumTextView;
    @InjectView(R.id.title_tv)
    TextView mTitleTv;

    public HomeInfluenceLayout(Context context) {
        super(context);
    }

    public HomeInfluenceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeInfluenceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setImageIcon(int resId) {
        mImageView.setImageResource(resId);
    }

    public void setNumText(String num) {
        mNumTextView.setText(num);
    }

    public void setTitle(int resId) {
        mTitleTv.setText(resId);
    }
}
