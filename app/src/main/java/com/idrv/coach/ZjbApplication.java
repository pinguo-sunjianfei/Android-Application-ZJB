package com.idrv.coach;

import android.app.Activity;
import android.app.Application;

import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.volley.Volley;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunjianfei on 16-2-25.
 */
public class ZjbApplication extends Application {
    private static final List<Activity> ACTIVITIES = new ArrayList<Activity>();

    public static ZjbApplication gContext;

    public static List<Activity> getActivities() {
        return ACTIVITIES;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gContext = this;

        //初始化
        AppInitManager.getInstance().initializeApp(this);
    }

    public static void addActivity(Activity activity) {
        if (!ACTIVITIES.contains(activity)) {
            ACTIVITIES.add(activity);
        }
    }

    public static void removeActivity(Activity activity) {
        if (ACTIVITIES.contains(activity)) {
            ACTIVITIES.remove(activity);
        }
    }

    public static Activity getTop2Activity() {
        if (ACTIVITIES.size() >= 2) {
            return ACTIVITIES.get(ACTIVITIES.size() - 2);
        }
        return null;
    }

    /**
     * finish掉所有非栈顶的activity
     */
    public static void finishNoTopActivity() {
        if (ValidateUtil.isValidate(ACTIVITIES)) {
            while (ACTIVITIES.size() > 1) {
                Activity activity = ACTIVITIES.get(0);
                ACTIVITIES.remove(activity);
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
        }

    }

    /**
     * finish掉所有的activity
     */
    public static void finishAllActivity() {
        while (ValidateUtil.isValidate(ACTIVITIES)) {
            Activity activity = ACTIVITIES.get(0);
            ACTIVITIES.remove(activity);
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static Activity getCurrentActivity() {
        if (ValidateUtil.isValidate(ACTIVITIES)) {
            return ACTIVITIES.get(ACTIVITIES.size() - 1);
        }
        return null;
    }

    public static int getStackActivitiesNum() {
        return ACTIVITIES.size();
    }

    public static void exit() {
        finishAllActivity();
        Volley.release();
        ZjbImageLoader.destroy();
    }
}
