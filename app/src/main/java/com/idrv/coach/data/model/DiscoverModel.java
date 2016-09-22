package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.DiscoverPage;
import com.idrv.coach.data.cache.ACache;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.PreferenceUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.utils.GsonUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/3/31
 * description:
 *
 * @author sunjianfei
 */
public class DiscoverModel {
    private static final String KEY_DISCOVER_ITEMS = "discover_items_cache";
    ACache mACache;

    public DiscoverModel() {
        mACache = ACache.get(ZjbApplication.gContext);
    }

    /**
     * 从缓存获取最新数据
     *
     * @return
     */
    public Observable<DiscoverPage> getDiscoverPageCache() {
        return Observable.<DiscoverPage>create(subscriber -> {
            String json = mACache.getAsString(KEY_DISCOVER_ITEMS);
            if (!TextUtils.isEmpty(json)) {
                try {
                    DiscoverPage page = GsonUtil.fromJson(json, DiscoverPage.class);
                    subscriber.onNext(page);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            } else {
                subscriber.onError(new NullPointerException());
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 从服务器获取最新的数据
     *
     * @return
     */
    public Observable<DiscoverPage> getDiscoverPage() {
        //1.创建Request
        HttpGsonRequest<DiscoverPage> mRefreshRequest = RequestBuilder.create(DiscoverPage.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_DISCOVER_ITEMS)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(page -> {
                    //1.先清除缓存
                    mACache.remove(KEY_DISCOVER_ITEMS);
                    //2.存入缓存
                    mACache.put(KEY_DISCOVER_ITEMS, GsonUtil.toJson(page));
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取是否开通了教练空间
     *
     * @return
     */
    public Observable<String> getWebsiteOpenStatus() {
        if (isOpenWebSite()) {
            return Observable.just("website is active");
        }
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_IS_OPEN_WEBSITE)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(data -> PreferenceUtil.putString(SPConstant.KEY_IS_OPEN_WEBSITE + LoginManager.getInstance().getUid(), data))
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 判断是否开通了教练空间
     *
     * @return
     */
    public boolean isOpenWebSite() {
        String result = PreferenceUtil.getString(SPConstant.KEY_IS_OPEN_WEBSITE + LoginManager.getInstance().getUid());
        if (!TextUtils.isEmpty(result)) {
            if (result.equals("true")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
