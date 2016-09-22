package com.zjb.loader.core.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.zjb.loader.internal.core.imageaware.ViewAware;

/**
 * time: 15/6/11
 * description:
 *
 * @author sunjianfei
 */
public class SimpleViewAware extends ViewAware {
	public SimpleViewAware(View view) {
		super(view);
	}

	public SimpleViewAware(View view, boolean checkActualViewSize) {
		super(view, checkActualViewSize);
	}

	@SuppressWarnings("deprecation")
	protected void setImageDrawableInto(Drawable drawable, View view) {
		view.setBackgroundDrawable(drawable);
	}

	@SuppressWarnings("deprecation")
	protected void setImageBitmapInto(Bitmap bitmap, View view) {
		view.setBackgroundDrawable(new BitmapDrawable(view.getResources(), bitmap));
	}
}
