package com.idrv.coach.data.event;


import android.support.annotation.NonNull;

import com.idrv.coach.utils.ValidateUtil;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * time: 2015/8/21
 * description:Rx
 *
 * @author sunjianfei
 */
public class RxBus {
    private static final String TAG = RxBus.class.getSimpleName();
    private static RxBus instance;

    public static synchronized RxBus get() {
        if (null == instance) {
            instance = new RxBus();
        }
        return instance;
    }

    private RxBus() {
    }

    private Hashtable<String, Map<String, Subject>> mSubjects = new Hashtable<>();

    public <T> Observable<T> register(@NonNull String pageKey,
                                      @NonNull String eventKey,
                                      @NonNull Class<T> clazz) {
        Map<String, Subject> map = mSubjects.get(pageKey);
        if (null == map) {
            map = new HashMap<String, Subject>();
            mSubjects.put(pageKey, map);
        }
        Subject<T, T> subject = map.get(eventKey);
        if (null == subject) {
            subject = PublishSubject.create();
            map.put(eventKey, subject);
        }
        return subject;
    }

    public void unregister(@NonNull String pageKey,
                           @NonNull String eventKey) {
        Map<String, Subject> map = mSubjects.get(pageKey);
        if (ValidateUtil.isValidate(map)) {
            map.remove(eventKey);
        }
    }

    public void unregister(@NonNull String pageKey) {
        mSubjects.remove(pageKey);
    }

    public void post(@NonNull Object content) {
        post(content.getClass().getName(), content);
    }

    public void post(@NonNull final Object tag, @NonNull final Object content) {
        //Map.Entry<String, Map<Object, Subject>
        Observable.from(mSubjects.entrySet())
                .map(new Func1<Map.Entry<String, Map<String, Subject>>, Map<String, Subject>>() {
                    @Override
                    public Map<String, Subject> call(Map.Entry<String, Map<String, Subject>> stringMapEntry) {
                        return stringMapEntry.getValue();
                    }
                })
                .filter(new Func1<Map<String, Subject>, Boolean>() {
                    @Override
                    public Boolean call(Map<String, Subject> stringSubjectMap) {
                        return ValidateUtil.isValidate(stringSubjectMap);
                    }
                })
                .map(new Func1<Map<String, Subject>, Subject>() {
                    @Override
                    public Subject call(Map<String, Subject> objectSubjectMap) {
                        return objectSubjectMap.get(tag);
                    }
                })
                .subscribe(new Action1<Subject>() {
                    @Override
                    public void call(Subject subject) {
                        //防止没有注册观察者，这里空指针异常
                        if (subject != null) {
                            subject.onNext(content);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
}
