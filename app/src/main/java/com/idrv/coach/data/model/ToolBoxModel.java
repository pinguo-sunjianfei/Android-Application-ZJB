package com.idrv.coach.data.model;

import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/4/21
 * description:
 *
 * @author sunjianfei
 */
public class ToolBoxModel {


    /**
     * 分享语音名片后,回调
     *
     * @return
     */
    public Observable<String> shareComplete() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_SHARE_VOICE_CARD_SUCCESS)
                .put("studentId", LoginManager.getInstance().getUid())
                .put("coachId", LoginManager.getInstance().getUid())
                .put("action", "1")
                .put("type", "1")
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
