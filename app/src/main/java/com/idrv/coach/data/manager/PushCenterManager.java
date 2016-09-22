package com.idrv.coach.data.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.PushBean;
import com.idrv.coach.bean.RedPoint;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.cache.ACache;
import com.idrv.coach.data.constants.SchemeConstant;
import com.idrv.coach.ui.SchemeActivity;
import com.idrv.coach.ui.SplashActivity;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.SystemUtil;
import com.idrv.coach.utils.handler.WeakHandler;
import com.igexin.sdk.PushManager;
import com.zjb.volley.utils.GsonUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.idrv.coach.ZjbApplication.gContext;

/**
 * time:2016/3/25
 * description:推送管理
 *
 * @author sunjianfei
 */
public class PushCenterManager {
    private static final String KEY_NEW_MESSAGE = "new_message";
    public static PushCenterManager instance = new PushCenterManager();

    //静默时间，表示从每天的22点开始
    private static final int SILENT_TIME = 22;
    //表示静默的持续时间
    private static final int TIME_DURATION = 9;

    private NotificationManager mNtfManger;
    private static int foregroundNotifyID = 0555;
    private WeakHandler mHandler = new WeakHandler();

    ACache mACache;
    PushBean bean;

    protected PushCenterManager() {
        mACache = ACache.get(ZjbApplication.gContext);
    }

    public static PushCenterManager getInstance() {
        return instance;
    }

    public void init(Context context) {
        PushManager.getInstance().initialize(context);
        //设置静默
        PushManager.getInstance().setSilentTime(context, SILENT_TIME, TIME_DURATION);
        //注册RX bus,用于接收透传过来的push 消息
        RxBusManager.register(this, EventConstant.KEY_NEW_PUSH_MESSAGE, PushBean.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNewMessage, Logger::e);
    }

    private void onNewMessage(PushBean bean) {
        this.bean = bean;
        //如果是首页消息体
        if (null != bean.getMessage()) {
            RxBusManager.post(EventConstant.KEY_HOME_NEW_MESSAGE, bean.getMessage());
        }
        updateRedPoint(bean.getType(), false);
        showNotification(bean);
    }

    private void showNotification(PushBean bean) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ZjbApplication.gContext);
        int ntfId = buildCommonInfo(bean.getTitle(), builder);
        Intent intent;
        //如果是堆栈内已经有Activity,则不走splash界面
        if (ZjbApplication.getStackActivitiesNum() > 0) {
            intent = new Intent(ZjbApplication.gContext, SchemeActivity.class);
        } else {
            intent = new Intent(ZjbApplication.gContext, SplashActivity.class);
        }
        String schema = bean.getSchema();
        if (!TextUtils.isEmpty(schema)) {
            Uri uri = Uri.parse(schema);
            intent.setData(uri);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(ZjbApplication.gContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setContentText(bean.getTitle());

        Notification notification = builder.build();

        if (null == mNtfManger) {
            mNtfManger = (NotificationManager) ZjbApplication.gContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        // 判断app是否在后台
        if (SystemUtil.isAppRunningForeground(ZjbApplication.gContext)) {
            mNtfManger.notify(foregroundNotifyID, notification);
            mHandler.postDelayed(() -> mNtfManger.cancel(foregroundNotifyID), 1000);
        } else {
            mNtfManger.notify(ntfId, notification);
        }
    }

    private int buildCommonInfo(String title, NotificationCompat.Builder builder) {
        int icon = R.mipmap.ic_app;
        long when = System.currentTimeMillis();
        int ntfId = 1;

        builder.setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(gContext.getResources(), R.mipmap.ic_app))
                .setWhen(when)
                .setContentTitle(ZjbApplication.gContext.getString(R.string.app_name))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setSound(Uri.parse("android.resource://" + gContext.getPackageName() + "/" + R.raw.notify_sound))
                .setTicker(title);
        return ntfId;
    }

    public void updateRedPoint(int type, boolean isClear) {
        Observable<RedPoint> observable = Observable.<RedPoint>create(subscriber -> {
            String json = mACache.getAsString(KEY_NEW_MESSAGE);
            RedPoint redPoint;
            try {
                if (!TextUtils.isEmpty(json)) {
                    redPoint = GsonUtil.fromJson(json, RedPoint.class);
                } else {
                    redPoint = new RedPoint();
                }
            } catch (Exception e) {
                redPoint = new RedPoint();
            }
            switch (type) {
                case SchemeConstant.TYPE_NEW_DYNAMIC:
                    redPoint.setDynamicStatus(isClear ? 0 : 1);
                    break;
                case SchemeConstant.TYPE_NEW_BUSINESS:
                    redPoint.setBusinessStatus(isClear ? 0 : 1);
                    break;
                case SchemeConstant.TYPE_NEW_NEWS:
                    redPoint.setNewsStatus(isClear ? 0 : 1);
                    break;
                case SchemeConstant.TYPE_NEW_WELFARE:
                    redPoint.setWelfareStatus(isClear ? 0 : 1);
                    break;
            }
            mACache.remove(KEY_NEW_MESSAGE);
            mACache.put(KEY_NEW_MESSAGE, GsonUtil.toJson(redPoint));
            subscriber.onNext(redPoint);
        }).subscribeOn(Schedulers.computation());
        observable.subscribe(redPoint -> RxBusManager.post(EventConstant.KEY_NEW_MESSAGE, redPoint), Logger::e);
    }

    public Observable<RedPoint> getRedPoint() {
        return Observable.<RedPoint>create(subscriber -> {
            String json = mACache.getAsString(KEY_NEW_MESSAGE);
            RedPoint redPoint;
            try {
                if (!TextUtils.isEmpty(json)) {
                    redPoint = GsonUtil.fromJson(json, RedPoint.class);
                } else {
                    redPoint = new RedPoint();
                }
            } catch (Exception e) {
                redPoint = new RedPoint();
            }
            subscriber.onNext(redPoint);
        }).subscribeOn(Schedulers.computation());
    }
}
