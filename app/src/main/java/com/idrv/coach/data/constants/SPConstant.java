package com.idrv.coach.data.constants;

/**
 * time: 15/7/17
 * description:存放SharedPreference当中的key值
 *
 * @author sunjianfei
 */
public interface SPConstant {
    // sharepreference文件名
    String SP_NAME = "zjb";
    /* 存储eid*/
    String KEY_EID = "eid";
    /* 用户相关 */
    String KEY_USER = "user";
    String KEY_COACH = "coach";

    /* 用户登录获取到token的时间*/
    String KEY_USER_TOKEN_TIME = "user_token_time";
    //是否第一次启动助驾帮,用来显示引导页
    String KEY_FIRST_USE = "first_use";

    /* 推送的clientId */
    String KEY_PUSH_CLIENT_ID = "push_client_id";
    /* 是否请求过录音权限 */
    String KEY_IS_RECORD_PERMISSION = "is_record_permission";

    //存储本地定位时间戳的key
    String KEY_LOCATION_TIME = "location_time";
    //经度
    String KEY_LON = "lon";
    //纬度
    String KEY_LAT = "lat";
    //定位信息
    String KEY_LOCATION = "location_info";

    //是否显示过邀请的对话框
    String KEY_INVITE = "invite";
    //是否显示过创建团队的提示框
    String KEY_CREATE_TEAM_NOTICED = "is_create_team_noticed";
    /* 上次检查更新的时间(毫秒) */
    String KEY_LAST_CHECK_UPDATE = "last_check_update_millis";
    //是否开通了个人网站
    String KEY_IS_OPEN_WEBSITE = "is_open_web_site";
    //上一次的影响力
    String LAST_INFLUENCE = "last_influence";
    //广告图片下载完成
    String KEY_ADV_PIC = "adv_pic";
    //点击查看的广告
    String KEY_ADV_VIEWS = "adv_views";
    //上次弹出广告的时间
    String KEY_SHOW_ADV_TIME = "show_adv_time";
    //第一次上传照片
    String KEY_HAS_UPLOAD_PHOTO = "has_upload_photo";
    //每周五记录一次资讯的访问数量
    String KEY_NEWS_ACCESS_NUM = "news_access_num";
    //周五是否检测过
    String KEY_NEWS_ACCESS_CHECKED = "news_access_checked";
    //是否显示过海报制作引导
    String KEY_HAS_SHOW_POSTER_MAKE_GUIDE = "has_show_poster_make_guide";
    //照片的数量
    String KEY_PIC_NUMBER = "pic_number";
    //评论数量
    String KEY_COMMENT_NUM = "comment_number";
    //闪屏页广告数据
    String KEY_SPLASH = "splash_data";
    //闪屏页图片
    String KEY_SPLASH_PIC = "splash_pic";
    //记录分享资讯的时间
    String KEY_SHARE_NEWS_TIME = "share_news_time";
    //保存到系统相册海报模板的文件路径
    String KEY_SAVED_POSTER_PATH = "saved_poster_path%s";
    //切换正式环境的标识
    String KEY_IS_DEBUG = "key_is_debug";
    //传播方案类型
    String KEY_PRO_TYPE = "pro_type";
}
