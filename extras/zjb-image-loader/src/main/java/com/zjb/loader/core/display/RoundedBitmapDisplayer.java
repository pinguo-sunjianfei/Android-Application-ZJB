package com.zjb.loader.core.display;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

import com.zjb.loader.internal.core.assist.LoadedFrom;
import com.zjb.loader.internal.core.display.BitmapDisplayer;
import com.zjb.loader.internal.core.imageaware.ImageAware;

/**
 * time: 15/6/11
 * description:圆角矩形显示图片
 *
 * @author sunjianfei
 */
public class RoundedBitmapDisplayer implements BitmapDisplayer {
 protected final int cornerRadius;
 protected final int margin;

 public RoundedBitmapDisplayer(int cornerRadiusPixels) {
     this(cornerRadiusPixels, 0);
 }

 public RoundedBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
     this.cornerRadius = cornerRadiusPixels;
     this.margin = marginPixels;
 }

 public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
     imageAware.setImageDrawable(new RoundedDrawable(bitmap, this.cornerRadius, this.margin));
 }

 public static class RoundedDrawable extends Drawable {
     protected final float cornerRadius;
     protected final int margin;
     protected final RectF mRect = new RectF();
     protected final Rect mBitmapRect;
     protected final BitmapShader bitmapShader;
     protected final Paint paint;

     public RoundedDrawable(Bitmap bitmap, int cornerRadius, int margin) {
         this.cornerRadius = (float)cornerRadius;
         this.margin = margin;
         this.bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
         this.mBitmapRect = new Rect(margin, margin, bitmap.getWidth() - margin, bitmap.getHeight() - margin);
         this.paint = new Paint();
         this.paint.setAntiAlias(true);
         this.paint.setShader(this.bitmapShader);
     }

     protected void onBoundsChange(Rect bounds) {
         super.onBoundsChange(bounds);
         this.mRect.set((float)this.margin, (float)this.margin, (float)(bounds.width() - this.margin), (float)(bounds.height() - this.margin));
         Matrix shaderMatrix = new Matrix();
         float dx = 0.0F;
         float dy = 0.0F;
         int dwidth = this.mBitmapRect.width();
         int dheight = this.mBitmapRect.height();
         int vwidth = bounds.width() - this.margin;
         int vheight = bounds.height() - this.margin;
         float scale;
         if(dwidth * vheight > vwidth * dheight) {
             scale = (float)vheight / (float)dheight;
             dx = ((float)vwidth - (float)dwidth * scale) * 0.5F;
         } else {
             scale = (float)vwidth / (float)dwidth;
             dy = ((float)vheight - (float)dheight * scale) * 0.5F;
         }

         shaderMatrix.setScale(scale, scale);
         shaderMatrix.postTranslate((float)((int)(dx + 0.5F)), (float)((int)(dy + 0.5F)));
         this.bitmapShader.setLocalMatrix(shaderMatrix);
     }

     public void draw(Canvas canvas) {
         canvas.drawRoundRect(this.mRect, this.cornerRadius, this.cornerRadius, this.paint);
     }

     public int getOpacity() {
         return -3;
     }

     public void setAlpha(int alpha) {
         this.paint.setAlpha(alpha);
     }

     public void setColorFilter(ColorFilter cf) {
         this.paint.setColorFilter(cf);
     }
 }
}

