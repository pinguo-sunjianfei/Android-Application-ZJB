package com.idrv.coach.data.constants;

import com.idrv.coach.BuildConfig;
import com.idrv.coach.utils.PreferenceUtil;

/**
 * Created by sunjianfei on 2016/3/7.
 * 服务器Api类
 */
public interface ApiConstant {
    boolean IS_RELEASE_FLAG = PreferenceUtil.getBoolean(SPConstant.KEY_IS_DEBUG);
    String HOST_DEVELOP_TEST = "http://test.idrv.com.cn/zjb";
    String HOST_RELEASE = "http://zjb.idrv.com.cn";
    String HOST_USER_CENTER_DEVELOP_TEST = "http://test.idrv.com.cn/zjb/v1";
    //        String HOST_USER_CENTER_DEVELOP_TEST = "http://192.168.31.138:4005/v1";
    String HOST_USER_CENTER_RELEASE = "http://i.idrv.com.cn/v1";
    String HOST = BuildConfig.DEBUG_ENABLE ? (IS_RELEASE_FLAG ? HOST_RELEASE : HOST_DEVELOP_TEST) : HOST_RELEASE;
    String USER_CENTER_HOST = BuildConfig.DEBUG_ENABLE ? HOST_USER_CENTER_DEVELOP_TEST : HOST_USER_CENTER_RELEASE;

    //微信登录API
    String API_WX_LOGIN = USER_CENTER_HOST + "/weChatLogin";
    //获取用户信息
    String API_GET_USER_INFO = USER_CENTER_HOST + "/userInfo";
    //邀请码
    String API_INVITE_CODE = USER_CENTER_HOST + "/verifyInviteCode";
    //获取新任务
    String API_NEW_TASK = HOST + "/task/getDailyTask";
    //提现
    String API_CASH = USER_CENTER_HOST + "/cashing";
    //查询佣金和账户余额
    String API_COMMISSION = USER_CENTER_HOST + "/commission";
    //分享回调
    String API_SHARE_TASK = HOST + "/task/shareTask";
    //任务大厅
    String API_BUSINESS_HALL = HOST + "/bs/hall";
    //上传图片获取七牛token
    String API_UPLOAD_AUTH = HOST + "/auth/uploadAuth";
    //上传图片获取七牛token
    String API_BATCH_UPLOAD_AUTH = HOST + "/auth/uploadAuths";
    //获取教练个人资料
    String API_GET_COACH_INFO = USER_CENTER_HOST + "/coachData";

    //修改个人信息
    String API_PUT_USER_INFO = USER_CENTER_HOST + "/userProfile";
    //修改教练信息
    String API_PUT_COACH_INFO = USER_CENTER_HOST + "/coachData";
    // 获取绑定手机验证码：/v1/bindPhoneCode
    String API_GET_BIND_CODE = USER_CENTER_HOST + "/bindPhoneCode";
    // 绑定手机：/v1/bindPhone
    String API_PUT_BIND_PHONE = USER_CENTER_HOST + "/bindPhone";


    // 获取七牛的 Token
    String API_GET_QINIU_TOKEN = HOST + "/auth/uploadAuth";

    //获取首页数据
    String API_GET_HOME_PAGE_DATA = HOST + "/v2/index";
    //七牛服务器
    String API_UPLOAD_QINIU = "http://upload.qiniu.com";
    //提交车险信息
    String API_CAR_INSURANCE = HOST + "/is/post";
    //车险询价历史记录
    String API_INSURANCE_HIS = HOST + "/is/all";

    //判断是否是签约教练
    String API_GET_IS_SIGNED = USER_CENTER_HOST + "/signed";
    //创建团队
    String API_POST_CREATE_TEAM = HOST + "/team/register";
    //获取团队信息
    String API_POST_MY_TEAM = HOST + "/team/myTeam";
    //修改团队名字
    String API_POST_REVISE_NAME = HOST + "/team/rename";
    //邀请团队成员
    String API_POST_TEAM_INVITE = HOST + "/team/invite";

