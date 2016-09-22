package com.idrv.coach.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.idrv.coach.bean.PushBean;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.igexin.sdk.PushConsts;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * time:15-9-8
 * description: 用于接受推送透传消息的静态广播
 *
 * @author sunjianfei
 */
public class PushGeTuiReceiver extends BroadcastReceiver {
    private String mClientId = null;
    private static final String TAG = "PushGeTuiReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Logger.e(TAG, "onReceive() action=" + bundle.getInt(PushConsts.CMD_ACTION));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            // 获取透传数据
            case PushConsts.GET_MSG_DATA:
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    // 获得透传的数据
                    Logger.e(TAG, "Get Payload:" + data);
                    parseJson(data)
                            .subscribe(pushBean -> RxBusManager.post(EventConstant.KEY_NEW_PUSH_MESSAGE, pushBean), Logger::e);
                }
                break;
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)0
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                mClientId = bundle.getString("clientid");
                Logger.e(TAG, "cid:" + mClientId);
                if (null != mClientId) {
                    PreferenceUtil.putString(SPConstant.KEY_PUSH_CLIENT_ID, mClientId);
                    AppInitManager.getInstance().getSdkEntity().setCid(mClientId);
                }
                break;
            default:
                break;
        }
    }

    private Observable<PushBean> parseJson(String data) {
        return Observable.<PushBean>create(subscriber -> {
            try {
                subscriber.onNext(PushBean.getPushBean(data));
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation());
    }
}
