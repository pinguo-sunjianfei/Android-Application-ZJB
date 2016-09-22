package com.idrv.coach.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.utils.PixelUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/6/28
 * description:兑换传播工具的对话框
 *
 * @author sunjianfei
 */
public class ExchangeSpreadToolDialog extends Dialog {
    @InjectView(R.id.title_tv)
    TextView mTitleTv;
    @InjectView(R.id.subtitle_tv)
    TextView mSubtitleTv;
    @InjectView(R.id.btn_integral)
    Button mIntegralBtn;
    @InjectView(R.id.btn_member)
    Button mMemberBtn;
    @InjectView(R.id.btn_other)
    TextView mOtherBtn;


    @InjectView(R.id.other_layout)
    View mOtherLayout;

    //正常情况,只有两项
    public static final int ITEM_SHOW_TYPE_NORMAL = 0;
    //会员免费,是会员的情况
    public static final int ITEM_SHOW_TYPE_MEMBER = 1;
    //会员免费，不是会员的情况
    public static final int ITEM_SHOW_TYPE_NOT_MEMBER = 2;

    public ExchangeSpreadToolDialog(Context context) {
        super(context, R.style.SelectPhotoDialogStyle);

        //初始化布局
        setContentView(R.layout.vw_exchange_spread_tool_dialog);
        Window dialogWindow = getWindow();
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);
        setCanceledOnTouchOutside(true);
        setCancelable(true);

        ButterKnife.inject(this, this);
    }

    public void setTitle(int res) {
        mTitleTv.setText(res);
    }

    public void setSubtitle(String title) {
        mSubtitleTv.setText(title);
    }

    public void setMemberItemClickListener(View.OnClickListener listener) {
        mMemberBtn.setOnClickListener(listener);
    }

    public void setIntegralItemClickListener(View.OnClickListener listener) {
        mIntegralBtn.setOnClickListener(listener);
    }

    public void setOtherItemClickListener(View.OnClickListener listener) {
        mOtherLayout.setOnClickListener(listener);
    }

    /**
     * @param type          类型
     * @param secondItemStr 第二个item的文案
     * @param thirdItemStr  第三个item的文案
     */
    public void setItemShowType(int type, String secondItemStr, String thirdItemStr, boolean isWxPay) {

        if (type == ITEM_SHOW_TYPE_NORMAL) {
            //常规
            mIntegralBtn.setText(secondItemStr);
            mIntegralBtn.setBackgroundDrawable(getGradientDrawable(0xFFD13E3C));

            mOtherBtn.setText(thirdItemStr);
            if (isWxPay) {
                //如果是微信支付,改变样式
                mOtherBtn.setTextColor(0xFFFFFFFF);
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_we_chat_white);
                mOtherBtn.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                mOtherLayout.setBackgroundDrawable(getGradientDrawable(0xFF46A271));
            } else {
                //设置边框样式
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
                drawable.setStroke((int) PixelUtil.dp2px(1), 0xFFD13E3C);
                drawable.setCornerRadius(PixelUtil.dp2px(4));
                mOtherLayout.setBackgroundDrawable(drawable);
            }
        } else if (type == ITEM_SHOW_TYPE_MEMBER) {
            //会员item
            mMemberBtn.setVisibility(View.VISIBLE);
            mMemberBtn.setBackgroundDrawable(getGradientDrawable(0xFFD13E3C));

            //积分item
            mIntegralBtn.setText(secondItemStr);
            mIntegralBtn.setBackgroundDrawable(getGradientDrawable(0xFF9C9C9C));
            mIntegralBtn.setEnabled(false);

            mOtherBtn.setText(thirdItemStr);
            mOtherBtn.setTextColor(0xFFFFFFFF);
            mOtherLayout.setEnabled(false);
            mOtherLayout.setBackgroundDrawable(getGradientDrawable(0xFF9C9C9C));
            if (isWxPay) {
                //如果是微信支付,添加左图标
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_we_chat_white);
                mOtherBtn.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        } else {
            //非会员
            mMemberBtn.setVisibility(View.VISIBLE);
            mMemberBtn.setBackgroundDrawable(getGradientDrawable(0xFF9C9C9C));

            //积分item
            mIntegralBtn.setText(secondItemStr);
            mIntegralBtn.setBackgroundDrawable(getGradientDrawable(0xFFD13E3C));

            mOtherBtn.setText(thirdItemStr);
            if (isWxPay) {
                //如果是微信支付,改变样式
                mOtherBtn.setTextColor(0xFFFFFFFF);
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_we_chat_white);
                mOtherBtn.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                mOtherLayout.setBackgroundDrawable(getGradientDrawable(0xFF46A271));
            } else {
                mOtherBtn.setTextColor(0xFFD13E3C);
                //设置边框样式
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
                drawable.setStroke((int) PixelUtil.dp2px(1), 0xFFD13E3C);
                drawable.setCornerRadius(PixelUtil.dp2px(4));
                mOtherLayout.setBackgroundDrawable(drawable);
            }
        }
    }

    private GradientDrawable getGradientDrawable(int color) {
        float radius = PixelUtil.dp2px(4);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }
}
