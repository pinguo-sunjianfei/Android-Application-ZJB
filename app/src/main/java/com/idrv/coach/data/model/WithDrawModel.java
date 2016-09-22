package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.idrv.coach.bean.Commission;
import com.idrv.coach.bean.WithDrawBean;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.manager.AppInitManager;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/3/11
 * description:
 *
 * @author sunjianfei
 */
public class WithDrawModel extends BaseModel {
    private WithDrawBean mData;
    private Commission mCommission;

    public WithDrawModel() {

    }

    public WithDrawModel(WithDrawBean data) {
        this.mData = data;
    }

    public WithDrawBean getData() {
        return mData;
    }

    public void setData(WithDrawBean mData) {
        this.mData = mData;
    }

    public float getBlance() {
        String blanceStr = mCommission.getBalance();
        if (!TextUtils.isEmpty(blanceStr)) {
            return Float.valueOf(blanceStr);
        }
        return 0f;
    }

    /**
     * 提交提现信息
     *
     * @return
     */
    public Observable<String> commitInfo() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_CASH)
                .put("phone", mData.getTelNum())
                .put("bankName", mData.getBankName())
                .put("cardNumber", mData.getBankId())
                .put("name", mData.getName())
                .put("idNumber", mData.getCardId())
                .put("sum", mData.getMoneySum())
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Commission> getCommission() {
        //1.创建Request
        HttpGsonRequest<Commission> mRefreshRequest = RequestBuilder.create(Commission.class)
                .requestMethod(Request.Method.GET)
                .url(ApiConstant.API_COMMISSION)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(commission -> mCommission = commission)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
