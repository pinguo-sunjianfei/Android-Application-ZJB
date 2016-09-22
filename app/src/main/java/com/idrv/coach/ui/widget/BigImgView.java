package com.idrv.coach.ui.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.idrv.coach.R;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.helper.ViewUtils;


/**
 * time: 2016/3/18
 * description: 点击查看大图（一个popupWindow）
 *
 * @author bigflower
 */
public class BigImgView implements View.OnClickListener {

    final PopupWindow popupWindow;

    public BigImgView(final Context context, String url, final View view) {
        // 一个自定义的布局，作为显示的内容
        final View contentView = LayoutInflater.from(context).inflate(
                R.layout.vw_bigimg, null);

        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);

        ImageView imageView = (ImageView) contentView.findViewById(R.id.bigImg_img);
        ViewUtils.showImage(imageView, url);

        Logger.i("显示图片："+url);
        imageView.setOnClickListener(this);
        contentView.findViewById(R.id.bigImgPop_layout).setOnClickListener(this);

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
    }


    @Override
    public void onClick(View v) {
        if(popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}
