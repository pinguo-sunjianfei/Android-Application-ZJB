package com.idrv.coach.data.model;

import com.idrv.coach.bean.TeamInfo;
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
 * time: 2016/3/22
 * description:
 *  我的团队
 *
 * @author bigflower
 */
public class TeamMineModel extends BaseModel {

    public static final int TYPE_ERROR = -1; // 意外情况！
    public static final int TYPE_NOSIGN = 0; // 非签约教练，有团队
    public static final int TYPE_NOSIGN_NOTEAM = 1; // 非签约教练，没有团队
    public static final int TYPE_SIGN = 2;// 签约教练，有团队
    public static final int TYPE_SIGN_NOTEAM = 3;// 签约教练，没有团队
    public static final int TYPE_SIGN_NOINVITE = 4;// 签约教练，没有团队，也没人可邀请

    // 当前教练的类型
    public int gType;
    public TeamInfo gTeamInfo;
    public boolean gIsSigned = true;

    public TeamMineModel() {
    }

    /**
     * 获取教练，签约与否
     * @return
     */
    public Observable<Boolean> getSignState() {
        //1.创建Request
        HttpGsonRequest<Boolean> mRefreshRequest = RequestBuilder.create(Boolean.class)
                .requestMethod(Request.Method.GET)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .url(ApiConstant.API_GET_IS_SIGNED)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(this::isSigned)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void isSigned(Boolean isSigned){
        gIsSigned = isSigned;
    }

    /**
     * 获取我的团队信息
     *
     * @return
     */
    public Observable<TeamInfo> getMyTeam() {
        //1.创建Request
        HttpGsonRequest<TeamInfo> mRefreshRequest = RequestBuilder.create(TeamInfo.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_POST_MY_TEAM)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(this::detectState)
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 判断教练的类型
     */
    public int detectState(TeamInfo teamInfo) {
        gTeamInfo = teamInfo;
        if (gTeamInfo == null)
            return TYPE_ERROR;
        if (gIsSigned) {
            if (teamInfo.getTeam() != null) {
                gType = TYPE_SIGN;
            } else if (teamInfo.getInviteUsers() != null) {
                gType = TYPE_SIGN_NOTEAM;
            } else {
                gType = TYPE_SIGN_NOINVITE;
            }
        } else {
            if (teamInfo.getTeam() == null) {
                gType = TYPE_NOSIGN_NOTEAM;
            } else {
                gType = TYPE_NOSIGN;
            }
        }
        return gType ;
    }

}
