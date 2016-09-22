package com.zjb.volley.utils;

import android.os.Environment;

import java.util.Map;

/**
 * time: 15/7/10
 * description:
 *
 * @author sunjianfei
 */
public class Util {

    public static String parseCharset(Map<String, String> headers) {
        String contentType = headers.get("Content-Type");
        if (contentType != null) {
            String[] params = contentType.split(";");

            for (int i = 1; i < params.length; ++i) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2 && pair[0].equals("charset")) {
                    return pair[1];
                }
            }
        }

        return "UTF-8";
    }

    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
