package com.idrv.coach.data.model;

import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.LoginManager;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/3/8
 * description:邀请码
 *
 * @author sunjianfei
 */
public class InviteModel extends BaseModel {

    /**
     * 发送验证码
     *
     * @param code
     * @return
     */
    public Observable<String> verifyInviteCode(String code) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.PUT)
                .url(ApiConstant.API_INVITE_CODE)
                .put("code", code)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .map(HttpResponse::getData)
                .doOnNext(s -> LoginManager.getInstance().updateInviteCode())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
