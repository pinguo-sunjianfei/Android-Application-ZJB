package com.idrv.coach.data.constants;

/**
 * time:2016/3/24
 * description: 跳转链接path常量
 *
 * @author sunjianfei
 */
public interface SchemeConstant {
    //1.path名称
    //资讯大厅
    String PATH_NEWS = "/newsHallPage";
    //发现
    String PATH_DISCOVER = "/discoverPage";
    //动态
    String PATH_DYNAMIC = "/dynamicPage";
    //默认进入应用
    String DEFAULT = "/default";

    //2.push类型,用于控制显示小红点
    //新的动态
    int TYPE_NEW_DYNAMIC = 1;
    //新的业务
    int TYPE_NEW_BUSINESS = 2;
    //新的资讯
    int TYPE_NEW_NEWS = 3;
    //新的福利
    int TYPE_NEW_WELFARE = 4;


    //3.需要的参数
    String DIALOG_MESSAGE = "dialogMsg";

    //工具箱的配置
    String PATH_INS = "/insurance";
    String PATH_DRIVER_INS = "/driverIs";
    //统一跳转scheme
    String KEY_APP = "zjb";
    String KEY_HTTP = "http";
    String KEY_HTTPS = "https";
    String KEY_THIRD = "third";
    String KEY_TEL = "tel";
    String KEY_SMS = "smsto";
    String KEY_QQ = "mqqwpa";

    String INTENT_ACTION = "com.idrv.coach.action";
}
