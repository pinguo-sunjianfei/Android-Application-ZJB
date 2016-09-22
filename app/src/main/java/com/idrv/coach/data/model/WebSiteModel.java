package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.Comment;
import com.idrv.coach.bean.Picture;
import com.idrv.coach.bean.WebSitePage;
import com.idrv.coach.bean.WebSitePhoto;
import com.idrv.coach.bean.parser.CommentParser;
import com.idrv.coach.bean.parser.PhotoWallParser;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/6/6
 * description:
 *
 * @author sunjianfei
 */
public class WebSiteModel {
    boolean isLoaded = false;
    String time;
    String spComment;
    String uid;

    public void setUid(String uid) {
        if (LoginManager.getInstance().getUid().equals(uid)) {
            this.uid = "";
        } else {
            this.uid = uid;
        }
    }

    List<Picture> picturesList = new ArrayList<>();
    int pictureNum = 0;

    public int getPictureNum() {
        return pictureNum;
    }

    public void setPictureNum(int pictureNum) {
        this.pictureNum = pictureNum;
    }

    public List<Picture> getPicList() {
        return picturesList;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public Observable<WebSitePage> refresh() {
        time = "";
        spComment = "";
        return request();
    }

    /**
     * 获取个人网站数据
     *
     * @return
     */
    private Observable<WebSitePage> request() {
        //1.创建Request
        HttpGsonRequest<WebSitePage> mRefreshRequest = RequestBuilder.create(WebSitePage.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_MY_WEB_SITE)
                .put("coachId", uid)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(page -> {
                    isLoaded = true;
                    List<Picture> pictures = page.getPictureList();
                    List<Comment> comments = page.getMessageList();

                    if (ValidateUtil.isValidate(pictures)) {
                        time = pictures.get(pictures.size() - 1).getCreated();
                    }

                    if (ValidateUtil.isValidate(comments)) {
                        spComment = comments.get(comments.size() - 1).getTime();
                    }

                    page.setWebSitePhotos(WebSitePage.changePhoto(page.getPictureList()));
                    picturesList.clear();
                    if (ValidateUtil.isValidate(pictures)) {
                        picturesList.addAll(pictures);
                    }

                    pictureNum = page.getPictureNum();
                })
                .doOnNext(page -> {
                    //如果为空,构建假数据
                    List<WebSitePhoto> mPhotoList = page.getWebSitePhotos();
                    List<Comment> comments = page.getMessageList();

                    //照片
                    if (!ValidateUtil.isValidate(mPhotoList)) {
                        mPhotoList = new ArrayList<>();
                        WebSitePhoto webSitePhoto = new WebSitePhoto();
                        webSitePhoto.setIsFake(true);
                        mPhotoList.add(webSitePhoto);
                        page.setWebSitePhotos(mPhotoList);
                    }

                    //评论
                    if (!ValidateUtil.isValidate(comments)) {
                        comments = new ArrayList<>();
                        Comment comment = new Comment();
                        comment.setIsFake(true);
                        comments.add(comment);
                        page.setMessageList(comments);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<WebSitePhoto>> loadPhoto() {
        return getPictureFromServer()
                .map(__ -> WebSitePage.changePhoto(picturesList))
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取个人网站照片的照片
     *
     * @return
     */
    private Observable<List<Picture>> getPictureFromServer() {
        //1.创建Request
        HttpGsonRequest<List<Picture>> mRefreshRequest = RequestBuilder.<List<Picture>>create()
                .parser(new PhotoWallParser())
                .requestMethod(Request.Method.POST)
                .put("type", 2)
                .put("count", 20)
                .put("time", time)
                .put("uid", uid)
                .url(ApiConstant.API_PHOTO_WALL)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .filter(ValidateUtil::isValidate)
                .doOnNext(picturesList::addAll)
                .doOnNext(list -> time = list.get(list.size() - 1).getCreated());
    }

    /**
     * 获取评论数据
     *
     * @return
     */
    public Observable<List<Comment>> loadComment() {
        //1.创建Request
        HttpGsonRequest<List<Comment>> mRefreshRequest = RequestBuilder.<List<Comment>>create()
                .parser(new CommentParser())
                .requestMethod(Request.Method.POST)
                .put("coachId", uid)
                .put("count", 20)
                .put("sp", spComment)
                .url(ApiConstant.API_COMMENT_AND_CONSULATION)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .filter(ValidateUtil::isValidate)
                .doOnNext(list -> spComment = list.get(list.size() - 1).getTime())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 删除照片
     *
     * @return
     */
    public Observable<String> photoDelete(String id) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_PHOTO_DELETE)
                .put("id", id)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 分享个人网站后,回调
     *
     * @return
     */
    public Observable<String> shareComplete() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_WEB_SITE_SHARE_COMPLETE)
                .put("action", 2)
                .put("coachId", LoginManager.getInstance().getUid())
                .put("channel", "app")
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public boolean checkProfileComplete() {
        Coach coach = LoginManager.getInstance().getCoach();
        String schoolName = coach.getDrivingSchool();
        String teachAge = coach.getCoachingDate();

        return !TextUtils.isEmpty(schoolName) && !TextUtils.isEmpty(teachAge) && !"0001-01-01".equals(teachAge);
    }
}
