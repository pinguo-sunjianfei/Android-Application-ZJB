package com.idrv.coach.data.manager;

import android.content.Intent;
import android.text.TextUtils;

import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.share.WChatLoginInfo;
import com.idrv.coach.data.constants.ShareConstant;
import com.idrv.coach.data.pool.RequestPool;
import com.idrv.coach.utils.ValidateUtil;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time: 15/7/17
 * description:微信登陆认证的管理器
 *
 * @author sunjianfei
 */
public class WChatManager {
    private boolean isWxCallBack = false;
    private static WChatManager INSTANCE;

    public IWXAPI WXAPI;

    //用来区分是从那个页面调用微信登录
    private String mTransaction = null;

    /**
     * 构造方法当中初始化IWXAPI
     */
    private WChatManager() {
        WXAPI = WXAPIFactory.createWXAPI(ZjbApplication.gContext, ShareConstant.WEIXIN_APPID, false);
        WXAPI.registerApp(ShareConstant.WEIXIN_APPID);
    }

    public static WChatManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WChatManager();
        }
        return INSTANCE;
    }

    public String getTransaction() {
        return mTransaction;
    }

    public boolean isWxCallBack() {
        return isWxCallBack;
    }

    public void setIsWxCallBack(boolean isWxCallBack) {
        this.isWxCallBack = isWxCallBack;
    }

    /**
     * 第一步、获得授权的code
     */
    public void requestOauthCode(IWXListener listener, String transaction) {
        if (!WXAPI.isWXAppInstalled()) {
            if (listener != null) {
                listener.wxUnInstall();
            }
            return;
        }
        //1.生成一个此链接的唯一标识，返回的时候会原封不动地返回回来
        Random random = new Random();
        int value = random.nextInt(1000) + 1;
        mTransaction = transaction;
        String state = mTransaction + value;
        //2.发出请求，获取code
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = state;
        req.transaction = mTransaction;
        WXAPI.sendReq(req);
    }

    /**
     * 第二步、通过上一步得到的code，获取到access_token
     * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     *
     * @param code 上一次获取到的code
     */
    public Observable<WChatLoginInfo> login(String code) {
        HttpGsonRequest<WChatLoginInfo> loginRequest = RequestBuilder.create(WChatLoginInfo.class)
                .requestMethod(Request.Method.GET)
                .paramsType(RequestBuilder.TYPE_NO_NEED_BASE)
                .url(ShareConstant.WEIXIN_LOGIN_URL)
                .put("appid", ShareConstant.WEIXIN_APPID)
                .put("secret", ShareConstant.WEIXIN_APPSECRET)
                .put("code", code)
                .put("grant_type", "authorization_code")
                .build();
        return RequestPool.gRequestPool.request(loginRequest)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 处理微信的回调
     *
     * @param intent  activity的意图界面
     * @param handler 微信回调
     */
    public void handleIntent(Intent intent, IWXAPIEventHandler handler) {
        WXAPI.handleIntent(intent, handler);
    }

    /**
     * 微信的回调接口
     */
    public interface IWXListener {
        void wxUnInstall();
    }

    /**
     * Map转List
     */
    private static List<String> getListParameters(Map<String, String> params) {
        if (!ValidateUtil.isValidate(params)) {
            throw new NullPointerException("params can't be null");
        }
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                String join = key + "=" + value;
                if (!list.contains(join)) {
                    list.add(join);
                }
            }
        }
        return list;
    }

}
