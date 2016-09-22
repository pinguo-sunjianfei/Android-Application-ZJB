package com.zjb.volley.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.zjb.volley.bean.HttpParams;


/**
 * time: 15/6/6
 * description: 请求的工具类
 *
 * @author sunjianfei
 */
public class RequestHelper {

    public static String buildUrl(String url, HttpParams params) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        String urlStr = url;
        if (!urlStr.contains("?")) {
            urlStr += '?';
        }
        urlStr += encode(params.getTextParams(), "UTF-8");
        return urlStr;

    }

    public static String encode(Map<String, String> params, String encoding) {
        StringBuilder encodedParams = new StringBuilder();

        try {
            Set uee = params.entrySet();
            int size = uee.size();
            int index = 0;
            Iterator iterator = uee.iterator();

            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                encodedParams.append(URLEncoder.encode((String) entry.getKey(), encoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode((String) entry.getValue(), encoding));
                ++index;
                if (index < size) {
                    encodedParams.append('&');
                }
            }

            return encodedParams.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + encoding, e);
        }
    }
}
