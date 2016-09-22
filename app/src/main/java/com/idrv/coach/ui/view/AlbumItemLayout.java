package com.idrv.coach.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.zjb.loader.ZjbImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/5/23
 * description:
 *
 * @author sunjianfei
 */
public class AlbumItemLayout extends FrameLayout {
    @InjectView(R.id.image_view)
    ImageView mImageView;
    @InjectView(R.id.title)
    TextView mTitleTv;
    @InjectView(R.id.pic_num)
    TextView mPicNumTv;

    public AlbumItemLayout(Context context) {
        super(context);
        initView();
    }

    public AlbumItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AlbumItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.vw_album_item, this);
        ButterKnife.inject(this, this);
    }

    public void setImage(String url,@DrawableRes int emptyResId) {
        if (TextUtils.isEmpty(url)) {
            mImageView.setImageResource(emptyResId);
        } else {
            ZjbImageLoader.create(url)
                    .setBitmapConfig(Bitmap.Config.ARGB_8888)
                    .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                    .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                    .setEmptyDrawable(getResources().getDrawable(emptyResId))
                    .into(mImageView);
        }
    }

    public void setTitle(int resId) {
        mTitleTv.setText(resId);
    }

    public void setPicNum(String text) {
        mPicNumTv.setText(text);
    }
}
