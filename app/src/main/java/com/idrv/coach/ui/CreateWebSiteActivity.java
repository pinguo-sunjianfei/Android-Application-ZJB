package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.data.model.CreateWebSiteModel;
import com.idrv.coach.ui.view.CreateWebSiteDialog;
import com.idrv.coach.utils.StatisticsUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.utils.NetworkUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/6/2
 * description:
 *
 * @author sunjianfei
 */
public class CreateWebSiteActivity extends BaseActivity<CreateWebSiteModel> implements CreateWebSiteDialog.onAnimEndListener {
    CreateWebSiteDialog mDialog;
    boolean isAnimEnd = false;
    boolean isCreateSuccess = false;

    public static void launch(Context context) {
        Intent intent = new Intent(context, CreateWebSiteActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_web_site);
        ButterKnife.inject(this);
        initToolBar();
        initViewModel();
    }

    @Override
    protected boolean hasBaseLayout() {
        return true;
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    public void onClickRetry() {
        if (NetworkUtil.isConnected(this)) {
            showProgressView();
            refresh();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.my_website);
    }

    private void initViewModel() {
        mViewModel = new CreateWebSiteModel();
        refresh();
    }

    private void refresh() {
        Subscription subscription = mViewModel.getPicNum()
                .subscribe(__ -> {
                }, __ -> showErrorView(), this::getShareDays);
        addSubscription(subscription);
    }

    private void getShareDays() {
        Subscription subscription = mViewModel.getContinuousShareDays()
                .subscribe(__ -> {
                }, __ -> showErrorView(), this::showContentView);
        addSubscription(subscription);
    }

    private void createWebSite() {
        Subscription subscription = mViewModel.createWebSite()
                .subscribe(this::onNext, this::onError);
        addSubscription(subscription);
    }

    private void onNext(String s) {
        isCreateSuccess = true;
        if (isAnimEnd) {
            onAnimEnd();
        }
    }

    private void onError(Throwable e) {
        UIHelper.shortToast(R.string.create_website_fail);
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @OnClick(R.id.btn_create)
    public void onCreateClick(View v) {
        //点击创建网站统计
        StatisticsUtil.onEvent(R.string.click_generate);
        //必须连续3天分享才能生成个人网站
        if (mViewModel.getShareDays() < 3) {
            DialogHelper.create(DialogHelper.TYPE_NORMAL)
                    .cancelable(true)
                    .canceledOnTouchOutside(true)
                    .title(getString(R.string.tip))
                    .content(getString(R.string.share_three_days_in_a_row))
                    .bottomButton(getString(R.string.ok), ContextCompat.getColor(v.getContext(), R.color.themes_main))
                    .bottomBtnClickListener((dialog, view) -> dialog.dismiss())
                    .show();
            return;
        }
        //重置状态
        isAnimEnd = false;
        isCreateSuccess = false;

        createWebSite();
        if (null != mDialog) {
            mDialog = null;
        }
        mDialog = new CreateWebSiteDialog(this);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setAnimEndListener(this);
        mDialog.show();
        mDialog.startAnim();
    }

    @Override
    public void onAnimEnd() {
        isAnimEnd = true;
        if (isCreateSuccess) {
            if (null != mDialog && mDialog.isShowing()) {
                mDialog.dismiss();
                mDialog = null;
            }
            CreateWebSiteSuccessActivity.launch(this);
            finish();
        }
    }
}
