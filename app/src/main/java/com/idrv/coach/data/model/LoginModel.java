package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.User;
import com.idrv.coach.bean.WChatUserInfo;
import com.idrv.coach.bean.share.WChatLoginInfo;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.constants.ShareConstant;
import com.idrv.coach.data.db.DBService;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.UrlParserManager;
import com.idrv.coach.data.manager.WChatManager;
import com.idrv.coach.utils.EncryptUtil;
import com.idrv.coach.utils.PreferenceUtil;
import com.igexin.sdk.PushManager;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.utils.GsonUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/3/7
 * description: 登录
 *
 * @author sunjianfei
 */
public class LoginModel extends BaseModel {

    /**
     * 呼起微信客户端，获取到Oauth的code，会在WXEntryActivity当中的回调中或得code，用RxBus传递数据给登陆界面
     */
    public void wChatLogin(String transaction, WChatManager.IWXListener listener) {
        WChatManager.getInstance().requestOauthCode(listener, transaction);
    }

    /**
     * 获取微信的用户信息
     *
     * @param info
     * @return
     */
    public Observable<WChatUserInfo> getWChatUserInfo(WChatLoginInfo info) {
        //1.创建Request
        HttpGsonRequest<WChatUserInfo> mRefreshRequest = RequestBuilder.create(WChatUserInfo.class)
                .requestMethod(Request.Method.GET)
                .url(ShareConstant.WEIXIN_USERINFO_URL)
                .put("access_token", info.getAccessToken())
                .put("openid", info.getOpenid())
                .put("lang", "zh_CN")
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(data -> data.encodingFormat())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 登录到服务器
     *
     * @param info
     * @return
     */
    public Observable<User> logIn(WChatUserInfo info) {
        //1.创建Request
        HttpGsonRequest<User> mRefreshRequest = RequestBuilder.create(User.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_WX_LOGIN)
                .put("role", "coach")
                // 因为名字限制了长度，这里传到服务器的提前截断
                .put("nickname", info.getNickname().length() > 18 ? info.getNickname().substring(0, 18) : info.getNickname())
                .put("headimgurl", TextUtils.isEmpty(info.getHeadimgurl()) ? "avatar" : info.getHeadimgurl())
                .put("unionId", info.getUnionid())
                .put("openId", info.getOpenid())
                .put("city", info.getCity())
                .put("province", info.getProvince())
                .put("sex", info.getSex() == 0 ? 1 : info.getSex())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(this::saveUserInfo)
                .doOnNext(user -> {
                    LoginManager.getInstance().setLoginUser(user);
                    //登录成功之后,初始化数据库
                    DBService.init(ZjbApplication.gContext);
                    //更新占位符基本参数
                    UrlParserManager.getInstance().updateBaseParams();
                })
                .doOnNext(user -> {
                    //绑定个推别名
                    String alias = EncryptUtil.md5(user.getUid());
                    PushManager.getInstance().bindAlias(ZjbApplication.gContext, alias);
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 登录成功之后,保存用户信息
     *
     * @param user
     */
    private void saveUserInfo(User user) {
        PreferenceUtil.putString(SPConstant.KEY_USER, GsonUtil.toJson(user));
        PreferenceUtil.putLong(SPConstant.KEY_USER_TOKEN_TIME, System.currentTimeMillis());
        AppInitManager.getInstance().updateAfterLogin(user.getUid(), user.getToken());
    }
}