    //动态
    String API_DYNAMIC = HOST + "/discover/trend";
    //上报登录
    String API_REPORT_LOGIN = HOST + "/u/reportLogin";
    //防爆险
    String API_DRIVING_TEST_INS = HOST + "/is/dis/post";
    //学车险列表
    String API_DRIVING_TEST_INS_LIST = HOST + "/is/dis/all";
    //加入团队
    String API_JOIN_TEAM = HOST + "/team/join";
    //点赞
    String API_LIKE = HOST + "/discover/praise";
    //发现item配置
    String API_DISCOVER_ITEMS = HOST + "/discover/v3/items";
    //版本升级
    String API_CHECK_UPDATE = HOST + "/v/update";
    //获取是否开通了个人网站
    String API_IS_OPEN_WEBSITE = HOST + "/admissionweb/isOpenedWeb";
    //获取招生网站数据
    String API_GET_WEBSITE_DATA = HOST + "/admissionweb/webData";
    //获取已有服务和可以添加的服务
    String API_SERVICES_LIST = HOST + "/admissionweb/businessList";
    //分享语音名片成功之后的回调
    String API_SHARE_VOICE_CARD_SUCCESS = HOST + "/popularize/post";
    //修改教练的业务
    String API_UPDATE_SERVICES = HOST + "/admissionweb/updateBusiness";
    //获取照片墙照片
    String API_PHOTO_WALL = HOST + "/admissionweb/pictures";
    //上传照片完成后的回调----照片墙
    String API_PHOTO_WALL_UPLOAD_SUCCESS = HOST + "/admissionweb/addPictures";
    //删除照片
    String API_PHOTO_DELETE = HOST + "/admissionweb/deletePicture";
    //获取弹窗广告
    String API_POP_ADV = HOST + "/ads";
    //清除未读消息状态
    String API_PROFILE_READALL = HOST + "/profile/readAll";
    //获取订单id
    String API_GET_ORDER = HOST + "/order/prePayId";
    //获取钱包明细
    String API_WALLET_DETAILS = HOST + "/appAccount/inAccount";
    //相册明细
    String API_ALBUM = HOST + "/admissionweb/albumInfo";
    //获取一个周期内资讯和个人网站访问信息
    String API_ACCESS_INFO = HOST + "/s/visitInfo";
    //个人网站分享后的回调
    String API_WEB_SITE_SHARE_COMPLETE = HOST + "/profile/action";
    //获取评论和照片的数量
    String API_PIC_AND_COMMENT_NUMBER = HOST + "/admissionweb/picAndCommentNum";
    //我的网站
    String API_MY_WEB_SITE = HOST + "/admissionweb/v2/profileWebData";
    //获取网站的评论
    String API_WEB_SITE_COMMENT = HOST + "/profile/comments";
    //获取传播方案设置
    String API_GET_TRANSMISSION_MODE = HOST + "/u/getTransmissionMode";
    //设置传播模式
    String API_SET_TRANSMISSION_MODE = HOST + "/u/transmissionMode";
    //生成个人网站
    String API_CREATE_WEB_SITE = HOST + "/admissionweb/makeProfileWeb";
    //连续分享的天数
    String API_SHARE_DAYS = HOST + "/s/serialShareDay";
    //优秀的个人网站
    String API_EXCELLENT_WEBSITE = HOST + "/admissionweb/bestProfileWeb";
    //闪屏页的广告
    String API_SPLASH_ADV = HOST + "/splashAds";
    //教练排行
    String API_COACH_RANK = HOST + "/u/rank";
    //传播工具
    String API_SPREAD_TOOLS = HOST + "/transmissionTool/list";
    //购买传播工具
    String API_BUY_GOODS = HOST + "/order/buyGoods";
    //海报模板
    String API_GET_POSTER = HOST + "/transmissionTool/poster";
    //获取评论和咨询
    String API_COMMENT_AND_CONSULATION = HOST + "/mixMessage/AskAndCommentMessage";
    //提交教练认证申请
    String API_COACH_AUTH = HOST + "/v1/coach/authentication";
    //获取教练认证的状态
    String API_COACH_AUTH_STATUS = HOST + "/v1/coach/authenticationState";
    //删除评论或咨询
    String API_DELETE_COMMENT = HOST + "/mixMessage/deleteAskorComment";
    //防爆险说明
    String API_DRIVING_INS_DETAIL = HOST + "/is/instructions";
}
