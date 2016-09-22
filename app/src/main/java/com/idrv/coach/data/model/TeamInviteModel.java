package com.idrv.coach.data.model;

import com.idrv.coach.bean.TeamMember;
import com.idrv.coach.data.constants.ApiConstant;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time: 2016/3/24
 * description:
 * 邀请团队成员
 *
 * @author bigflower
 */
public class TeamInviteModel extends BaseModel {

    public int teamId;
    public String gButtonName;
    public int gTotalNumer;

    /**
     * 邀请成员
     *
     * @return
     */
    public Observable<String> inviteMumber(String mumbers) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_POST_TEAM_INVITE)
                .put("teamId", teamId)
                .put("teams", mumbers)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public List<TeamMember> listDeduplication(List<TeamMember> list) {
        List<TeamMember> results = new ArrayList<>();
        for (TeamMember member : list) {
            if (Collections.frequency(results, member) < 1) results.add(member);
        }
        return results;
    }
}
