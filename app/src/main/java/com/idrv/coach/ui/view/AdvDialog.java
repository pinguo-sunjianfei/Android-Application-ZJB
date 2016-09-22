package com.idrv.coach.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.utils.helper.ResHelper;
import com.zjb.loader.ZjbImageLoader;

/**
 * time:2016/4/26
 * description:
 *
 * @author sunjianfei
 */
public class AdvDialog extends Dialog implements View.OnClickListener {
    private OnBottomButtonClick mListener;
    private ImageView mImageView;

    public AdvDialog(Context context) {
        super(context, R.style.BaseDialog);
        //初始化布局
        setContentView(R.layout.vw_adv_dialog);
        mImageView = (ImageView) findViewById(R.id.image_view);

        Window dialogWindow = getWindow();
        int width = ResHelper.getScreenWidth() * 560 / 720;
        int height = ResHelper.getScreenHeight() * 620 / 1280;
        ViewGroup.LayoutParams lp = mImageView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);
        findViewById(R.id.btn_view).setOnClickListener(this);
        mImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_view:
            case R.id.btn_view:
                if (null != mListener) {
                    mListener.onDialogButtonClick();
                }
                break;
        }
    }

    public void showImage(String url) {
        ZjbImageLoader.create(url)
                .setBitmapConfig(Bitmap.Config.RGB_565)
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                .into(mImageView);
    }

    public void setClickListener(OnBottomButtonClick listener) {
        mListener = listener;
    }

    public interface OnBottomButtonClick {
        void onDialogButtonClick();
    }
}
