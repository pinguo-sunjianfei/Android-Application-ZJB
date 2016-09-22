package com.idrv.coach.data.model;

import com.idrv.coach.bean.DrivingTestInsurance;
import com.idrv.coach.bean.WxPay;
import com.idrv.coach.bean.WxPayInfo;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.manager.WChatManager;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/5/5
 * description:
 *
 * @author sunjianfei
 */
public class DrivingTestInsPayModel {
    DrivingTestInsurance mData;
    WxPayInfo mWxPayInfo;

    public void setData(DrivingTestInsurance mData) {
        this.mData = mData;
    }

    public DrivingTestInsurance getData() {
        return mData;
    }

    public Observable<WxPayInfo> getOrder() {
        //1.创建Request
        HttpGsonRequest<WxPay> mRefreshRequest = RequestBuilder.create(WxPay.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_GET_ORDER)
                .put("outTradeNo", mData.getOutTradeNo())
                .put("type", 1)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(resp -> {
                    WxPay pay = resp.data;
                    mWxPayInfo = pay.getM_values();
                    return mWxPayInfo;
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void wxPay() {
        PayReq req = new PayReq();
        req.appId = mWxPayInfo.getAppid();
        req.partnerId = mWxPayInfo.getPartnerid();
        req.prepayId = mWxPayInfo.getPrepayid();
        req.nonceStr = mWxPayInfo.getNoncestr();
        req.timeStamp = mWxPayInfo.getTimestamp();
        req.packageValue = "Sign=WXPay";
        req.sign = mWxPayInfo.getSign();
        WChatManager.getInstance().WXAPI.sendReq(req);
    }
}
