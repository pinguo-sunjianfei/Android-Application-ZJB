package com.idrv.coach.data.model;

import com.idrv.coach.bean.Coach;
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
 * time: 2016/3/14
 * description:
 * 教练证和微信二维码共用，
 * 原理上都是上传图片，然后修改教练信息
 * @author bigflower
 */
public class QrCodeCoachCardModel extends UpLoadModel {

    public Coach gCoach;

    public QrCodeCoachCardModel() {
        gCoach = LoginManager.getInstance().getCoach();
    }
    /**
     * 修改教练信息，
     *
     * @return
     */
    public Observable<Coach> putCoachInfo(String key) {
        //1.创建Request
        HttpGsonRequest<Coach> mRefreshRequest = RequestBuilder.create(Coach.class)
                .requestMethod(Request.Method.PUT)
                .url(ApiConstant.API_PUT_COACH_INFO)
                .put(key, mImgUrl)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }



}
