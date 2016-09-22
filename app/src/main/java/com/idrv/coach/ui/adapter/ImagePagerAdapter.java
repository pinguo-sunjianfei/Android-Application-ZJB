package com.idrv.coach.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;

import com.idrv.coach.bean.Picture;
import com.idrv.coach.ui.widget.CenterCropImageView;
import com.idrv.coach.utils.PixelUtil;
import com.zjb.loader.ZjbImageLoader;

/**
 * time:2016/8/9
 * description:纯图片的Page
 *
 * @author sunjianfei
 */
public class ImagePagerAdapter extends AbsPagerAdapter<Picture> {
    int height = (int) PixelUtil.dp2px(180);
    int width = (int) PixelUtil.dp2px(240);
    OnImageClickListener mOnImageClickListener;

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mOnImageClickListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Picture picture = mData.get(position);
        CenterCropImageView imageView = new CenterCropImageView(container.getContext());

        int w = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int h = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        imageView.measure(w, h);

        container.addView(imageView);
        ZjbImageLoader.create(picture.getUrl())
                .setDisplayType(ZjbImageLoader.DISPLAY_FADE_IN)
                .setQiniu(width, height)
                .setBitmapConfig(Bitmap.Config.RGB_565)
                .setFadeInTime(1000)
                .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                .into(imageView);
        imageView.setOnClickListener(v -> {
            if (null != mOnImageClickListener) {
                mOnImageClickListener.onClick(position);
            }
        });
        return imageView;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    interface OnImageClickListener {
        void onClick(int position);
    }
}
