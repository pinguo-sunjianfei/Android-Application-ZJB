package com.idrv.coach.data.model;

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
 *  创建团队
 * @author bigflower
 */
public class TeamCreateModel extends BaseModel {

    public String gTeamName ;
    public String gButtonName ;
    public int gTotalNumber ;

    /**
     * 创建我的团队
     *
     * @param name  团队的名称
     * @param teams 邀请的团队成员的id,之间用英文逗号分隔开，例如sunjianfei,wuziqiang(可以为空！)
     * @return
     */
    public Observable<Integer> createTeam(String name, String teams) {
        //1.创建Request
        HttpGsonRequest<Integer> mRefreshRequest = RequestBuilder.create(Integer.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_POST_CREATE_TEAM)
                .put("name", name)
                .put("teams", teams)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }


}
