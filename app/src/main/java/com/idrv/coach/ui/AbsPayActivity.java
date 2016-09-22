package com.idrv.coach.ui;

import android.os.Bundle;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.SpreadTool;
import com.idrv.coach.bean.User;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.SpreadModel;
import com.idrv.coach.ui.view.ExchangeSpreadToolDialog;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.MathUtils;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.zjb.volley.core.exception.NetworkError;
import com.zjb.volley.utils.NetworkUtil;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/7/12
 * description:积分商城下的海报或者传播方案抽象Activity
 *
 * @author sunjianfei
 */
public abstract class AbsPayActivity extends AbsWebActivity<SpreadModel> {
    protected static final String KEY_TOOL_PARAM = "tool_param";
    protected static final int TYPE_SPREAD_TOOL = 2;
    protected static final int TYPE_POSTER = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEvent();
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

    private void registerEvent() {
        //1.微信支付
        RxBusManager.register(this, EventConstant.KEY_PAY_RESULT, Integer.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processPayResult, Logger::e);
    }

    private void initViewModel() {
        mViewModel = new SpreadModel();
        SpreadTool tool = getIntent().getParcelableExtra(KEY_TOOL_PARAM);
        mViewModel.setTool(tool);
        mViewModel.setToolType(getToolType());
        refresh();
    }

    /**
     * 获取订单信息
     */
    private void getOrder() {
        showProgressDialog(R.string.get_order_info_now);
        Subscription subscription = mViewModel.getOrder()
                .subscribe(__ -> Logger.e("success"), this::onBuyError, () -> {
                    //获取订单成功,微信支付
                    dismissProgressDialog();
                    mViewModel.wxPay();
                });
        addSubscription(subscription);
    }

    /**
     * 积分或者佣金购买传播方案
     *
     * @param type
     */
    private void buySpreadTool(int type) {
        showProgressDialog(R.string.buy_now_pls_waite);
        Subscription subscription = mViewModel.buySpreadTool(type)
                .subscribe(s -> onBuyNext(type), this::onBuyError);
        addSubscription(subscription);
    }

    private void onBuyNext(int type) {
        dismissProgressDialog();
        if (type == 1) {
            UIHelper.shortToast(R.string.pay_success);
        } else if (type == 2) {
            UIHelper.shortToast(R.string.exchange_success);
        } else {
            UIHelper.shortToast(R.string.has_free);
        }
        String typeStr;
        if (type == 1) {
            typeStr = "bPay";
        } else if (type == 2) {
            typeStr = "cPay";
        } else {
            typeStr = "memberFree";
        }
        mViewModel.updatePayType(typeStr);
        //发送事件,通知钱包刷新数据
        RxBusManager.post(EventConstant.KEY_WITHDRAW_COMPLETE, "");
        //发送事件，通知列表修改支付状态
        RxBusManager.post(EventConstant.KEY_SPREAD_TOOL_PAY_SUCCESS, "");
        refreshUI();
    }

    private void onBuyError(Throwable e) {
        if (e instanceof NetworkError) {
            NetworkError error = (NetworkError) e;
            UIHelper.shortToast(error.getErrorCode().getMessage());
        }
        dismissProgressDialog();
    }

    /**
     * 显示购买的对话框
     *
     * @param tool
     */
    protected void showByDialog(SpreadTool tool) {
        ExchangeSpreadToolDialog mExchangeDialog = new ExchangeSpreadToolDialog(ZjbApplication.getCurrentActivity());
        int titleRes = getToolType() == TYPE_SPREAD_TOOL ? R.string.vw_exchange_title : R.string.confirm_to_use_poster;
        mExchangeDialog.setSubtitle(tool.getTitle());
        mExchangeDialog.setTitle(titleRes);

        User user = LoginManager.getInstance().getLoginUser();
        boolean hasEnoughMoney = mViewModel.isEnoughMoney();
        boolean isMemberFree = tool.isMemberFree();
        boolean isMember = user.getMember() == 1;

        String text1 = getString(R.string.pay_plan_1, tool.getCredit());
        String text2;
        int itemShowType;

        String price = MathUtils.decimalFormat(tool.getPrice() * 1.0f / 100);
        if (hasEnoughMoney) {
            text2 = getString(R.string.pay_plan_2, price);
        } else {
            text2 = getString(R.string.pay_plan_3, price);
        }

        if (isMemberFree) {
            //如果该工具是会员免费
            if (!isMember) {
                //非会员
                itemShowType = ExchangeSpreadToolDialog.ITEM_SHOW_TYPE_NOT_MEMBER;
            } else {
                itemShowType = ExchangeSpreadToolDialog.ITEM_SHOW_TYPE_MEMBER;
            }
        } else {
            itemShowType = ExchangeSpreadToolDialog.ITEM_SHOW_TYPE_NORMAL;
        }
        mExchangeDialog.setItemShowType(itemShowType, text1, text2, !hasEnoughMoney);

        //积分兑换点击事件
        mExchangeDialog.setIntegralItemClickListener(v -> {
            //积分兑换
            if (mViewModel.isEnoughCredit()) {
                buySpreadTool(2);
            } else {
                //积分不足,弹窗提示
                DialogHelper.create(DialogHelper.TYPE_NORMAL)
                        .cancelable(true)
                        .canceledOnTouchOutside(true)
                        .title(getString(R.string.tip))
                        .content(getString(R.string.not_enough_credit))
                        .bottomButton(getString(R.string.ok), getResources().getColor(R.color.themes_main))
                        .bottomBtnClickListener((dialog, view1) -> dialog.dismiss())
                        .show();
            }
            mExchangeDialog.dismiss();
        });

        //微信支付或者佣金购买点击事件
        mExchangeDialog.setOtherItemClickListener(v -> {
            //佣金支付或者微信支付
            if (hasEnoughMoney) {
                //如果余额大于价格,走余额支付
                buySpreadTool(1);
            } else {
                //微信支付
                getOrder();
            }
            mExchangeDialog.dismiss();
        });

        //会员免费点击事件
        mExchangeDialog.setMemberItemClickListener(v -> {
            if (isMember) {
                //如果是会员,走会员流程
                buySpreadTool(3);
            } else {
                //非会员,弹窗提示
                DialogHelper.create(DialogHelper.TYPE_NORMAL)
                        .cancelable(true)
                        .canceledOnTouchOutside(true)
                        .title(getString(R.string.tips))
                        .content(getString(R.string.how_to_be_member))
                        .bottomButton(getString(R.string.ok), getResources().getColor(R.color.themes_main))
                        .bottomBtnClickListener((dialog, view1) -> dialog.dismiss())
                        .show();
            }
            mExchangeDialog.dismiss();
        });
        mExchangeDialog.show();
    }

    private void processPayResult(int code) {
        switch (code) {
            case BaseResp.ErrCode.ERR_OK:
                UIHelper.shortToast(R.string.pay_success);
                mViewModel.updatePayType("wxPay");
                refreshUI();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                UIHelper.shortToast(R.string.pay_cancel);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                UIHelper.shortToast(R.string.pay_failed);
                break;
        }
    }

    /**
     * 购买完成后,刷新UI
     */
    protected abstract void refreshUI();

    /**
     * 刷新网络数据
     */
    protected abstract void refresh();

    /**
     * 分享动作
     */
    protected void share() {

    }

    protected abstract int getToolType();
}
