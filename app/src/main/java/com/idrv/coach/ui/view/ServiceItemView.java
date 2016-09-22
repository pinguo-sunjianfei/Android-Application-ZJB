package com.idrv.coach.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.utils.helper.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/6/3
 * description:
 *
 * @author sunjianfei
 */
public class ServiceItemView extends RelativeLayout {
    @InjectView(R.id.left_image)
    ImageView mLeftImageView;
    @InjectView(R.id.service_name)
    TextView mServiceNameTv;
    @InjectView(R.id.service_desc)
    TextView mServiceDescTv;
    @InjectView(R.id.right_btn)
    ImageView mRightBtn;
    @InjectView(R.id.h_divider_line)
    View mLine;


    public ServiceItemView(Context context) {
        super(context);
        initView();
    }

    public ServiceItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ServiceItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.vw_my_service_item, this);
        ButterKnife.inject(this, this);
    }

    public void setLineStatus(boolean status) {
        mLine.setVisibility(status ? VISIBLE : GONE);
    }

    public void setLeftImage(String url) {
        ViewUtils.showImage(mLeftImageView, url);
    }

    public void setServicesName(String name) {
        mServiceNameTv.setText(name);
    }

    public void setStatus(boolean isOpened) {
        int resId = isOpened ? R.drawable.service_enable : R.drawable.service_disable;
        mRightBtn.setImageResource(resId);
    }

    public void setServicesDesc(String desc) {
        mServiceDescTv.setText(desc);
    }

    public void setClickListener(OnClickListener listener) {
        mRightBtn.setOnClickListener(listener);
    }
}
