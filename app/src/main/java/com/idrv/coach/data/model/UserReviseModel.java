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
 * time: 2016/3/25
 * description: 修改教练信息和个人信息
 *
 * @author bigflower
 */
public class UserReviseModel extends BaseModel {

    public static final String INTENT_KEY = "intent_key";
    public static final String INTENT_TEXT = "intent_text";

    public static final String KEY_NAME = "nickname";
    public static final String KEY_TRAIN = "trainingSite";
    public static final String KEY_TEST = "testSite";
    public static final String KEY_SCHOOL = "drivingSchool";

    public int gMaxLength = 18;
    public String gHttpKey;
    public String gOldText;


    /**
     * 修改个人基本信息，
     *
     * @param value
     * @return
     */
    public Observable<String> putUserInfo(String value) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.PUT)
                .url(ApiConstant.API_PUT_USER_INFO)
                .put(gHttpKey, value)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .map(__ -> onUserNext(value))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private String onUserNext(String text) {
        try {
            User user = GsonUtil.fromJson(PreferenceUtil.getString(SPConstant.KEY_USER), User.class);
            user.setNickname(text);
            PreferenceUtil.putString(SPConstant.KEY_USER, GsonUtil.toJson(user));
            LoginManager.getInstance().setLoginUser(user);
        } catch (Exception e) {
            Logger.e(e.toString());
        }
        return text;
    }

    /**
     * 修改教练信息，
     *
     * @param value
     * @return
     */
    public Observable<String> putCoachInfo(String value) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.PUT)
                .url(ApiConstant.API_PUT_COACH_INFO)
                .put(gHttpKey, value)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .map(__ -> onCoachNext(value))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private String onCoachNext(String text) {
        try {
            Coach coach = LoginManager.getInstance().getCoach();
            if (KEY_TRAIN.equals(gHttpKey)) {
                coach.setTrainingSite(text);
            } else if (KEY_SCHOOL.equals(gHttpKey)) {
                coach.setDrivingSchool(text);
            } else {
                coach.setTestSite(text);
            }
            PreferenceUtil.putString(SPConstant.KEY_COACH, GsonUtil.toJson(coach));
        } catch (Exception e) {
            Logger.e(e.toString());
        }
        return text;
    }

}
