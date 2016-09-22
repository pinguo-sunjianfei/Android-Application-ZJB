package com.idrv.coach.data.model;

import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/8/19
 * description:
 *
 * @author sunjianfei
 */
public class CoachAuthModel extends UpLoadModel {
    //教练证
    public static final int TYPE_COACH = 0x000;
    //身份证
    public static final int TYPE_ID = 0x001;

    int currentType = TYPE_COACH;

    String mCoachCardUrl;
    String mIDCardUrl;


    public void setCurrentType(int type) {
        currentType = type;
    }

    public int getCurrentType() {
        return currentType;
    }

    public void setImageUrl() {
        if (currentType == TYPE_COACH) {
            mCoachCardUrl = mImgUrl;
        } else {
            mIDCardUrl = mImgUrl;
        }
    }

    public String getCoachCardUrl() {
        return mCoachCardUrl;
    }

    public String getIDCardUrl() {
        return mIDCardUrl;
    }

    /**
     * 提交教练认证申请
     *
     * @return
     */
    public Observable<String> authentication() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_COACH_AUTH)
                .put("driverLicense", mCoachCardUrl)
                .put("idCard", mIDCardUrl)
                .putHeaders("Authorization", LoginManager.getInstance().getLoginUser().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

}
