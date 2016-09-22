package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.AdvShareInfo;
import com.idrv.coach.bean.SpreadTool;
import com.idrv.coach.bean.WebParamBuilder;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.bean.share.ShareWebProvider;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.helper.ViewUtils;
import com.idrv.coach.wxapi.WXEntryActivity;
import com.zjb.loader.ZjbImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/6/27
 * description:
 *
 * @author sunjianfei
 */
public class SpreadToolsActivity extends AbsPayActivity implements View.OnClickListener {
    private static final String KEY_PARAM = "param";

    @InjectView(R.id.content_iv)
    ImageView mContentIv;
    @InjectView(R.id.content_layout)
    LinearLayout mContentLayout;
    @InjectView(R.id.desc)
    TextView mDescTv;
    @InjectView(R.id.btn_preview)
    TextView mPreviewBtn;
    @InjectView(R.id.countdown_tv)
    TextView mCountDownTv;
    @InjectView(R.id.btn_activation)
    Button mActivationBtn;

    public static void launch(Context context, SpreadTool tool) {
        Intent intent = new Intent(context, SpreadToolsActivity.class);
        intent.putExtra(KEY_PARAM, tool);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_spread_tools);
        ButterKnife.inject(this);
        initToolBar();
        registerEvent();
    }

    @Override
    protected void refresh() {
        Subscription subscription = mViewModel.getCommission()
                .subscribe(__ -> onNext(mViewModel.getTool()),
                        e -> showErrorView(), this::showContentView);
        addSubscription(subscription);
    }

    @Override
    protected void refreshUI() {
        onNext(mViewModel.getTool());
    }

    @Override
    protected void share() {
        SpreadTool tool = mViewModel.getTool();
        AdvShareInfo info = tool.getShareInfo();

        ShareWebProvider provider = new ShareWebProvider();
        String imagePath;
        String url = LoginManager.getInstance().getLoginUser().getHeadimgurl();
        if (!TextUtils.isEmpty(url) && url.startsWith("http://")) {
            imagePath = BitmapUtil.getImagePath(url);
        } else {
            imagePath = url;
        }
        provider.setTitle(info.getShareTitle());
        provider.setDesc(info.getShareContent());
        provider.setImagePath(imagePath);
        provider.setUrl(info.getShareUrl());
        WXEntryActivity.launch(this, provider, R.string.share_str, 0);
    }

    @Override
    protected int getToolType() {
        return 2;
    }

    protected void initToolBar() {
        mToolbarLayout.setTitle(R.string.item_spread_tools);
    }

    @Override
    public WebParamBuilder getParams() {
        return null;
    }

    private void registerEvent() {
        //1.预览点击底部按钮的事件
        RxBusManager.register(this, EventConstant.KEY_SPREAD_TOOL_PREVIEW_BOTTOM_BUTTON_CLICK, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> mActivationBtn.performClick(), Logger::e);
    }

    private void onNext(SpreadTool tool) {
        RxBusManager.post(EventConstant.KEY_SPREAD_TOOL_PAY_SUCCESS, "");
        String payType = tool.getPayType();

        if (!TextUtils.isEmpty(payType)) {
            mActivationBtn.setText(R.string.share);
        } else {
            mActivationBtn.setText(R.string.immediately_use);
        }

        //显示前置照片
        ZjbImageLoader.create(tool.getImage())
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                .into(mContentIv);

        //显示背景照片
        ZjbImageLoader.create(tool.getBgImage())
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                .into(mContentLayout);

        //显示描述
        mDescTv.setText(tool.getDescription());

        //获取剩余时间
        int countDownDays = 0;
        try {
            countDownDays = TimeUtil.getTimestamp(tool.getEndTime(), mViewModel.getServerTime()) + 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (countDownDays > 0 && countDownDays <= 30) {
            mCountDownTv.setText(getString(R.string.count_down_days, countDownDays));
        } else {
            Spannable spannable = new SpannableString("还剩?天");
            StrikethroughSpan stSpan = new StrikethroughSpan();  //设置删除线样式
            spannable.setSpan(stSpan, 0, 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            mCountDownTv.setText(spannable);
        }

        //设置底部按钮点击事件
        mActivationBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(payType)) {
                showByDialog(tool);
            } else {
                share();
            }
        });
    }

    @OnClick({R.id.content_iv, R.id.btn_preview})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_preview:
            case R.id.content_iv:
                SpreadTool tool = mViewModel.getTool();
                ViewUtils.setDelayedClickable(v, 500);
//                SpreadToolPreviewActivity.launch(this, WebParamBuilder
//                        .create()
//                        .setTitle(getString(R.string.preview_spread_tool_title, tool.getTitle()))
//                        .setUrl(tool.getUrl()), !TextUtils.isEmpty(tool.getPayType()));
                break;
        }
    }
}
