package com.idrv.coach.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.idrv.coach.utils.BitmapUtil;

/**
 * time:2016/8/10
 * description:
 *
 * @author sunjianfei
 */
public class CenterCropImageView extends ImageView {
    int mWidth;
    int mHeight;

    public CenterCropImageView(Context context) {
        super(context);
    }

    public CenterCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CenterCropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        Bitmap bitmap = BitmapUtil.resizeBitmapByCenterCrop(bm, mWidth, mHeight);
        super.setImageBitmap(bitmap);
    }
}
