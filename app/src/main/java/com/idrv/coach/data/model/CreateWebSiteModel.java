package com.idrv.coach.data.model;

import com.idrv.coach.bean.PicAndCommentNum;
import com.idrv.coach.bean.WebSite;
import com.idrv.coach.bean.parser.WebSiteParser;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/6/2
 * description:
 *
 * @author sunjianfei
 */
public class CreateWebSiteModel {
    protected int photoNum;
    private int shareDays;
    private List<WebSite> mWebSites;

    public int getPhotoNum() {
        return photoNum;
    }

    public int getShareDays() {
        return shareDays;
    }

    public List<WebSite> getWebSites() {
        return mWebSites;
    }

    public Observable<PicAndCommentNum> getPicNum() {
        //1.创建Request
        HttpGsonRequest<PicAndCommentNum> mRefreshRequest = RequestBuilder.create(PicAndCommentNum.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_PIC_AND_COMMENT_NUMBER)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(num -> {
                    PreferenceUtil.putInt(SPConstant.KEY_PIC_NUMBER, num.getPictureNum());
                    photoNum = num.getPictureNum();
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 生成个人网站
     *
     * @return
     */
    public Observable<String> createWebSite() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_CREATE_WEB_SITE)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(s -> PreferenceUtil.putString(SPConstant.KEY_IS_OPEN_WEBSITE + LoginManager.getInstance().getUid(), "true"))
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取连续分享资讯的天数
     *
     * @return
     */
    public Observable<String> getContinuousShareDays() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_SHARE_DAYS)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(s -> shareDays = Integer.parseInt(s))
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取优秀的个人网站
     *
     * @return
     */
    public Observable<List<WebSite>> getExcellentWebSite() {
        //1.创建Request
        HttpGsonRequest<List<WebSite>> mRefreshRequest = RequestBuilder.<List<WebSite>>create()
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_EXCELLENT_WEBSITE)
                .parser(new WebSiteParser())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .filter(ValidateUtil::isValidate)
                .doOnNext(data -> mWebSites = data)
                .observeOn(AndroidSchedulers.mainThread());
    }

}
