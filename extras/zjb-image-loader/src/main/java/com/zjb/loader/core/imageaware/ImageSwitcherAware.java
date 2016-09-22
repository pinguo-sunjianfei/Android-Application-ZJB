package com.zjb.loader.core.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;

import com.zjb.loader.internal.core.imageaware.ViewAware;
import com.zjb.loader.view.AnimationImageView;


/**
 * time: 15/6/11
 * description:对imageSwiter的包装
 *
 * @author sunjianfei
 */
public class ImageSwitcherAware extends ViewAware {
    public ImageSwitcherAware(View view) {
        super(view);
    }

    public ImageSwitcherAware(View view, boolean checkActualViewSize) {
        super(view, checkActualViewSize);
    }

    protected void setImageDrawableInto(Drawable drawable, View view) {
        ((ImageSwitcher) view).setImageDrawable(drawable);
    }

    protected void setImageBitmapInto(Bitmap bitmap, View view) {
        ((ImageSwitcher) view).setImageDrawable(new BitmapDrawable(view.getResources(), bitmap));
    }

    @Override
    public int getHeight() {
        View view = viewRef.get();
        if (view != null) {
            final ViewGroup.LayoutParams params = view.getLayoutParams();
            int height = 0;
            if (view instanceof AnimationImageView) {
                AnimationImageView iv = (AnimationImageView) view;
                height = iv.getQiniuHeight();
            }
            if (height <= 0 && checkActualViewSize && params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = view.getHeight(); // Get actual image height
            }
            if (height <= 0 && params != null)
                height = params.height; // Get layout height parameter


            return height;
        }
        return 0;
    }

    @Override
    public int getWidth() {
        View view = viewRef.get();
        if (view != null) {
            final ViewGroup.LayoutParams params = view.getLayoutParams();
            int width = 0;
            if (view instanceof AnimationImageView) {
                AnimationImageView iv = (AnimationImageView) view;
                width = iv.getQiniuWidth();
            }
            if (width <= 0 && checkActualViewSize && params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
                width = view.getWidth(); // Get actual image width
            }
            if (width <= 0 && params != null) width = params.width; // Get layout width parameter
            return width;
        }
        return 0;
    }


}
