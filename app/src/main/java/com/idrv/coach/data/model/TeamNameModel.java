package com.idrv.coach.data.model;

import com.idrv.coach.bean.TeamInfo;
import com.idrv.coach.data.constants.ApiConstant;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time: 2016/3/22
 * description:
 *  修改团队名
 *
 * @author bigflower
 */
public class TeamNameModel extends BaseModel {

    public String gName ;
    public int gTeamId ;

    public Observable<String> reviseTeamName(String name) {
        //1.创建Request
        HttpGsonRequest<TeamInfo> mRefreshRequest = RequestBuilder.create(TeamInfo.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_POST_REVISE_NAME)
                .put("name", name)
                .put("teamId", gTeamId)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .map(__ -> name)
                .observeOn(AndroidSchedulers.mainThread());
    }

}
