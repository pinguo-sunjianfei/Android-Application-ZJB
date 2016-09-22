package com.idrv.coach.data.model;

import com.idrv.coach.bean.WebSiteServicesPage;
import com.idrv.coach.data.constants.ApiConstant;
import com.zjb.volley.core.request.HttpGsonRequest;
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
public class SetMyServicesModel {
    //添加和删除业务最后id的总和.
    private long values = 0;

    public void computeValues(long id) {
        values += id;
    }

    public long getValues() {
        return values;
    }

    /**
     * 获取服务列表
     *
     * @return
     */
    public Observable<WebSiteServicesPage> getWebsiteServices() {
        //1.创建Request
        HttpGsonRequest<WebSiteServicesPage> mRefreshRequest = RequestBuilder.create(WebSiteServicesPage.class)
                .url(ApiConstant.API_SERVICES_LIST)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 更新服务列表
     *
     * @return
     */
    public Observable<String> updateWebsiteServices() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .url(ApiConstant.API_UPDATE_SERVICES)
                .put("idSum", values)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
