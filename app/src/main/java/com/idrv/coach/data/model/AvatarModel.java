package com.idrv.coach.data.model;

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
 * time: 2016/3/28
 * description: 头像
 *
 * @author bigflower
 */
public class AvatarModel extends UpLoadModel {

    // 缓存老的图片链接，如果上传失败了，就显示这个
    public String gAvatarUrl ;
    // 记录屏幕的宽度
    public int gScreenHeight;

    public AvatarModel() {
    }

    /**
     * 修改个人基本信息，
     *
     * @param key
     * @param value
     * @return
     */
    public Observable<User> putUserInfo(String key, String value) {
        //1.创建Request
        HttpGsonRequest<User> mRefreshRequest = RequestBuilder.create(User.class)
                .requestMethod(Request.Method.PUT)
                .url(ApiConstant.API_PUT_USER_INFO)
                .put(key, value)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(__ -> {
                    saveUserInfo(value);
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

}
