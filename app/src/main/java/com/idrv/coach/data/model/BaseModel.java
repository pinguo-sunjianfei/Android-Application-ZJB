package com.idrv.coach.data.model;

import com.idrv.coach.bean.UploadPhotoAuth;
import com.idrv.coach.bean.parser.UploadAuthlParser;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.pool.RequestPool;
import com.idrv.coach.utils.CollectionUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.io.File;
import java.util.List;

import rx.Observable;

/**
 * time: 15/10/17
 * description: 如果接口出现重复调用，那么就将重复调用的接口部分放入此类当中，
 *
 * @author sunjianfei
 */
public class BaseModel {

    /**
     * 上传图片的认证接口
     */
    public Observable<UploadPhotoAuth> photoUploadAuth(final String eTag) {
        HttpGsonRequest<UploadPhotoAuth> request = RequestBuilder.create(UploadPhotoAuth.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_UPLOAD_AUTH)
                .put("etag", eTag)
                .build();
        return RequestPool.gRequestPool.request(request)
                .filter(response -> response != null && response.data != null)
                .map(response -> response.data);
    }

    /**
     * 批量上传图片的认证接口
     */
    public Observable<List<UploadPhotoAuth>> photoUploadAuths(List<String> eTags) {
        HttpGsonRequest<List<UploadPhotoAuth>> request = RequestBuilder.<List<UploadPhotoAuth>>create()
                .requestMethod(Request.Method.POST)
                .parser(new UploadAuthlParser())
                .url(ApiConstant.API_BATCH_UPLOAD_AUTH)
                .put("etags", CollectionUtil.join(",", eTags))
                .build();
        return RequestPool.gRequestPool.request(request)
                .filter(response -> response != null && response.data != null)
                .map(response -> response.data);
    }

    public Observable<String> fileUpload(String etag, String filePath, String token) {
        HttpGsonRequest<String> request = RequestBuilder.create(String.class)
                .url(ApiConstant.API_UPLOAD_QINIU)
                .paramsType(RequestBuilder.TYPE_NO_NEED_BASE)
                .put("token", token)
                .put("key", etag)
                .put("file", new File(filePath))
                .build();
        return RequestPool.gRequestPool.request(request)
                .filter(response -> response != null)
                .map(HttpResponse::getData);
    }
}
