package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.Task;
import com.idrv.coach.bean.visitInfoPage;
import com.idrv.coach.data.cache.ACache;
import com.idrv.coach.data.constants.ApiConstant;
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
 * time:2016/3/9
 * description:
 *
 * @author sunjianfei
 */
public class NewsHallModel extends BaseModel {
    private static final String TASK_CACHE = "task_cache";
    ACache mACache;
    Task task;

    public NewsHallModel() {
        mACache = ACache.get(ZjbApplication.gContext);
    }

    /**
     * 从服务器获取新的任务
     *
     * @return
     */
    public Observable<Task> getNewTask() {
        //1.创建Request
        HttpGsonRequest<Task> mRefreshRequest = RequestBuilder.create(Task.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_NEW_TASK)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(resp -> {
                    task = resp.data;
                    return task;
                })
                .doOnNext(task -> {
                    //1.先删除原有的缓存
                    mACache.remove(TASK_CACHE);
                    //2.重新存入新的数据
                    mACache.put(TASK_CACHE, GsonUtil.toJson(task));
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取文件缓存的任务
     *
     * @return
     */
    public Observable<Task> getTaskCache() {
        Observable<Task> observable = Observable.<Task>create(subscriber -> {
            try {
                String taskStr = mACache.getAsString(TASK_CACHE);
                if (TextUtils.isEmpty(taskStr)) {
                    subscriber.onError(new NullPointerException("no cache!"));
                } else {
                    Task task = GsonUtil.fromJson(taskStr, Task.class);
                    this.task = task;
                    subscriber.onNext(task);
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
        return observable;
    }

    /**
     * 分享任务后,回调
     *
     * @return
     */
    public Observable<String> shareComplete(String channel) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_SHARE_TASK)
                .put("shareChannel", channel)
                .put("taskId", task.getId())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取一个周期内资讯和个人网站访问信息
     *
     * @return
     */
    public Observable<visitInfoPage> getVisitInfo() {
        //1.创建Request
        HttpGsonRequest<visitInfoPage> mRefreshRequest = RequestBuilder.create(visitInfoPage.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_ACCESS_INFO)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Task getTask() {
        return task;
    }
}
