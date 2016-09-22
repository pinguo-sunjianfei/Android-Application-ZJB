package com.zjb.loader.core.display;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

import com.zjb.loader.internal.core.assist.LoadedFrom;
import com.zjb.loader.internal.core.imageaware.ImageAware;

/**
 * time: 15/6/11
 * description:淡入显示圆形图片
 *
 * @author sunjianfei
 */
public class CircleFadeInBitmapDisplayer extends CircleBitmapDisplayer {
	private final int durationMillis;
	private final boolean animateFromNetwork;
	private final boolean animateFromDisk;
	private final boolean animateFromMemory;

	public CircleFadeInBitmapDisplayer(int durationMillis) {
		this(durationMillis, true, true, true);
	}

	public CircleFadeInBitmapDisplayer(int durationMillis, boolean animateFromNetwork, boolean animateFromDisk,
			boolean animateFromMemory) {
		this.durationMillis = durationMillis;
		this.animateFromNetwork = animateFromNetwork;
		this.animateFromDisk = animateFromDisk;
		this.animateFromMemory = animateFromMemory;
	}

	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		super.display(bitmap, imageAware, loadedFrom);
		if (this.animateFromNetwork && loadedFrom == LoadedFrom.NETWORK || this.animateFromDisk
				&& loadedFrom == LoadedFrom.DISC_CACHE || this.animateFromMemory
				&& loadedFrom == LoadedFrom.MEMORY_CACHE) {
			animate(imageAware.getWrappedView(), this.durationMillis);
		}

	}

	public static void animate(View imageView, int durationMillis) {
		if (imageView != null) {
			AlphaAnimation fadeImage = new AlphaAnimation(0.0F, 1.0F);
			fadeImage.setDuration((long) durationMillis);
			fadeImage.setInterpolator(new DecelerateInterpolator());
			imageView.startAnimation(fadeImage);
		}

	}
}
