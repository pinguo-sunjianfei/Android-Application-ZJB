package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.model.BindPhoneModel;
import com.idrv.coach.ui.view.InputItemView;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.SystemUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.bean.ErrorCode;
import com.zjb.volley.core.exception.NetworkError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time: 2016/3/28
 * description:
 * 绑定手机号
 *
 * @author bigflower
 */
public class BindPhoneActivity extends BaseActivity<BindPhoneModel> {

    private static final String KEY_IS_AFTER_LOGIN = "isAfterLogin";

    @InjectView(R.id.bindPhone_phoneEt)
    EditText mPhoneEt;
    @InjectView(R.id.bindPhone_verifycodeEt)
    EditText mVerifyEt;
    @InjectView(R.id.bindPhone_verifyTv)
    TextView mVerifyTv;
    @InjectView(R.id.invite_code_layout)
    View mInviteCodeLayout;
    @InjectView(R.id.item_invite_code)
    InputItemView mInviteCodeItemView;

    /**
     * 加载该页面
     *
     * @param activity
     */
    public static void launch(Context activity) {
        Intent intent = new Intent(activity, BindPhoneActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_bind_phone);
        ButterKnife.inject(this);

        // 1. 初始化ViewModel
        mViewModel = new BindPhoneModel();
        // 2. 初始化title
        initToolBar();
        // 3. 加载布局，并根据状态赋值
        initViews();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.title_phone);
        mToolbarLayout.setLeftIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    public void onToolbarLeftClick(View view) {
        //do nothing
    }

    private void initViews() {
        // 倒计时，初始化
        mViewModel.gTimer = new BindPhoneActivity.Timer(BindPhoneModel.TIME_VERIFY_CODE, 1000);
        // 如果上个版本填写了contact，则这里默认加进去
        mInviteCodeItemView.setText(R.string.invite_code);
        mInviteCodeItemView.setTextHint(R.string.input_invite_code);
        mInviteCodeItemView.setInputType(InputType.TYPE_CLASS_TEXT);
        if (LoginManager.getInstance().isInvited()) {
            mInviteCodeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 获取验证码
     * 请求成功，则开始倒计时
     *
     * @param v
     */
    public void getVerifyCode(View v) {
        if (isPhoneNotValid()) {
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_verify_code)).show();
        Subscription subscription = mViewModel.getPhoneVerifyCode()
                .subscribe(__ -> downTimeStart(), this::onError);
        addSubscription(subscription);
    }

    private void downTimeStart() {
        dismissProgressDialog();
        mViewModel.gTimer.start();
        UIHelper.shortToast(R.string.verify_code_send_success);
        mVerifyTv.setClickable(false);
        mVerifyTv.setBackgroundResource(R.drawable.btn_gray_unclickable);
        mVerifyTv.setText(BindPhoneModel.TIME_VERIFY_CODE / 1000 + "");
    }

    @OnClick(R.id.bindPhone_surebt)
    public void onBindClick() {
        UIHelper.hideSoftInput(mVerifyEt);
        if (isPhoneNotValid()) {
            return;
        }
        // 判断验证码
        String code = mVerifyEt.getText().toString().trim();
        if (code.length() != 4) {
            UIHelper.shortToast(R.string.pls_input_right_varifycode);
            return;
        }
        //判断邀请码
        mViewModel.inviteCode = mInviteCodeItemView.getInputText();
        //如果填写了邀请码
        if (!TextUtils.isEmpty(mViewModel.inviteCode) && mViewModel.inviteCode.length() != 4) {
            UIHelper.shortToast(R.string.input_correct_invite_code);
            return;
        }
        // 开始请求
        mProgressDialog = DialogHelper
                .create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_bingding))
                .show();
        Subscription subscription = mViewModel.bindPhone(code)
                .subscribe(__ -> onPostSuccess(), this::onError);
        addSubscription(subscription);
    }

    /**
     * 最终的，提交成功后，的处理
     */
    private void onPostSuccess() {
        dismissProgressDialog();
        MainActivity.launch(this);
        finish();
    }

    private void onError(Throwable e) {
        dismissProgressDialog();
        if (null != e) {
            if (e instanceof NetworkError) {
                NetworkError error = (NetworkError) e;
                if (error.getErrorCode().getCode() == ErrorCode.ERROR500.getCode()) {
                    UIHelper.shortToast(R.string.verify_code_has_send);
                } else {
                    UIHelper.shortToast(error.getErrorCode().getMessage());
                }
            } else {
                UIHelper.shortToast(R.string.http_error);
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断手机号是否有效
     *
     * @return
     */
    private boolean isPhoneNotValid() {
        mViewModel.gPhone = mPhoneEt.getText().toString().trim();
        if (!SystemUtil.checkPhoneNumber(mViewModel.gPhone)) {
            UIHelper.shortToast(R.string.pls_input_right_phone);
            return true;
        } else if (!mViewModel.gPhone.startsWith("1")) {
            UIHelper.shortToast(R.string.pls_input_right_phone);
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        UIHelper.hideSoftInput(mPhoneEt);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mViewModel.gTimer != null)
            mViewModel.gTimer.cancel();
        super.onDestroy();
    }

    /////////////////////////////////////////////////////////////
    // 倒计时
    /////////////////////////////////////////////////////////////

    public class Timer extends CountDownTimer {

        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mVerifyTv.setText(millisUntilFinished / 1000 + "");
            Logger.i(mVerifyTv.getText().toString());
        }

        @Override
        public void onFinish() {
            mVerifyTv.setClickable(true);
            mVerifyTv.setBackgroundResource(R.drawable.btn_withdraw);
            mVerifyTv.setText(R.string.verify_code_again);
        }
    }
}
