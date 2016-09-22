package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.AuthStatus;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.PicAndCommentNum;
import com.idrv.coach.bean.User;
import com.idrv.coach.data.cache.ACache;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.UrlParserManager;
import com.idrv.coach.utils.PreferenceUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.utils.GsonUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/3/18
 * description: 教练资料
 *
 * @author sunjianfei
 */
public class CoachInfoModel {
    ACache mACache;


    public CoachInfoModel() {
        mACache = ACache.get(ZjbApplication.gContext);
    }

    /**
     * 获取教练信息
     *
     * @return
     */
    public Observable<Coach> getCoachInfo() {
        //1.创建Request
        HttpGsonRequest<Coach> mRefreshRequest = RequestBuilder.create(Coach.class)
                .requestMethod(Request.Method.GET)
                .url(ApiConstant.API_GET_COACH_INFO)
                .putHeaders("Authorization", getUser().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(coach -> {
                    PreferenceUtil.putString(SPConstant.KEY_COACH, GsonUtil.toJson(coach));
                    LoginManager.getInstance().setCoach(coach);
                    //更新占位符基本参数
                    UrlParserManager.getInstance().updateBaseParams();
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取教练认证的状态
     *
     * @return
     */
    public Observable<AuthStatus> getAuthenticationState() {
        //1.创建Request
        HttpGsonRequest<AuthStatus> mRefreshRequest = RequestBuilder.create(AuthStatus.class)
                .requestMethod(Request.Method.GET)
                .url(ApiConstant.API_COACH_AUTH_STATUS)
                .putHeaders("Authorization", getUser().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(status -> {
                    //更新内存中教练信息
                    Coach coach = LoginManager.getInstance().getCoach();
                    coach.setAuthenticationState(status.getAuthenticationState());
                    //更新文件缓存
                    PreferenceUtil.putString(SPConstant.KEY_COACH, GsonUtil.toJson(coach));
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Coach getCacheData() {
        return LoginManager.getInstance().getCoach();
    }

    public User getUser() {
        return LoginManager.getInstance().getLoginUser();
    }

    /**
     * 获取评论数和照片数
     *
     * @return
     */
    public Observable<PicAndCommentNum> getPicNum() {
        //1.创建Request
        HttpGsonRequest<PicAndCommentNum> mRefreshRequest = RequestBuilder.create(PicAndCommentNum.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_PIC_AND_COMMENT_NUMBER)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(num -> {
                    //更新照片数量
                    PreferenceUtil.putInt(SPConstant.KEY_PIC_NUMBER, num.getPictureNum());
                    //更新评论数量
                    PreferenceUtil.putInt(SPConstant.KEY_COMMENT_NUM, num.getCommentNum());
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public boolean isProfileValid() {
        User user = LoginManager.getInstance().getLoginUser();
        Coach coach = getCacheData();

        return null != user && null != coach
                && !TextUtils.isEmpty(user.getHeadimgurl())
                && !TextUtils.isEmpty(user.getNickname())
                && !TextUtils.isEmpty(coach.getCoachingDate())
                && !TextUtils.isEmpty(coach.getTeachingDeclaration())
                && !TextUtils.isEmpty(coach.getQrCode())
                && !TextUtils.isEmpty(user.getPhone())
                && !TextUtils.isEmpty(coach.getDrivingSchool())
                && !TextUtils.isEmpty(coach.getTrainingSite())
                && !TextUtils.isEmpty(coach.getTestSite());
    }
}
