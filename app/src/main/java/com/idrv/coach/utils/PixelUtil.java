package com.idrv.coach.utils;

import android.content.Context;
import android.content.res.Resources;

import com.idrv.coach.utils.helper.ResHelper;

import static com.idrv.coach.ZjbApplication.gContext;

/**
 * time: 15/7/17
 * description:像素转换器
 *
 * @author sunjianfei
 */
public class PixelUtil {

    /**
     * dp转 px.
     *
     * @param value the value
     * @return the int
     */
    public static float dp2px(float value) {
        final float scale = gContext.getResources().getDisplayMetrics().densityDpi;
        return value * scale / 160 + 0.5f;
    }

    /**
     * dp转 px.
     *
     * @param value   the value
     * @param context the context
     * @return the int
     */
    public static float dp2px(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return value * scale / 160 + 0.5f;
    }

    /**
     * px转dp.
     *
     * @param value the value
     * @return the int
     */
    public static float px2dp(float value) {
        final float scale = gContext.getResources().getDisplayMetrics().densityDpi;
        return value * 160 / scale + 0.5f;
    }

    /**
     * px转dp.
     *
     * @param value   the value
     * @param context the context
     * @return the int
     */
    public static float px2dp(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return value * 160 / scale + 0.5f;
    }

    /**
     * sp转px.
     *
     * @param value the value
     * @return the int
     */
    public static int sp2px(float value) {
        Resources r;
        if (gContext == null) {
            r = Resources.getSystem();
        } else {
            r = gContext.getResources();
        }
        float spvalue = value * r.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }

    /**
     * sp转px.
     *
     * @param value   the value
     * @param context the context
     * @return the int
     */
    public static int sp2px(float value, Context context) {
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        float spvalue = value * r.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }

    /**
     * px转sp.
     *
     * @param value the value
     * @return the int
     */
    public static int px2sp(float value) {
        final float scale = gContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) ((value - 0.5) / scale);
    }

    /**
     * px转sp.
     *
     * @param value   the value
     * @param context the context
     * @return the int
     */
    public static int px2sp(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }

    public static int getWidthDp(Context context) {
        int screenWidth = ResHelper.getScreenWidth();
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (screenWidth * 1.0f / scale);
    }

}
