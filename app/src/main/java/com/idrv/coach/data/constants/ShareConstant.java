package com.idrv.coach.data.constants;


import com.idrv.coach.utils.DebugUtil;

/**
 * time: 15/6/11
 * description: 分享相关的常量
 *
 * @author sunjianfei
 */
public interface ShareConstant {
    // 新浪微博分享相关
    String REDIRECT_URL = "http://share.camera360.com/login_callback.json";
    String SINA_APP_KEY = "3811866140";
    String SINA_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    // 微信分享相关
    String WEIXIN_APPID = "wxebda976b488d5382";
    String WEIXIN_APPSECRET = "734a59f4b5406369145f45af7485a1db";
    String WEIXIN_LOGIN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    String WEIXIN_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo";
    String SHARE_URL_ALBUM = DebugUtil.isDebug() ? "http://activity-test.camera360.com/album/dist/index.html?aid=%s"
            : "http://activity.camera360.com/album/dist/index.html?aid=%s";
    public static final String SHARE_URL_USER_INFO = DebugUtil.isDebug() ? "http://activity-test.camera360.com/album/dist/personalHomePage.html?uid=%s"
            : "http://activity.camera360.com/album/dist/personalHomePage.html?uid=%s";
    //QQ分享相关
    String QQ_APP_KEY = "1104999798";
    String QQ_APPSECRET = "zNdCVAM08htg1p2n";
    //站点名字
    String SITE_CODE_SINA = "sina";
    String SITE_CODE_WEIXIN = "wechat";
    long DEFAULT_TIME = 800;

    //默认的分享配图
    String SHARE_DEFAULT_IMAGE = "http://7xr5ja.com2.z0.glb.qiniucdn.com/logo.jpg";

    //驾考方保险分享的配图
    String SHARE_INS_IMAGE = "http://7xjo4p.com1.z0.glb.clouddn.com/fangbaoxian_edu.png";
}
