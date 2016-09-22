package com.zjb.loader.core.display;


import android.graphics.Bitmap;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.PorterDuff.Mode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;

import com.zjb.loader.internal.core.assist.LoadedFrom;
import com.zjb.loader.internal.core.imageaware.ImageAware;

/**
 * time: 15/6/11
 * description:带阴影(LOMO)效果的圆角矩形
 *
 * @author sunjianfei
 */
public class RoundedLomoBitmapDisplayer extends RoundedBitmapDisplayer {
 public RoundedLomoBitmapDisplayer(int cornerRadiusPixels) {
     super(cornerRadiusPixels);
 }

 public RoundedLomoBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
     super(cornerRadiusPixels, marginPixels);
 }

 public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
     imageAware.setImageDrawable(new RoundedVignetteDrawable(bitmap, this.cornerRadius, this.margin));
 }

 protected static class RoundedVignetteDrawable extends RoundedDrawable {
     RoundedVignetteDrawable(Bitmap bitmap, int cornerRadius, int margin) {
         super(bitmap, cornerRadius, margin);
     }

     protected void onBoundsChange(Rect bounds) {
         super.onBoundsChange(bounds);
         RadialGradient vignette = new RadialGradient(this.mRect.centerX(), this.mRect.centerY() * 1.0F / 0.7F, this.mRect.centerX() * 1.3F, new int[]{0, 0, 2130706432}, new float[]{0.0F, 0.7F, 1.0F}, TileMode.CLAMP);
         Matrix oval = new Matrix();
         oval.setScale(1.0F, 0.7F);
         vignette.setLocalMatrix(oval);
         this.paint.setShader(new ComposeShader(this.bitmapShader, vignette, Mode.SRC_OVER));
     }
 }
}

