package com.zjb.volley.utils;

import android.support.annotation.DimenRes;
import android.support.annotation.StringRes;

import static com.zjb.volley.Volley.gContext;

/**
 * time: 2015/10/16 0016
 * description:
 *
 * @author sunjianfei
 */
public class ResUtil {
    public static String getString(@StringRes int resId) {
        return gContext.getResources().getString(resId);
    }

    public static float getDimen(@DimenRes int dimenId) {
        return gContext.getResources().getDimension(dimenId);
    }

    public static int getScreenWidth() {
        return gContext.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return gContext.getResources().getDisplayMetrics().heightPixels;
    }
}
