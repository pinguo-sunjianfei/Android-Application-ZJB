package com.idrv.coach.data.model;

import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.DynamicPage;
import com.idrv.coach.bean.Trend;
import com.idrv.coach.data.cache.ACache;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.utils.GsonUtil;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/3/28
 * description:
 *
 * @author sunjianfei
 */
public class DynamicModel {
    static String KEY_DYNAMIC = "dynamic";
    private ACache mACache;
    String time;

    public DynamicModel() {
        mACache = ACache.get(ZjbApplication.gContext);
    }

    /**
     * 下拉刷新
     *
     * @param clearAdapter
     * @return
     */
    public Observable<DynamicPage> refresh(Action0 clearAdapter) {
        time = "";
        return request()
                .doOnNext(page -> mACache.put(KEY_DYNAMIC, GsonUtil.toJson(page)))
                .doOnNext(__ -> clearAdapter.call());
    }

    /**
     * 加载更多
     *
     * @return
     */
    public Observable<DynamicPage> loadMore() {
        return request();
    }

    /**
     * 获取动态的数据
     *
     * @return Observable<DynamicPage>
     */
    private Observable<DynamicPage> request() {
        //1.创建Request
        HttpGsonRequest<DynamicPage> mRefreshRequest = RequestBuilder.create(DynamicPage.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_DYNAMIC)
                .put("time", time)
                .put("count", 20)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp && null != resp.data)
                .map(resp -> {
                    DynamicPage page = resp.data;
                    List<Trend> list = page.getTrends();
                    if (ValidateUtil.isValidate(list)) {
                        time = list.get(list.size() - 1).getTime();
                    }
                    return page;
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 加入团队
     *
     * @return Observable<String>
     */
    public Observable<String> joinTeam(String teamId) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_JOIN_TEAM)
                .put("coachId", LoginManager.getInstance().getUid())
                .put("teamId", teamId)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> like(String targetId, String coachId, int type) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_LIKE)
                .put("coachId", coachId)
                .put("targetId", targetId)
                .put("type", type)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public boolean isShowInviteDialog() {
        return PreferenceUtil.getBoolean(SPConstant.KEY_INVITE);
    }

}
