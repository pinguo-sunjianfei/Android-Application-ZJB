package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.WithDrawBean;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.WithDrawModel;
import com.idrv.coach.ui.view.InputItemView;
import com.idrv.coach.utils.SystemUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.bean.ErrorCode;
import com.zjb.volley.core.exception.NetworkError;

import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/3/11
 * description: 提现-填写身份信息
 *
 * @author sunjianfei
 */
public class WithDrawSecondActivity extends BaseActivity<WithDrawModel> implements View.OnClickListener {
    private static final String KEY_PARAM = "param";
    @InjectView(R.id.item_name)
    InputItemView mNameItemView;
    @InjectView(R.id.item_card)
    InputItemView mCardIdItemView;
    @InjectView(R.id.item_tel)
    InputItemView mTelItemView;
    @InjectView(R.id.bank_name_tv)
    TextView mBankNameTv;
    @InjectView(R.id.bank_id_tv)
    TextView mBankIdTv;
    @InjectView(R.id.money_sum_tv)
    TextView mMoneySumTv;

    public static void launch(Context context, WithDrawBean bean) {
        Intent intent = new Intent(context, WithDrawSecondActivity.class);
        intent.putExtra(KEY_PARAM, bean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_withdraw_next_step);
        ButterKnife.inject(this);
        initToolBar();
        initViewModel();
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UIHelper.hideSoftInput(mNameItemView);
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.details);
    }

    private void initViewModel() {
        mViewModel = new WithDrawModel(getIntent().getParcelableExtra(KEY_PARAM));
    }

    private void initView() {
        WithDrawBean bean = mViewModel.getData();
        mNameItemView.setText(R.string.name);
        mNameItemView.setTextHint(R.string.pls_input_name);
        mNameItemView.setInputType(InputType.TYPE_CLASS_TEXT);

        mCardIdItemView.setText(R.string.id_card);
        mCardIdItemView.setTextHint(R.string.pls_input_card_id);
        mCardIdItemView.setInputType(InputType.TYPE_CLASS_TEXT);

        mTelItemView.setText(R.string.tel_num);
        mTelItemView.setTextHint(R.string.pls_input_tel_num);
        mTelItemView.setInputType(InputType.TYPE_CLASS_PHONE);

        mBankNameTv.setText(bean.getBankName());
        mBankIdTv.setText(bean.getBankId());
        mMoneySumTv.setText(bean.getMoneySum());
    }

    @OnClick(R.id.btn_commit)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                commit();
                break;
        }
    }

    private void commit() {
        if (isInputValid()) {
            showDialog();
            Subscription subscription = mViewModel.commitInfo()
                    .subscribe(this::onNext, this::onError);
            addSubscription(subscription);
        }
    }

    final protected void showDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .progressText(getResources().getString(R.string.commit_now))
                .onDismissListener(__ -> {
                    if (mCompositeSubscription != null) {
                        mCompositeSubscription.unsubscribe();
                    }
                }).show();
    }

    private boolean isInputValid() {
        String name = mNameItemView.getInputText();
        String id = mCardIdItemView.getInputText();
        String telNum = mTelItemView.getInputText();

        if (TextUtils.isEmpty(name)) {
            UIHelper.shortToast(R.string.pls_input_name);
            return false;
        } else if (TextUtils.isEmpty(id) || id.length() != 18) {
            UIHelper.shortToast(R.string.pls_input_valid_card_id);
            return false;
        } else if (!SystemUtil.checkPhoneNumber(telNum)) {
            UIHelper.shortToast(R.string.pls_input_valid_tel_num);
            return false;
        }
        WithDrawBean bean = mViewModel.getData();
        bean.setName(name);
        bean.setCardId(id);
        bean.setTelNum(telNum);
        return true;
    }

    private void onNext(String s) {
        dismissProgressDialog();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);//取得年份
        int month = calendar.get(Calendar.MONTH) + 1;//取得月份
        int day = calendar.get(Calendar.DAY_OF_MONTH);//取得日期

        //如果时间是在6号到15号之间,则取15
        if (day > 5 && day <= 15) {
            day = 15;
        } else if (day > 15 && day <= 25) {
            //如果时间是在16号到25号之间,取25
            day = 25;
        } else if (day > 25) {
            //如果时间是在25号之后,则是下个月5号
            if (month == 12) {
                //如果是在12月份,则明年
                month = 1;
                year += 1;
            } else {
                month += 1;
            }
            day = 5;
        } else {
            day = 5;
        }
        String time = year + "-" + month + "-" + day;
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .title(getString(R.string.tip))
                .content(getString(R.string.commit_success, time))
                .bottomButton(getString(R.string.confirm), 0xff34a5df)
                .bottomBtnClickListener((dialog, view) -> {
                    dialog.dismiss();
                    WithDrawSecondActivity.this.finish();
                })
                .show();
        RxBusManager.post(EventConstant.KEY_WITHDRAW_COMPLETE, "");
    }

    private void onError(Throwable e) {
        dismissProgressDialog();
        if (e instanceof NetworkError) {
            NetworkError error = (NetworkError) e;
            ErrorCode errorCode = error.getErrorCode();
            String errorMsg = errorCode.getMessage();
            if (TextUtils.isEmpty(errorMsg)) {
                errorMsg = getString(R.string.status_default);
            }
            DialogHelper.create(DialogHelper.TYPE_NORMAL)
                    .title(getString(R.string.status_error_tip))
                    .content(errorMsg)
                    .bottomButton(getString(R.string.close), 0xff34a5df)
                    .bottomBtnClickListener((dialog, view) -> {
                        dialog.dismiss();
                    })
                    .show();
        }
    }
}
