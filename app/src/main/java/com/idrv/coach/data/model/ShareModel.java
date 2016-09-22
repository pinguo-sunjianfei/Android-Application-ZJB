package com.idrv.coach.data.model;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.idrv.coach.R;
import com.idrv.coach.bean.share.IShareProvider;
import com.idrv.coach.bean.share.ShareBean;
import com.idrv.coach.data.constants.ShareConstant;
import com.idrv.coach.data.manager.UrlParserManager;
import com.idrv.coach.data.manager.WChatManager;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.ResHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.wxapi.WXEntryActivity;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.open.utils.SystemUtils;
import com.tencent.tauth.Tencent;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.idrv.coach.ZjbApplication.gContext;

/**
 * time: 2015/9/18
 * description:分享界面对应的model
 *
 * @author sunjianfei
 */
public class ShareModel {
    private static final int THUMB_SIZE = 150;
    /*朋友支持的版本号*/
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private static final IWXAPI mWXAPI = WChatManager.getInstance().WXAPI;
    private IWeiboShareAPI mWeiboShareAPI;
    private Tencent mTencent;
    private Context mContext;
    private Dialog mProgressDialog;

    public void showProgressDialog() {
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(ResHelper.getString(R.string.share_render_picture))
                .show();
    }

    public void dismissProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * 分享界面初始化各个分享平台的信息
     *
     * @param context        WXEntryActivity
     * @param intent         WXEntryActivity.getintent
     * @param handleResponse 是否要处理回调类型 savedInstanceState != null
     */
    public void initPlatforms(Context context, Intent intent, boolean handleResponse) {
        mContext = context;
        //1.得到分享的Activity
        WXEntryActivity activity = (WXEntryActivity) context;
        //2.初始化微博平台的信息
        //2.1.创建分享API实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, ShareConstant.SINA_APP_KEY);
        //2.2.注册app,微博的注册需要放到点击的时候进入的哦
        //mWeiboShareAPI.registerApp();
        //2.3.当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (handleResponse) {
            handleWeiboResponse(intent, activity);
        }
        //3.初始化QQ分享相关
        mTencent = Tencent.createInstance(ShareConstant.QQ_APP_KEY, activity);
    }


    /**
     * 检查当前的微博版本是否支持sdk分享
     *
     * @return
     */
    public boolean checkWeibo() {
        mWeiboShareAPI.registerApp();
        //1. 如果未安装微博客户端，提示安装
        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
            return false;
        }
        //2.判断sdk版本
        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
            if (supportApi < 10351) {
                return false;
            }
            return true;
        } else {
            UIHelper.shortToast(R.string.sina_support_api);
            return false;
        }
    }

    /**
     * 分享到微博
     *
     * @param shareProvider 分享的数据源
     */
    public void share2Weibo(IShareProvider shareProvider) {
        //1.判断是否能够进行分享
        if (!checkWeibo()) {
            return;
        }
        //2.分享
        ShareBean bean = shareProvider.createSinaShareBean();
        // 2.1. 初始化微博的分享消息
        int shareType = shareProvider.getShareType();
        if (shareType == IShareProvider.SHARE_CONTENT_TYPE_WEB) {
            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
            //2.2.构建web对象
            WebpageObject mediaObject = new WebpageObject();
            mediaObject.identify = Utility.generateGUID();
            mediaObject.title = bean.getTitle();
            mediaObject.description = bean.getSummary();
            Bitmap bmp = BitmapUtil.loadAvailBitmap(bean.getImagePath());
            if (Math.min(bmp.getWidth(), bmp.getHeight()) > THUMB_SIZE) {
                Bitmap thumbBmp = BitmapUtil.getThumbBitmap(bmp, THUMB_SIZE);
                mediaObject.setThumbImage(thumbBmp);
                bmp.recycle();
            } else {
                mediaObject.setThumbImage(bmp);
            }
            mediaObject.actionUrl = bean.getTargetUrl();
            mediaObject.defaultText = ResHelper.getString(R.string.web_default_page_text);
            //2.3.将web对象给WeiboMessage
            weiboMessage.mediaObject = mediaObject;
            // 2.4. 初始化从第三方到微博的消息请求
            SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.multiMessage = weiboMessage;
            // 2.5. 发送请求消息到微博，唤起微博分享界面
            mWeiboShareAPI.sendRequest(request);
        } else if (shareType == IShareProvider.SHARE_CONTENT_TYPE_IMAGE) {
            //1.创建Observable
            Observable<String> observable = getShareImagePath(bean);
            //2.接收消息
            observable
                    .doOnSubscribe(this::showProgressDialog)
                    .subscribe(path -> {
                        if (TextUtils.isEmpty(path)) return;
                        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                        //2.1.初始化文字信息
                        String summary = bean.getSummary();
                        if (!TextUtils.isEmpty(summary)) {
                            TextObject textObject = new TextObject();
                            textObject.text = summary;
                            weiboMessage.textObject = textObject;
                        }
                        //2.2.初始化图片信息
                        ImageObject imageObject = new ImageObject();
                        imageObject.imagePath = path;
                        weiboMessage.imageObject = imageObject;
                        //2.3.初始化从第三方到微博的消息请求
                        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
                        request.transaction = String.valueOf(System.currentTimeMillis());
                        request.multiMessage = weiboMessage;
                        //2.4.发送请求消息到微博，唤起微博分享界面
                        mWeiboShareAPI.sendRequest(request);
                    }, e -> {
                        UIHelper.shortToast(R.string.share_error);
                        dismissProgressDialog();
                        Logger.e(e);
                    }, this::dismissProgressDialog);
        }
    }

    /**
     * 处理分享的回调
     *
     * @param intent
     * @param response
     */
    public void handleWeiboResponse(Intent intent, IWeiboHandler.Response response) {
        if (null != intent) {
            mWeiboShareAPI.handleWeiboResponse(intent, response);
        }
    }

    /**
     * 分享内容到微信/朋友圈
     *
     * @param scene:SendMessageToWX.Req.WXSceneSession/SendMessageToWX.Req.WXSceneTimeline
     * @param shareProvider
     */
    public void share2Weixin(int scene, IShareProvider shareProvider) {
        //先添加分享的渠道
        UrlParserManager.getInstance().addParams(UrlParserManager.METHOD_CHANNEL, "wechat");
        //1.判断是否安装了微信
        if (!mWXAPI.isWXAppInstalled()) {
            UIHelper.shortToast(R.string.weixin_not_install);
            return;
        }
        //2.判断是否不支持朋友圈
        if (scene == SendMessageToWX.Req.WXSceneTimeline) {
            int wxSdkVersion = mWXAPI.getWXAppSupportAPI();
            if (wxSdkVersion < TIMELINE_SUPPORTED_VERSION) {
                UIHelper.shortToast(R.string.weixin_timeline_support);
                return;
            }
        }
        //3.构建分享内容
        ShareBean bean = shareProvider.createWeixinShareBean();
        if (IShareProvider.SHARE_CONTENT_TYPE_IMAGE == shareProvider.getShareType()) {
            getShareImagePath(bean)
                    .doOnSubscribe(this::showProgressDialog)
                    .subscribe(path -> {
                        WXImageObject imgObj = new WXImageObject();
                        imgObj.setImagePath(path);
                        WXMediaMessage msg = new WXMediaMessage();
                        msg.mediaObject = imgObj;
                        msg.description = bean.getSummary();
                        Bitmap bmp = BitmapUtil.loadAvailBitmap(bean.getImagePath());
                        if (Math.min(bmp.getWidth(), bmp.getHeight()) > THUMB_SIZE) {
                            Bitmap thumbBmp = BitmapUtil.getThumbBitmap(bmp, THUMB_SIZE);
                            msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true);
                        } else {
                            msg.thumbData = BitmapUtil.bmpToByteArray(bmp, true);
                        }
                        bmp.recycle();
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("img");
                        req.message = msg;
                        req.scene = scene;
                        mWXAPI.sendReq(req);
                    }, e -> {
                        UIHelper.shortToast(R.string.share_error);
                        dismissProgressDialog();
                        Logger.f(e);
                    }, this::dismissProgressDialog);
        } else {
            WXWebpageObject webObj = new WXWebpageObject();
            WXMediaMessage msg = new WXMediaMessage();
            String targetUrl = UrlParserManager.getInstance().parsePlaceholderUrl(bean.getTargetUrl());
            webObj.webpageUrl = targetUrl;
            msg.mediaObject = webObj;
            msg.description = bean.getSummary();
            msg.title = bean.getTitle();
            Bitmap bmp = BitmapUtil.loadAvailBitmap(bean.getImagePath());
            if (null == bmp) {
                bmp = BitmapFactory.decodeResource(gContext.getResources(),
                        R.mipmap.ic_app);
            }
            if (Math.min(bmp.getWidth(), bmp.getHeight()) > THUMB_SIZE) {
                Bitmap thumbBmp = BitmapUtil.getThumbBitmap(bmp, THUMB_SIZE);
                msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true);
            } else {
                msg.thumbData = BitmapUtil.bmpToByteArray(bmp, true);
            }
            bmp.recycle();
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = scene;
            mWXAPI.sendReq(req);
        }
    }

    /**
     * 构建一个transaction用于标识某个请求
     *
     * @param type
     * @return
     */
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 分享到QQ空间
     *
     * @param context  上下文
     * @param provider 数据源
     */
    public void share2QQ(Context context, IShareProvider provider) {
        //先添加分享的渠道
        UrlParserManager.getInstance().addParams(UrlParserManager.METHOD_CHANNEL, "qq");
        WXEntryActivity activity = (WXEntryActivity) context;
        //0.是否安装了QQ
        if (!SystemUtils.checkMobileQQ(context)) {
            UIHelper.shortToast(R.string.qq_support);
            return;
        }
        //1.得到分享的实体
        ShareBean bean = provider.createQzoneShareBean();
        if (TextUtils.isEmpty(bean.getImagePath())) {
            shareWeb2QQ(activity, bean, provider.getShareType());
        } else {
            shareImage2QQ(activity, bean, provider.getShareType());
        }

    }

    private void shareWeb2QQ(WXEntryActivity activity, ShareBean bean, int type) {
        // extarFlag ==1 会弹出发送到QQ控件的对话框，为0则不会弹出
        int extarFlag = 0;
        //2.确定分享类型
        int shareType = type == IShareProvider.SHARE_CONTENT_TYPE_IMAGE ?
                QQShare.SHARE_TO_QQ_TYPE_IMAGE : QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
        //3.构建分享的参数
        final Bundle params = new Bundle();
        if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
            if (!TextUtils.isEmpty(bean.getTitle())) {
                params.putString(QQShare.SHARE_TO_QQ_TITLE, bean.getTitle());
            }

            String targetUrl = bean.getTargetUrl();
            if (!TextUtils.isEmpty(targetUrl)) {
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, UrlParserManager.getInstance().parsePlaceholderUrl(targetUrl));
            }
            if (!TextUtils.isEmpty(bean.getSummary())) {
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, bean.getSummary());
            }
        }
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, ResHelper.getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extarFlag);
        mTencent.shareToQQ(activity, params, activity);
    }

    private void shareImage2QQ(WXEntryActivity activity, ShareBean bean, int type) {
        getShareImagePath(bean)
                .doOnSubscribe(this::showProgressDialog)
                .subscribe(path -> {
                    // extarFlag ==1 会弹出发送到QQ控件的对话框，为0则不会弹出
                    int extarFlag = 0;
                    //2.确定分享类型
                    int shareType = type == IShareProvider.SHARE_CONTENT_TYPE_IMAGE ?
                            QQShare.SHARE_TO_QQ_TYPE_IMAGE : QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
                    //3.构建分享的参数
                    final Bundle params = new Bundle();
                    if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
                        if (!TextUtils.isEmpty(bean.getTitle())) {
                            params.putString(QQShare.SHARE_TO_QQ_TITLE, bean.getTitle());
                        }

                        String targetUrl = bean.getTargetUrl();
                        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, UrlParserManager.getInstance().parsePlaceholderUrl(targetUrl));
                        if (!TextUtils.isEmpty(bean.getSummary())) {
                            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, bean.getSummary());
                        }
                    }
                    if (!TextUtils.isEmpty(bean.getImagePath())) {
                        if (shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
                            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path);
                        } else {
                            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, path);
                        }
                    }
                    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, ResHelper.getString(R.string.app_name));
                    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
                    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extarFlag);
                    mTencent.shareToQQ(activity, params, activity);
                }, e -> {
                    UIHelper.shortToast(R.string.share_error);
                    dismissProgressDialog();
                    Logger.f(e);
                }, this::dismissProgressDialog);
    }

    /**
     * 获取到加水印之后的bitmap
     *
     * @param bean
     * @return
     */
    private Observable<String> getShareImagePath(ShareBean bean) {
        return Observable.<String>create(subscriber -> {
            try {
                String path = bean.getImagePath();
                if (TextUtils.isEmpty(path)) {
                    throw new IllegalArgumentException("the arguments is wrong!");
                } else {
                    String targetPath = BitmapUtil.getShareImagePath(mContext, path, bean.getUserName());
                    subscriber.onNext(targetPath);
                }
            } catch (Exception e) {
                subscriber.onError(e);
                e.printStackTrace();
            } finally {
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
