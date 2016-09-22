package com.idrv.coach.ui.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.ResHelper;
import com.zjb.loader.ZjbImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/8/22
 * description:
 *
 * @author sunjianfei
 */
public class DrivingInsDetailItemView extends LinearLayout {
    @InjectView(R.id.image)
    ImageView mImageView;
    @InjectView(R.id.text)
    TextView mTextView;

    public DrivingInsDetailItemView(Context context) {
        super(context);
    }

    public DrivingInsDetailItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrivingInsDetailItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setImage(String url) {
        int width = (int) (ResHelper.getScreenWidth() - PixelUtil.dp2px(20));
        int height = (int) PixelUtil.dp2px(160);
        ZjbImageLoader.create(url)
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setQiniu(width, height)
                .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                .into(mImageView);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

}
