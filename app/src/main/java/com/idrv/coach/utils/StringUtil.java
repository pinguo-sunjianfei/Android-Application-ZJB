package com.idrv.coach.utils;

import android.text.TextUtils;

/**
 * time: 15/6/7
 * description: 对String类型的基本操作
 *
 * @author sunjianfei
 */
public class StringUtil {

    /**
     * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
     *
     * @param c 需要判断的字符
     * @return 返回true, Ascill字符
     */
    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0;
    }

    /**
     * 取得字符串的实际长度（考虑了汉字的情况）
     *
     * @param SrcStr 源字符串
     * @return 字符串的实际长度
     */
    public static int getStringLen(String SrcStr) {
        int return_value = 0;
        if (SrcStr != null) {
            char[] theChars = SrcStr.toCharArray();
            for (int i = 0; i < theChars.length; i++) {
                return_value += (theChars[i] <= 255) ? 1 : 2;
            }
        }
        return return_value;
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @param s 需要得到长度的字符串
     * @return i得到的字符串长度
     */
    public static int length(String s) {
        if (s == null) {
            return 0;
        }
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            //如果为汉，日，韩，则多加一位
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    /**
     * 全是空格
     * tangsong
     *
     * @param text
     * @return
     */
    public static boolean isSpaces(String text) {
        if (text == null) {
            return true;
        } else {
            return text.trim().length() == 0;
        }
    }

    /**
     * 将1w以上的数字转为k
     *
     * @param number
     * @return
     */
    public static String numToK(int number) {
        if (number < 10000) {
            return String.valueOf(number);
        } else {
            float value = number * 1.0f / 10000;
            StringBuffer sb = new StringBuffer(String.valueOf(value));
            sb.append("K");
            return sb.toString();
        }
    }

    /**
     * 判断字符串是否为Html代码
     *
     * @param str
     * @return
     */
    public static boolean isHtml(String str) {
        if (!TextUtils.isEmpty(str)) {
            return str.contains("<html");
        }
        return false;
    }

}
