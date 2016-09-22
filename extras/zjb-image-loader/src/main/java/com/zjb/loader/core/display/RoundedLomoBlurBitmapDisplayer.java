package com.zjb.loader.core.display;

import android.graphics.Bitmap;

import com.zjb.loader.core.util.GaussianBlur;
import com.zjb.loader.internal.core.assist.LoadedFrom;
import com.zjb.loader.internal.core.imageaware.ImageAware;

/**
 * time: 15/6/11
 * description:带阴影(LOMO)效果的模糊圆角矩形
 *
 * @author sunjianfei
 */
public class RoundedLomoBlurBitmapDisplayer extends RoundedLomoBitmapDisplayer {
	private final int depth;

	public RoundedLomoBlurBitmapDisplayer(int cornerRadiusPixels, int depth) {
		super(cornerRadiusPixels);
		this.depth = depth;
	}

	public RoundedLomoBlurBitmapDisplayer(int cornerRadiusPixels, int marginPixels, int depth) {
		super(cornerRadiusPixels, marginPixels);
		this.depth = depth;
	}

	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		GaussianBlur blurProcess = new GaussianBlur();
		Bitmap blurBitmap = blurProcess.blur(bitmap, (float)this.depth);
//Bitmap blurBitmap = BlurUtil.blurBitmap( bitmap, depth);
		if (blurBitmap != null && !blurBitmap.isRecycled()) {
			imageAware.setImageDrawable(new RoundedVignetteDrawable(blurBitmap, this.cornerRadius, this.margin));
		}

	}
}
