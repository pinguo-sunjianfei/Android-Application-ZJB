package com.idrv.coach.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.idrv.coach.data.constants.SPConstant;

import static com.idrv.coach.ZjbApplication.gContext;

/**
 * time: 15/7/17
 * description: sharedPreference管理类
 *
 * @author sunjianfei
 */
public class PreferenceUtil {

    public static void putString(String key, String value) {
        if (!TextUtils.isEmpty(value) & !TextUtils.isEmpty(key)) {
            String encrypt = com.idrv.coach.utils.EncryptUtil.base64encode(value);
            SharedPreferences.Editor editor = getEditor();
            editor.putString(key, encrypt);
            editor.commit();
        }
    }

    public static String getString(String key) {
        return getString(key, null);
    }


    /**
     * 为了兼容1.0.3的版本，所以需要加这个方法，仅限于首次得到用户登陆信息的时候调用
     *
     * @param key    得到字符串的key值
     * @param spName SharePreference的名称
     * @return
     */
    public static String getPreferenceString(String key, String spName) {
        SharedPreferences preferences = gContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
        if (null != preferences) {
            return preferences.getString(key, null);
        }
        return null;
    }

    /**
     * 为了兼容1.0.3的版本，所以需要加这个方法，仅限于首次得到用户登陆信息的时候调用
     *
     * @param key    需要删除的key值
     * @param spName SharePreference的名称
     * @return
     */
    public static void removeSp(String key, String spName) {
        SharedPreferences preferences = gContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
        if (null != preferences) {
            SharedPreferences.Editor editor = preferences.edit();
            if (null != editor) {
                editor.remove(key);
                editor.commit();
            }

        }
    }

    /**
     * 为了兼容1.0.3的版本，所以需要加这个方法，仅限于首次是否是第一次登陆的时候调用
     *
     * @param key    得到boolean值的key值
     * @param spName SharePreference的名称
     * @return
     */
    public static boolean getPreferenceBoolean(String key, String spName, boolean defaultValue) {
        SharedPreferences preferences = gContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
        if (null != preferences) {
            return preferences.getBoolean(key, defaultValue);
        }
        return false;
    }

    /**
     * 得到配置数据，增加了解密的过程
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(String key, String defaultValue) {
        String origin = getSharedPreference().getString(key, null);
        if (!TextUtils.isEmpty(origin)) {
            return com.idrv.coach.utils.EncryptUtil.base64decode(origin);
        }
        return defaultValue;
    }

    public static void putInt(String key, int value) {
        putString(key, value + "");
    }

    public static void putLong(String key, long value) {
        putString(key, value + "");
    }

    public static void putBoolean(String key, boolean value) {
        putString(key, value + "");
    }

    public static void remove(String... keys) {
        SharedPreferences.Editor editor = getEditor();
        for (String key : keys) {
            if (!TextUtils.isEmpty(key)) {
                editor.remove(key);
            }
        }
        editor.commit();
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static long getLong(String key) {
        String origin = getString(key);
        if (!TextUtils.isEmpty(origin) && TextUtils.isDigitsOnly(origin)) {
            return Long.valueOf(origin);
        }
        return 0L;
    }

    public static int getInt(String key, int defaultValue) {
        String origin = getString(key);
        if (!TextUtils.isEmpty(origin) && TextUtils.isDigitsOnly(origin)) {
            return Integer.valueOf(origin);
        }
        return defaultValue;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String origin = getString(key);
        if (!TextUtils.isEmpty(origin)) {
            return Boolean.valueOf(origin);
        }
        return defaultValue;
    }

    public static boolean getBoolean(String key) {
        String origin = getString(key);
        if (!TextUtils.isEmpty(origin)) {
            return Boolean.valueOf(origin);
        }
        return false;
    }

    private static SharedPreferences getSharedPreference() {
        return gContext.getSharedPreferences(SPConstant.SP_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor() {
        SharedPreferences sp = gContext.getSharedPreferences(SPConstant.SP_NAME, Context.MODE_PRIVATE);
        return sp.edit();
    }
}
