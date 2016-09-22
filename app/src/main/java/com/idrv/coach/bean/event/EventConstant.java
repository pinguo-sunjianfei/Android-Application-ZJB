package com.idrv.coach.bean.event;

/**
 * time: 2015/8/21
 * description:RxBus上面传递的事件类型
 *
 * @author sunjianfei
 */
public interface EventConstant {
    /*微信获取OAuth_code成功*/
    String KEY_WX_OAUTH_CODE = "wx_oauth_code";
    //新的推送消息
    String KEY_NEW_PUSH_MESSAGE = "new_push_message";
    //分享完成的通知
    String KEY_SHARE_COMPLETE = "share_complete";
    //修改驾校，
    String KEY_REVISE_DRISCHOOL = "revise_item_drischool";
    //修改考场，
    String KEY_REVISE_TESTSITE = "revise_test_site";
    //修改练车场地，
    String KEY_REVISE_TRAINSITE = "revise_train_site";
    //修改昵称，
    String KEY_REVISE_NICKNAME = "revise_nickname";
    //上传了二维码
    String KEY_REVISE_QRCODE = "revise_item_qrcode";
    //上传了教练证
    String KEY_REVISE_COACHCARD = "revise_item_coachcard";
    //绑定了手机号
    String KEY_REVISE_PHONE = "revise_item_phone";
    //修改了头像
    String KEY_REVISE_AVATAR = "revise_item_avatar";
    //修改了语音
    String KEY_REVISE_RECORD = "revise_item_record";

    //修改了个人资料，通知[我的]页面
    String KEY_REVISE_USERINFO = "revise_userinfo";

    //文件上传失败的错误
    String KEY_FILE_UPLOAD_FAILED = "file_upload_failed";

    //单个文件上传成功
    String KEY_FILE_UPLOAD_SUCCESS = "single_file_upload_success";
    //修改团队名称成功
    String KEY_TEAM_NAME = "team_revise_name";
    //邀请成员成功
    String KEY_TEAM_MUMBER = "team_invite_mumber";
    //创建团队成功
    String KEY_TEAM_CREATE = "team_create";

    /*高德定位*/
    String KEY_LOCATION = "location";

    //有新消息
    String KEY_NEW_MESSAGE = "new_message";
    //当分享回调完成之后
    String KEY_SHARE_CALL_BACK_COMPLETE = "share_call_back_complete";
    //资讯分享完成之后，增加通知首页增加打广告的次数
    String KEY_NEWS_OR_WEBSITE_SHARE_COMPLETE = "news_or_website_share_complete";

    //教学引导,点击关闭的时候的消息
    String KEY_TEACH_GUIDE_CLOSE = "teach_guide_close";
    //提现成功之后,通知上一个页面关闭或通知钱包页面刷新
    String KEY_WITHDRAW_COMPLETE = "withdraw_complete";
    //招生网站拉取数据成功之后,通知fragment显示数据
    String KEY_WEBSITE_DATA_LOAD_SUCCESS = "web_site_data";

    //照片墙上传成功
    String KEY_PHOTO_WALL_UPLOAD_SUCCESS = "photo_wall_upload_success";
    //照片墙上传失败
    String KEY_PHOTO_WALL_UPLOAD_FAILED = "photo_wall_upload_failed";
    //删除照片
    String KEY_PHOTO_DELETE = "photo_delete";
    //删除照片成功
    String KEY_PHOTO_DELETE_SUCCESS = "photo_delete_success";
    //微信支付结果
    String KEY_PAY_RESULT = "pay_result";
    //刷新最新照片
    String KEY_REFRESH_PIC = "refresh_pic";
    //驾校保存成功后通知其他界面关闭
    String KEY_SCHOOL_SAVE_SUCCESS = "school_save_success";
    //token 过期的消息
    String KEY_TOKEN_EXPIRED = "token_expired";
    //设置传播方案成功
    String KEY_SET_TRANSMISSION_SUCCESS = "set_transmission_success";
    //在推荐网站界面.点击进入个人网站,通知上一个界面关闭
    String KEY_CLOSE_SUCCESS_PAGE = "close_success_page";
    //传播工具,预览点击底部按钮,通知上一个界面处理逻辑
    String KEY_SPREAD_TOOL_PREVIEW_BOTTOM_BUTTON_CLICK = "spread_tool_preview_bottom_button_click";
    //传播工具,当支付完成之后,通知列表界面改变支付状态
    String KEY_SPREAD_TOOL_PAY_SUCCESS = "spread_tool_pay_success";
    //首页有新消息
    String KEY_HOME_NEW_MESSAGE = "home_new_message";
    //防爆险提交保单成功之后的消息
    String KEY_DRIVING_INS_COMMIT_SUCCESS = "driving_ins_commit_success";
    //删除评论成功
    String KEY_COMMENT_DELETE_SUCCESS = "delete_comment_success";
}
