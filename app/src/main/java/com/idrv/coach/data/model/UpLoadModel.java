package com.idrv.coach.data.model;

import com.idrv.coach.bean.PhotoUpload;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.utils.EncryptUtil;
import com.idrv.coach.utils.QiNiuUtil;
import com.qiniu.android.storage.UpCompletionHandler;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time: 2016/3/28
 * description:
 * <p>
 * 上传的都extends这个Model，
 * 1.获取token
 * 2.判断文件是否在七牛存在
 * 3.七牛上传
 *
 * @author bigflower
 */
public class UpLoadModel extends BaseModel {
    // 本地文件的路径，用来获取etag
    public String mQiNiuFilePath;
    // 由上面的路径得到etag
    public String mEtagKey;
    // 获取token时，服务器返回的七牛上对应的链接地址
    public String mImgUrl;
    // 获取token时，服务器返回的七牛的上传用的token
    public String mQiNiuToken;

    public void setQiNiuFilePath(String filePath) {
        this.mQiNiuFilePath = filePath;
        mEtagKey = EncryptUtil.getQETAG(filePath);
    }

    /**
     * 获取 token
     *
     * @return
     */
    public Observable<PhotoUpload> getToken() {
        //1.创建Request
        HttpGsonRequest<PhotoUpload> mRefreshRequest = RequestBuilder.create(PhotoUpload.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_GET_QINIU_TOKEN)
                .put("etag", mEtagKey)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(this::onTokenNext)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取token的返回处理
     */
    private void onTokenNext(PhotoUpload photoUpload) {
        mImgUrl = photoUpload.getUrl();
        mQiNiuToken = photoUpload.getToken();
    }

    /**
     * 在网上看看这个图片是否存在
     * 如果图片不存在，才使用七牛把图片上传
     */
    public Observable<String> isFileExit() {
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(mImgUrl)
                .build();
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通过七牛 上传文件
     *
     * @param upCompletionHandler
     */
    public void upLoad(UpCompletionHandler upCompletionHandler) {
        QiNiuUtil.getInstance().qiNiuUpLoad(mQiNiuToken, mEtagKey,
                mQiNiuFilePath, upCompletionHandler, null);
    }

    /**
     * 压缩图片
     *
     * @return
     */
    public Observable<String> resizeImage(String filePath,int size) {
        try {
            return BitmapUtil.saveBitmapAsync(BitmapUtil.decodeSampledBitmapFromFile(filePath, size, size));
        } catch (IOException e) {
            return Observable.error(e);
        }
    }
}
