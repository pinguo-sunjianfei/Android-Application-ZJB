package com.zjb.loader.core.display;

import android.graphics.Bitmap;

import com.zjb.loader.core.util.GaussianBlur;
import com.zjb.loader.internal.core.assist.LoadedFrom;
import com.zjb.loader.internal.core.display.BitmapDisplayer;
import com.zjb.loader.internal.core.imageaware.ImageAware;

/**
 * time: 15/6/11
 * description:显示高斯模糊的图片
 *
 * @author sunjianfei
 */
public class BlurBitmapDisplayer implements BitmapDisplayer {
    private final int depth;

    public BlurBitmapDisplayer(int depth) {
        this.depth = depth;
    }

    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        GaussianBlur blurProcess = new GaussianBlur();
        Bitmap blurBitmap = blurProcess.blur(bitmap, (float) this.depth);
        if (blurBitmap != null && !blurBitmap.isRecycled()) {
            imageAware.setImageBitmap(blurBitmap);
        }
    }
}
