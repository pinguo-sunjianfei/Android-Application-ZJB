package com.idrv.coach.data.model;

import com.idrv.coach.bean.Rank;
import com.idrv.coach.bean.parser.CoachRankParser;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/6/23
 * description:
 *
 * @author sunjianfei
 */
public class CoachRankModel {


    /**
     * 获取教练排名信息
     *
     * @return
     */
    public Observable<List<Rank>> getCoachRank() {
        //1.创建Request
        HttpGsonRequest<List<Rank>> mRefreshRequest = RequestBuilder.<List<Rank>>create()
                .requestMethod(Request.Method.POST)
                .parser(new CoachRankParser())
                .url(ApiConstant.API_COACH_RANK)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .filter(ValidateUtil::isValidate)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
