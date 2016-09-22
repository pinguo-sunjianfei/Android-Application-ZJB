package com.idrv.coach.utils;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * time:2016/7/13
 * description:
 *
 * @author sunjianfei
 */
public class BaseUtil {

    /**
     * 构建一个Observable，异步获取数据
     *
     * @param func 获取数据的操作
     * @param <T>  Observable数据类型
     * @return
     */
    public static <T> Observable<T> makeObservable(Callable<T> func) {
        return Observable.<T>create(subscriber -> {
            try {
                subscriber.onNext(func.call());
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            } finally {
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }
}
