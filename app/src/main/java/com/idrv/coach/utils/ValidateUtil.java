package com.idrv.coach.utils;

import android.text.TextUtils;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * time: 15/6/7
 * description: 数据有效性的判断类
 *
 * @author sunjianfei
 */
public class ValidateUtil {

    public static boolean isValidate(Collection<?> collection) {
        return null != collection && !collection.isEmpty();
    }

    public static boolean isValidate(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    public static boolean isValidate(String content) {
        return !TextUtils.isEmpty(content);
    }

    /**
     * 检查是否是正确的email格式
     *
     * @param email email
     * @return
     */
    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        String reg = "^[0-9a-z_-][_.0-9a-z-]{0,31}@([0-9a-z][0-9a-z-]{0,30}\\.){1,4}[a-z]{2,4}$";
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 检查是否是正确手机号
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        String reg = "^[0-9]{11}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

}
