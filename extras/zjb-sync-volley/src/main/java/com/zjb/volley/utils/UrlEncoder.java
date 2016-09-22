package com.zjb.volley.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * time: 15/7/10
 * description: 对请求参数进行url编码
 *
 * @author sunjianfei
 */
public class UrlEncoder {

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
