package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.idrv.coach.R;
import com.idrv.coach.bean.User;
import com.idrv.coach.bean.WChatUserInfo;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.bean.event.WeiXinEvent;
import com.idrv.coach.bean.share.WChatLoginInfo;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.manager.WChatManager;
import com.idrv.coach.data.model.LoginModel;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.helper.UIHelper;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.zjb.volley.core.exception.NetworkError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;

/**
 * Created by sunjianfei on 2016/3/7.
 */
public class LoginActivity extends BaseActivity<LoginModel> implements View.OnClickListener {
    @InjectView(R.id.btn_wx_login)
    Button mWxLoginBtn;

    public static void launch(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ButterKnife.inject(this);
        //1.初始化model
        mViewModel = new LoginModel();
        //2.注册RxBus(请求Oauth_code时候的回调)
        RxBusManager.register(this, EventConstant.KEY_WX_OAUTH_CODE, WeiXinEvent.LoginEvent.class)
                .subscribe(resp -> {
                    SendAuth.Resp response = resp.getData();
                    if (SendAuth.Resp.ErrCode.ERR_OK == response.errCode) {
                        Observable<WChatLoginInfo> observable = WChatManager.getInstance().login(response.code);
                        Subscription subscription = observable.subscribe(this::onWChatAuthorizeNext, this::onWChatError);
                        addSubscription(subscription);
                    } else {
                        setLoginBtnStatus(false);
                    }
                }, Logger::e);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!WChatManager.getInstance().isWxCallBack()) {
            setLoginBtnStatus(false);
        }
    }

    @Override
    public boolean isSwipeBackEnabled() {
        return false;
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @OnClick({R.id.btn_wx_login})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wx_login:
                loginToWeiXin();
                break;
        }
    }

    /**
     * 登录到微信
     */
    protected void loginToWeiXin() {
        //1.显示登陆状态
        setLoginBtnStatus(true);
        //2.获取到Oauth_code
        mViewModel.wChatLogin("zjb_login", () -> {
            UIHelper.shortToast(R.string.weixin_not_install);
            setLoginBtnStatus(false);
        });
    }

    /**
     * 设置登录button的状态
     * isLogin:登录状态 true:登录中 false:登录失败或未登录
     */
    private void setLoginBtnStatus(boolean isLogin) {
        if (isLogin) {
            mWxLoginBtn.setText(R.string.login_now);
            mWxLoginBtn.setEnabled(false);
        } else {
            mWxLoginBtn.setText(R.string.login);
            mWxLoginBtn.setEnabled(true);
        }
    }

    /**
     * 微信授权完成后的下一步动作.
     */
    private void onWChatAuthorizeNext(WChatLoginInfo info) {
        Subscription subscription = mViewModel.getWChatUserInfo(info)
                .subscribe(this::onWChatNext, this::onWChatError);
        addSubscription(subscription);
    }

    /**
     * 获取微信用户信息的下一步动作
     *
     * @param info
     */
    private void onWChatNext(WChatUserInfo info) {
        Subscription subscription = mViewModel.logIn(info)
                .subscribe(this::onLoginNext, this::onWChatError);
        addSubscription(subscription);
    }

    /**
     * 登录完成之后的下一步动作
     */
    private void onLoginNext(User user) {
        if (!TextUtils.isEmpty(user.getPhone())) {
            MainActivity.launch(this);
        } else {
            BindPhoneActivity.launch(this);
        }
        this.finish();
    }

    /**
     * 微信授权失败
     *
     * @param e
     */
    private void onWChatError(Throwable e) {
        //1.状态重置
        setLoginBtnStatus(false);
        //2.错误提示
        if (null != e) {
            if (e instanceof NetworkError) {
                NetworkError error = (NetworkError) e;
                UIHelper.shortToast(error.getErrorCode().getMessage());
            } else {
                e.printStackTrace();
            }
        }
    }
}
