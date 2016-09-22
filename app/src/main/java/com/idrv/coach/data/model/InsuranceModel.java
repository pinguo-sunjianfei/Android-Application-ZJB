package com.idrv.coach.data.model;

import com.idrv.coach.bean.InsuranceInfo;
import com.idrv.coach.bean.parser.InsuranceParser;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/3/23
 * description:
 *
 * @author sunjianfei
 */
public class InsuranceModel {
    int currentPage = 0;


    /**
     * 下拉刷新
     *
     * @param clearAdapter
     * @return
     */
    public Observable<List<InsuranceInfo>> refresh(Action0 clearAdapter) {
        currentPage = 0;
        return request()
                .doOnNext(__ -> clearAdapter.call());
    }

    public Observable<List<InsuranceInfo>> loadMore() {
        currentPage++;
        return request()
                .filter(ValidateUtil::isValidate);
    }

    private Observable<List<InsuranceInfo>> request() {
        //1.创建Request
        HttpGsonRequest<List<InsuranceInfo>> mRefreshRequest = RequestBuilder.<List<InsuranceInfo>>create()
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_INSURANCE_HIS)
                .parser(new InsuranceParser())
                .put("coachId", LoginManager.getInstance().getUid())
                .put("page", currentPage)
                .put("count", 30)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
