package com.zjb.loader.core.display;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

import com.zjb.loader.internal.core.assist.LoadedFrom;
import com.zjb.loader.internal.core.imageaware.ImageAware;


/**
 * time: 15/6/11 
 * description:环形图片显示
 *
 * @author sunjianfei
 */
public class CircleRingBitmapDisplayer extends CircleBitmapDisplayer {
	private float mStrokeWidth;
	private int mColor;
	private float mRingPadding;

	public CircleRingBitmapDisplayer() {
	}

	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		imageAware.setImageDrawable(new CircleRingDrawable(bitmap, mStrokeWidth, mColor,
				mRingPadding));
	}

	public static class CircleRingDrawable extends CircleDrawable {
		private float mStrokeWidth;
		private int mColor;
		private float mRingPadding;

		public CircleRingDrawable(Bitmap bitmap, float strokeWidth, int color, float ringPadding) {
			super(bitmap);
			this.mStrokeWidth = strokeWidth;
			this.mColor = color;
			this.mRingPadding = ringPadding;
		}

		public void draw(Canvas canvas) {
			float ringMargin = mStrokeWidth;
			RectF rectRing = new RectF(this.mRect.left + ringMargin, this.mRect.top + ringMargin, this.mRect.right
					- ringMargin, this.mRect.bottom - ringMargin);
			Paint paint1 = new Paint();
			paint1.setAntiAlias(true);
			paint1.setColor(mColor);
			paint1.setStyle(Style.STROKE);
			paint1.setStrokeWidth(mStrokeWidth*2);
			canvas.drawOval(rectRing, paint1);
			 canvas.drawCircle(this.mRect.width() / 2.0F, this.mRect.height() / 2.0F, this.mRect.width() / 2.0F
			 - mRingPadding*2 - mStrokeWidth*2, this.paint);
		}
	}

	public CircleRingBitmapDisplayer setStrokeWidth(float width) {
		this.mStrokeWidth = width;
		return this;
	}

	public CircleRingBitmapDisplayer setColor(int color) {
		this.mColor = color;
		return this;
	}

	public CircleRingBitmapDisplayer setRingPadding(float ringPadding) {
		this.mRingPadding = ringPadding;
		return this;
	}
}
