package com.idrv.coach.ui.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.loader.internal.core.assist.ImageScaleType;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/8/5
 * description:发现页item布局
 *
 * @author sunjianfei
 */
public class DiscoverItemLayout extends LinearLayout {
    @InjectView(R.id.top_image)
    ImageView mTopIv;
    @InjectView(R.id.bottom_tv)
    TextView mBottomTv;

    public DiscoverItemLayout(Context context) {
        super(context);
        initView();
    }

    public DiscoverItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DiscoverItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.vw_discover_item_layout, this);
        ButterKnife.inject(this, this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        int width = View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY);
        super.onMeasure(width, width);
    }

    public void setImage(int size, String url) {
        //1.改变尺寸
        LayoutParams lp = (LayoutParams) mTopIv.getLayoutParams();
        lp.width = size;
        lp.height = size;

        //2.加载图片
        ZjbImageLoader.create(url)
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                .setImageScaleType(ImageScaleType.EXACTLY)
                .into(mTopIv);
    }

    public void setBackgroundDrawable(int drawable) {
        setBackgroundResource(drawable);
    }

    public void setTextColor(int color) {
        mBottomTv.setTextColor(color);
    }

    public void setText(String text) {
        mBottomTv.setText(text);
    }
}
