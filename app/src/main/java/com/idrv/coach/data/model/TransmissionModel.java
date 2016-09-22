package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.idrv.coach.bean.Coach;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.PreferenceUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/6/8
 * description:传播方案设置
 *
 * @author sunjianfei
 */
public class TransmissionModel extends CreateWebSiteModel {

    /**
     * 获取传播方案设置
     *
     * @return
     */
    public Observable<String> getTransmissionModel() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_GET_TRANSMISSION_MODE)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改传播方案设置
     *
     * @return
     */
    public Observable<String> setTransmissionModel(int mode) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_SET_TRANSMISSION_MODE)
                .put("mode", mode)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(s -> PreferenceUtil.putInt(SPConstant.KEY_PRO_TYPE, mode))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public int getNativePicNum() {
        photoNum = PreferenceUtil.getInt(SPConstant.KEY_PIC_NUMBER, -1);
        return photoNum;
    }

    public boolean isOpenWebSite() {
        String result = PreferenceUtil.getString(SPConstant.KEY_IS_OPEN_WEBSITE + LoginManager.getInstance().getUid());
        if (!TextUtils.isEmpty(result)) {
            if (result.equals("true")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean isRecord() {
        Coach coach = LoginManager.getInstance().getCoach();
        return !TextUtils.isEmpty(coach.getTeachingDeclaration());
    }

}
