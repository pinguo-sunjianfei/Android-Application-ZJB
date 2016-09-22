package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.idrv.coach.R;
import com.idrv.coach.bean.User;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.model.InviteModel;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.core.exception.NetworkError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/3/8
 * description:
 *
 * @author sunjianfei
 */
public class InviteActivity extends BaseActivity<InviteModel> implements View.OnClickListener {
    @InjectView(R.id.invite_edit)
    EditText mInviteEditText;


    public static void launch(Context context) {
        Intent intent = new Intent(context, InviteActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_invite);
        ButterKnife.inject(this);
        //1.初始化ViewModel
        mViewModel = new InviteModel();
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @Override
    public boolean isSwipeBackEnabled() {
        return false;
    }


    @OnClick({R.id.invite_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invite_btn:
                onInviteBtnClick();
                break;
        }
    }

    private void onInviteBtnClick() {
        String code = mInviteEditText.getText().toString();
        if (TextUtils.isEmpty(code) || code.length() != 4) {
            UIHelper.shortToast(R.string.input_correct_invite_code);
            return;
        }
        UIHelper.hideSoftInput(mInviteEditText);
        showDialog();
        Subscription subscription = mViewModel.verifyInviteCode(code)
                .subscribe(this::onNext, this::onError);
        addSubscription(subscription);
    }

    final protected void showDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .progressText(getResources().getString(R.string.pls_wait))
                .onDismissListener(__ -> {
                    if (mCompositeSubscription != null) {
                        mCompositeSubscription.unsubscribe();
                    }
                }).show();
    }

    private void onNext(String s) {
        dismissProgressDialog();
        User user = LoginManager.getInstance().getLoginUser();
        if (TextUtils.isEmpty(user.getPhone())) {
            BindPhoneActivity.launch(this);
        } else {
            MainActivity.launch(this);
        }
        finish();
    }

    private void onError(Throwable e) {
        dismissProgressDialog();
        if (null != e) {
            if (e instanceof NetworkError) {
                NetworkError error = (NetworkError) e;
                UIHelper.shortToast(error.getErrorCode().getMessage());
            } else {
                UIHelper.shortToast(R.string.invalid_invite_code);
                e.printStackTrace();
            }
        }
    }
}
