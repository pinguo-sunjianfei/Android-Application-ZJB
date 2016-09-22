package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.idrv.coach.bean.Picture;
import com.idrv.coach.bean.UploadPhotoAuth;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.bean.parser.PhotoWallParser;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.utils.CollectionUtil;
import com.idrv.coach.utils.EncryptUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.QiNiuUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.qiniu.android.storage.UpCompletionHandler;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/4/22
 * description:
 *
 * @author sunjianfei
 */
public class PhotoWallModel extends BaseModel {
    String time;

    boolean isResize;

    List<UploadPhotoAuth> auths;
    List<Picture> pictureList;

    /**
     * 下拉刷新
     *
     * @param clearAdapter
     * @return
     */
    public Observable<List<Picture>> refresh(Action0 clearAdapter) {
        time = "";
        return request()
                .doOnNext(__ -> clearAdapter.call());
    }


    /**
     * 加载更多
     *
     * @return
     */
    public Observable<List<Picture>> loadMore() {
        return request();
    }

    private Observable<List<Picture>> request() {
        //1.创建Request
        HttpGsonRequest<List<Picture>> mRefreshRequest = RequestBuilder.<List<Picture>>create()
                .parser(new PhotoWallParser())
                .requestMethod(Request.Method.POST)
                .put("type", 2)
                .put("count", 30)
                .put("time", time)
                .url(ApiConstant.API_PHOTO_WALL)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(resp -> {
                    List<Picture> list = resp.data;
                    if (ValidateUtil.isValidate(list)) {
                        time = list.get(list.size() - 1).getCreated();
                    }
                    return list;
                })
                .filter(ValidateUtil::isValidate)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传七牛完成之后,服务器的回调
     *
     * @return
     */
    public Observable<String> onUploadSuccess() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .put("type", 2)
                .put("urls", CollectionUtil.join(",", getUploadPicUrls()))
                .url(ApiConstant.API_PHOTO_WALL_UPLOAD_SUCCESS)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(s -> {
                    //更新照片数量
                    int picNum = PreferenceUtil.getInt(SPConstant.KEY_PIC_NUMBER, -1);
                    picNum = picNum == -1 ? picNum : picNum + auths.size();
                    PreferenceUtil.putInt(SPConstant.KEY_PIC_NUMBER, picNum);
                    reset();
                })
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

    private List<String> getUploadPicUrls() {
        List<String> urls = new ArrayList<>();
        for (UploadPhotoAuth auth : auths) {
            urls.add(auth.getUrl());
        }
        return urls;
    }

    /**
     * 上传图片入口
     */
    public void batchUpload(List<Picture> list) {
        reset();
        pictureList = list;
        Observable<List<Picture>> observable = Observable.<List<Picture>>create(subscriber -> {
            try {
                List<Picture> pictures = compressImageBeforeUpload(pictureList);
                subscriber.onNext(pictures);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.computation());
        observable.subscribe(this::upLoadPhoto, Logger::e);
    }

    /**
     * 获取token并上传
     *
     * @param pictures
     */
    private void upLoadPhoto(List<Picture> pictures) {
        List<String> eTags = getETags(pictures);
        photoUploadAuths(eTags)
                .doOnNext(tags -> auths = tags)
                .subscribe(__ -> fileUpLoad(),
                        e -> {
                            RxBusManager.post(EventConstant.KEY_PHOTO_WALL_UPLOAD_FAILED, "");
                            Logger.e("获取token失败");
                            Logger.e(e);
                        });
    }

    private void fileUpLoad() {
        if (!ValidateUtil.isValidate(auths)) {
            return;
        }
        //先移除一张
        Picture picture = pictureList.remove(0);
        for (UploadPhotoAuth auth : auths) {
            if (picture.geteTag().equals(auth.getEtag())) {
                upLoad(auth.getToken(), picture.geteTag(), picture.getUrl(), (key, info, resp) -> {
                    if (info.isOK()) {
                        onSingleUploadNext();
                    } else {
                        RxBusManager.post(EventConstant.KEY_PHOTO_WALL_UPLOAD_FAILED, "");
                        if (!pictureList.contains(picture)) {
                            pictureList.add(picture);
                        }
                        Logger.e("上传图片失败");
                    }
                });
            }
        }
    }

    /**
     * 状态重置
     */
    public void reset() {
        auths = null;
        pictureList = null;
        isResize = false;
    }

    /**
     * 上传失败,继续重传
     */
    public void reUpload() {
        if (ValidateUtil.isValidate(auths)) {
            fileUpLoad();
        } else {
            batchUpload(pictureList);
        }
    }

    private void onSingleUploadNext() {
        if (ValidateUtil.isValidate(pictureList)) {
            fileUpLoad();
        } else {
            RxBusManager.post(EventConstant.KEY_PHOTO_WALL_UPLOAD_SUCCESS, "");
        }
    }

    private List<String> getETags(List<Picture> pictures) {
        List<String> eTags = new ArrayList<>();
        for (Picture picture : pictures) {
            String eTag = EncryptUtil.getQETAG(picture.getUrl());
            //如果没拿到eTag,则用本地路径MD5
            if (TextUtils.isEmpty(eTag)) {
                eTag = EncryptUtil.md5(picture.getUrl());
            }
            eTags.add(eTag);
            picture.seteTag(eTag);
        }
        return eTags;
    }

    /**
     * 上传图片之前先压缩图片
     *
     * @param pictures
     * @return
     */
    public List<Picture> compressImageBeforeUpload(List<Picture> pictures) {
        if (!ValidateUtil.isValidate(pictures))
            throw new IllegalArgumentException("param is invalid!");
        if (!isResize) {
            for (Picture picture : pictures) {
                String filePath = null;
                try {
                    filePath = BitmapUtil.saveBitmap(BitmapUtil
                            .decodeSampledBitmapFromFile(picture.getUrl(), 500, 500));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                picture.setUrl(filePath);
            }
        }
        return pictures;
    }

    public boolean hasUpload() {
        return PreferenceUtil.getBoolean(SPConstant.KEY_HAS_UPLOAD_PHOTO);
    }

    /**
     * 通过七牛 上传文件
     *
     * @param
     */
    public void upLoad(String token, String eTag, String filePath, UpCompletionHandler upCompletionHandler) {
        QiNiuUtil.getInstance().qiNiuUpLoad(token, eTag,
                filePath, upCompletionHandler, null);
    }
}
