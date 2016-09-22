package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Commission;
import com.idrv.coach.bean.WithDrawBean;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.WithDrawModel;
import com.idrv.coach.ui.view.InputItemView;
import com.idrv.coach.ui.widget.ClearEditText;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.utils.NetworkUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/3/10
 * description:提现-填写银行账户信息
 *
 * @author sunjianfei
 */
public class WithDrawActivity extends BaseActivity<WithDrawModel> implements View.OnClickListener {
    @InjectView(R.id.item_bank_name)
    InputItemView mBankNameItemView;
    @InjectView(R.id.item_bank_num)
    InputItemView mBankNumItemView;
    @InjectView(R.id.money_sum_edit)
    ClearEditText mMoneySumEditTv;
    @InjectView(R.id.balance_tv)
    TextView mBalanceTv;


    WithDrawBean mWithDrawBean;

    public static void launch(Context context) {
        Intent intent = new Intent(context, WithDrawActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_withdraw);
        ButterKnife.inject(this);
        initToolBar();
        initView();
        initViewModel();
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
    protected void onPause() {
        super.onPause();
        UIHelper.hideSoftInput(mMoneySumEditTv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClickRetry() {
        if (NetworkUtil.isConnected(this)) {
            showProgressView();
            getData();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    private void registerEvent() {
        RxBusManager.register(this, EventConstant.KEY_WITHDRAW_COMPLETE, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> finish(), Logger::e);
    }

    private void initViewModel() {
        mViewModel = new WithDrawModel();
        getData();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.details);
        mToolbarLayout.setRightIcon(R.drawable.icon_details);
    }

    @Override
    public void onToolbarRightClick(View view) {
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .title(getString(R.string.helper))
                .content(getString(R.string.with_draw_help))
                .bottomButton(getString(R.string.close), getResources().getColor(R.color.themes_main))
                .bottomBtnClickListener((dialog, v) -> dialog.dismiss())
                .show();
    }

    private void initView() {
        mBankNameItemView.setText(R.string.bank);
        mBankNameItemView.setTextHint(R.string.pls_input_bank_name);
        mBankNameItemView.setInputType(InputType.TYPE_CLASS_TEXT);

        mBankNumItemView.setText(R.string.bank_num);
        mBankNumItemView.setTextHint(R.string.pls_input_bank_num);
        mBankNumItemView.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void getData() {
        Subscription subscription = mViewModel.getCommission()
                .subscribe(this::onNext, __ -> showErrorView());
        addSubscription(subscription);
    }

    @OnClick({R.id.next_step})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step:
                if (isInputValid()) {
                    WithDrawSecondActivity.launch(this, mWithDrawBean);
                }
                break;
        }
    }

    /**
     * 检查输入是否合法
     *
     * @return
     */
    private boolean isInputValid() {
        String moneySum = mMoneySumEditTv.getText().toString();
        String bankName = mBankNameItemView.getInputText();
        String bankId = mBankNumItemView.getInputText();

        if (TextUtils.isEmpty(moneySum) || moneySum.equals("0")) {
            UIHelper.shortToast(R.string.pls_input_valid_num);
            return false;
        }
        int sum = Integer.parseInt(moneySum);
        float blance = mViewModel.getBlance();
        if (sum == 0 || sum % 100 != 0) {
            UIHelper.shortToast(R.string.pls_input_valid_num);
            return false;
        } else if (sum > blance) {
            UIHelper.shortToast(R.string.pls_input_valid_num_while);
            return false;
        } else if (TextUtils.isEmpty(bankName)) {
            UIHelper.shortToast(R.string.pls_input_valid_bank_name);
            return false;
        } else if (TextUtils.isEmpty(bankId) || bankId.length() < 16) {
            UIHelper.shortToast(R.string.pls_input_valid_bank_id);
            return false;
        }
        mWithDrawBean = new WithDrawBean();
        mWithDrawBean.setMoneySum(moneySum);
        mWithDrawBean.setBankName(bankName);
        mWithDrawBean.setBankId(bankId);
        return true;
    }

    private void onNext(Commission commission) {
        mBalanceTv.setText(getString(R.string.balance, commission.getBalance()));
        showContentView();
    }
}
