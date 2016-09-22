package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.DrivingTestInsurance;
import com.idrv.coach.bean.WxPayInfo;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.manager.WChatManager;
import com.idrv.coach.data.model.DrivingTestInsPayModel;
import com.idrv.coach.ui.view.MasterItemView;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.helper.UIHelper;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.zjb.volley.utils.NetworkUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/3/30
 * description:
 *
 * @author sunjianfei
 */
public class DrivingTestInsPayActivity extends BaseActivity<DrivingTestInsPayModel> {
    private static final String KEY_PARAM = "param";

    @InjectView(R.id.apply_time)
    TextView mApplyTimeTv;
    @InjectView(R.id.name)
    TextView mNameTv;
    @InjectView(R.id.id_card_tv)
    TextView mIdCardTv;
    @InjectView(R.id.tel_tv)
    TextView mTelTv;
    @InjectView(R.id.item_wx_pay)
    MasterItemView mWxPayItemView;
    @InjectView(R.id.total_price_tv)
    TextView mPriceTv;
    @InjectView(R.id.ins_num_tv)
    TextView mInsNumTv;


    public static void launch(Context context, DrivingTestInsurance ins) {
        Intent intent = new Intent(context, DrivingTestInsPayActivity.class);
        intent.putExtra(KEY_PARAM, ins);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_driving_test_ins_reviews);
        ButterKnife.inject(this);
        initToolBar();
        initViewModel();
        initView();
        registerEvent();
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
        RxBusManager.register(this, EventConstant.KEY_PAY_RESULT, Integer.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processPayResult, Logger::e);
    }

    private void initToolBar() {
        mToolbarLayout.setTitleTxt(R.string.driving_test_ins);
    }

    private void initViewModel() {
        mViewModel = new DrivingTestInsPayModel();
        DrivingTestInsurance ins = getIntent().getParcelableExtra(KEY_PARAM);
        mViewModel.setData(ins);
        refresh();
    }

    private void initView() {
        DrivingTestInsurance ins = mViewModel.getData();
        mNameTv.setText(ins.getName());
        mTelTv.setText(ins.getPhone());
        mIdCardTv.setText(ins.getIdCard());

        String time = ins.getCreated();
        if (!TextUtils.isEmpty(time) && time.length() > 10) {
            time = time.substring(0, 10);
        }
        mApplyTimeTv.setText(getString(R.string.apply_time, time));

        mWxPayItemView.setText(R.string.wx_pay);
        mWxPayItemView.setTitleLeftDrawableRes(R.drawable.icon_wx_pay);

        String str = getString(R.string.ins_num);
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.themes_main)),
                str.length() - 3, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mInsNumTv.setText(spannableString);

        mWxPayItemView.setOnMasterItemClickListener(v -> {
            //1.判断是否安装了微信
            if (!WChatManager.getInstance().WXAPI.isWXAppInstalled()) {
                UIHelper.shortToast(R.string.weixin_not_install);
                return;
            }
            mViewModel.wxPay();
        });
    }

    private void refresh() {
        Subscription subscription = mViewModel.getOrder()
                .subscribe(this::onNext, e -> showErrorView());
        addSubscription(subscription);
    }

    private void onNext(WxPayInfo info) {
        String str = getString(R.string.total_price, info.getTotalFee() * 1.0f / 100);
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.themes_main)),
                3, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPriceTv.setText(spannableString);
        showContentView();
    }

    private void processPayResult(int code) {
        switch (code) {
            case BaseResp.ErrCode.ERR_OK:
                UIHelper.shortToast(R.string.weixin_pay_success);
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                UIHelper.shortToast(R.string.weixin_pay_cancel);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                UIHelper.shortToast(R.string.weixin_pay_fail);
                break;
        }
    }
}
