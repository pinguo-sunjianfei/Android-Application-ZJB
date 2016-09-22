package com.idrv.coach.data.model;

import com.idrv.coach.bean.Album;
import com.idrv.coach.bean.parser.AlbumParser;
import com.idrv.coach.data.constants.ApiConstant;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/5/23
 * description:
 *
 * @author sunjianfei
 */
public class AlbumModel {

    /**
     * 获取相册列表
     *
     * @return
     */
    public Observable<List<Album>> getAlbum() {
        //1.创建Request
        HttpGsonRequest<List<Album>> mRefreshRequest = RequestBuilder.<List<Album>>create()
                .requestMethod(Request.Method.POST)
                .parser(new AlbumParser())
                .url(ApiConstant.API_ALBUM)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
