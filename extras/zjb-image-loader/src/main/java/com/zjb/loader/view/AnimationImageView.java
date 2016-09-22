package com.zjb.loader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

/**
 * time: 15/6/11
 * description:封装了淡入淡出的ImageSwitcher
 *
 * @author sunjianfei
 */
public class AnimationImageView extends ImageSwitcher implements ViewFactory {

    private int qiniuWidth;
    private int qiniuHeight;

    public AnimationImageView(Context context) {
        super(context);
        initView();
    }

    public int getQiniuWidth() {
        return qiniuWidth;
    }

    public void setQiniuWidth(int qiniuWidth) {
        this.qiniuWidth = qiniuWidth;
    }

    public int getQiniuHeight() {
        return qiniuHeight;
    }

    public void setQiniuHeight(int qiniuHeight) {
        this.qiniuHeight = qiniuHeight;
    }

    public AnimationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        AlphaAnimation in = new AlphaAnimation(0.0f, 1.0f);
        in.setInterpolator(new LinearInterpolator());
        in.setDuration(800);
        AlphaAnimation out = new AlphaAnimation(1.0f, 0.0f);
        in.setInterpolator(new LinearInterpolator());
        out.setDuration(800);
        setInAnimation(in);
        setOutAnimation(out);
        setFactory(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public View makeView() {
        ImageView view = new ImageView(getContext());
        view.setBackgroundColor(0x00000000);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new LayoutParams(
                android.widget.Gallery.LayoutParams.MATCH_PARENT,
                android.widget.Gallery.LayoutParams.MATCH_PARENT));
        return view;
    }

}
