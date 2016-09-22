package com.zjb.loader.core.display;

import android.graphics.Bitmap;

import com.zjb.loader.core.util.GaussianBlur;
import com.zjb.loader.internal.core.assist.LoadedFrom;
import com.zjb.loader.internal.core.imageaware.ImageAware;

/**
 * time: 15/6/11
 * description:带高斯模糊的圆角矩形图片
 *
 * @author sunjianfei
 */
public class RoundedBlurBitmapDisplayer extends RoundedBitmapDisplayer {
	private final int depth;

	public RoundedBlurBitmapDisplayer(int cornerRadiusPixels, int depth) {
		super(cornerRadiusPixels);
		this.depth = depth;
	}

	public RoundedBlurBitmapDisplayer(int cornerRadiusPixels, int marginPixels, int depth) {
		super(cornerRadiusPixels, marginPixels);
		this.depth = depth;
	}

	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		GaussianBlur blurProcess = new GaussianBlur();
		Bitmap blurBitmap = blurProcess.blur(bitmap, (float)this.depth);
//Bitmap blurBitmap = BlurUtil.blurBitmap( bitmap, depth);
		if (blurBitmap != null && !blurBitmap.isRecycled()) {
			imageAware.setImageDrawable(new RoundedDrawable(blurBitmap, this.cornerRadius, this.margin));
		}

	}
}
