package com.idrv.coach.utils;

import android.support.annotation.StringRes;

import com.idrv.coach.ZjbApplication;
import com.idrv.coach.utils.helper.ResHelper;
import com.tendcloud.tenddata.TCAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * time: 2016/4/5
 * description:
 * Umeng统计的工具
 *
 * @author bigflower
 */
public class StatisticsUtil {

    public static void onEvent(@StringRes int resId) {
        onEvent(ResHelper.getString(resId));
    }

    /**
     * @param eventId
     */
    public static void onEvent(String eventId) {
        if (!DebugUtil.isDebug()) {
            TCAgent.onEvent(ZjbApplication.gContext, eventId);
        }
    }


    public static void onEvent(@StringRes int resId, String uid, String nickname) {
        if (!DebugUtil.isDebug()) {
            Map<String, String> map = new HashMap<>();
            map.put("uid", uid);
            map.put("nickname", nickname);
            TCAgent.onEvent(ZjbApplication.gContext, ResHelper.getString(resId), "", map);
        }
    }

}
