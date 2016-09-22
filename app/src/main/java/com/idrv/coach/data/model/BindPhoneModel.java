package com.idrv.coach.data.model;

import com.idrv.coach.bean.User;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.ui.BindPhoneActivity;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.utils.GsonUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time: 2016/3/28
 * description:
 * 绑定手机号
 *
 * @author bigflower
 */
public class BindPhoneModel extends BaseModel {
    public static final int TIME_VERIFY_CODE = 60_000;
    public String gPhone;
    public String inviteCode;
    public BindPhoneActivity.Timer gTimer;

    /**
     * 获取验证码
     *
     * @return
     */
    public Observable<String> getPhoneVerifyCode() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.GET)
                .url(ApiConstant.API_GET_BIND_CODE)
                .put("phone", gPhone)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 绑定手机号
     *
     * @param code
     * @return
     */
    public Observable<String> bindPhone(String code) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.PUT)
                .url(ApiConstant.API_PUT_BIND_PHONE)
                .put("phone", gPhone)
                .put("code", code)
                .put("inviteCode", inviteCode)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(__ -> savePhone())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 保存个人基本信息: 手机号
     */
    public void savePhone() {
        try {
            User user = GsonUtil.fromJson(PreferenceUtil.getString(SPConstant.KEY_USER), User.class);
            user.setPhone(gPhone);
            // save
            PreferenceUtil.putString(SPConstant.KEY_USER, GsonUtil.toJson(user));
            LoginManager.getInstance().setLoginUser(user);
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }
}
