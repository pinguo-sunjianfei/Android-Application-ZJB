package com.zjb.loader.core.display;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;

import com.zjb.loader.internal.core.assist.LoadedFrom;
import com.zjb.loader.internal.core.imageaware.ImageAware;

/**
 * time:2016/6/22
 * description:显示正六边形的图片
 *
 * @author sunjianfei
 */
public class PolygonBitmapDisplayer extends CircleBitmapDisplayer {
    private float mHeight;

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        imageAware.setImageDrawable(new PolygonDrawable(bitmap, mHeight));
    }

    //六边形Drawable
    public static class PolygonDrawable extends CircleDrawable {
        //矩形的宽高
        private float mHeight = 100;
        private Path mPath;

        public PolygonDrawable(Bitmap bitmap, float height) {
            super(bitmap);
            if (height != 0) {
                mHeight = height;
            }
            mPath = new Path();
        }

        @Override
        public void draw(Canvas canvas) {
            //正六边形的边长 mHeight * sin30
            float mLength = mHeight / 2;
            //正六边形的高 mHeight * cos30
            float height = mHeight * ((float) Math.sqrt(3) / 2);

            mPath.moveTo(mLength / 2, 0);
            mPath.lineTo(0, height / 2);
            mPath.lineTo(mLength / 2, height);
            mPath.lineTo(mLength * 1.5f, height);
            mPath.lineTo(2 * mLength, height / 2);
            mPath.lineTo(mLength * 1.5f, 0);
            mPath.lineTo(mLength / 2, 0);
            mPath.close();
            //绘制
            canvas.drawPath(mPath, paint);
        }
    }

    public PolygonBitmapDisplayer setHeight(float mHeight) {
        this.mHeight = mHeight;
        return this;
    }
}
