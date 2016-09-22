package com.idrv.coach.data.model;

import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.User;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.Logger;
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
 * Created by bigflower on 2016/3/14.
 * <p>
 * description: 修改用户信息：教练的教龄
 * <p>
 * 注：修改过后不会返回最新修改后的结果， 故，应根据key来手动set对应的value，并保存到本地。
 */
public class UserInfoModel extends UpLoadModel {

    public User gUser;
    public Coach gCoach;

    public String httpKey;

    public UserInfoModel() {
        refreshUser();
        refreshCoach();
    }

    public void refreshUser() {
        try {
            gUser = GsonUtil.fromJson(PreferenceUtil.getString(SPConstant.KEY_USER), User.class);
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }

    public void refreshCoach() {
        try {
            gCoach = GsonUtil.fromJson(PreferenceUtil.getString(SPConstant.KEY_COACH), Coach.class);
            LoginManager.getInstance().setCoach(gCoach);
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }

    /**
     * 修改个人基本信息，
     *
     * @return
     */
    public Observable<User> putUserInfo() {
        //1.创建Request
        HttpGsonRequest<User> mRefreshRequest = RequestBuilder.create(User.class)
                .requestMethod(Request.Method.PUT)
                .url(ApiConstant.API_PUT_USER_INFO)
                .put("headimgurl", mImgUrl)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(__ -> {
                    saveUserInfo(mImgUrl);
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 保存个人基本信息:头像
     *
     * @param value
     */
    private void saveUserInfo(String value) {
        try {
            User user = GsonUtil.fromJson(PreferenceUtil.getString(SPConstant.KEY_USER), User.class);
            user.setHeadimgurl(value);
            // save
            PreferenceUtil.putString(SPConstant.KEY_USER, GsonUtil.toJson(user));
            LoginManager.getInstance().setLoginUser(user);
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }

    /**
     * 修改教练信息，
     *
     * @param key
     * @param value
     * @return
     */
    public Observable<Coach> putCoachInfo(String key, String value) {
        //1.创建Request
        HttpGsonRequest<Coach> mRefreshRequest = RequestBuilder.create(Coach.class)
                .requestMethod(Request.Method.PUT)
                .url(ApiConstant.API_PUT_COACH_INFO)
                .put(key, value)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(coach -> {
                    // 更新User表中key对应的Value
                    saveCoachInfo(key, value);
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 保存教练信息
     *
     * @param key
     * @param value
     */
    private void saveCoachInfo(String key, String value) {
        if ("coachingDate".equals(key)) {
            gCoach.setCoachingDate(value);
            // save
            PreferenceUtil.putString(SPConstant.KEY_COACH, GsonUtil.toJson(gCoach));
        }
    }


    public User getgUser() {
        if (gUser == null) {
            refreshUser();
        }
        // TODO 如果gUser还是为null呢（SP里没有）
        return gUser;
    }

    public Coach getCoach() {
        if (gCoach == null) {
            refreshCoach();
        }
        return gCoach;
    }


}
