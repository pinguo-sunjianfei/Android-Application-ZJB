package com.zjb.volley.core;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.zjb.volley.bean.HttpParams;
import com.zjb.volley.utils.MimeTypeHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * time: 15/6/15
 * description: 封装了http的基本请求
 *
 * @author sunjianfei
 */

public class HttpManager {
    public static final String BOUNDARY = "kmdleuhfpsidnl";
    private static final String MP_BOUNDARY = "--" + BOUNDARY;
    private static final String END_MP_BOUNDARY = MP_BOUNDARY + "--";
    private static final int BUFFER_SIZE = 8192;

    private HttpManager() {
    }

    public static String buildGetURL(String originUrl, HttpParams httpParams, String paramsEncoding) {
        Map<String, String> params = httpParams.getTextParams();
        if (params == null || params.isEmpty()) {
            return originUrl;
        }
        StringBuilder encodedParams = new StringBuilder(originUrl);
        if (!originUrl.endsWith("?")) {
            encodedParams.append("?");
        }
        try {
            Iterator uee = params.entrySet().iterator();
            boolean hasNext = uee.hasNext();
            while (hasNext) {
                Map.Entry entry = (Map.Entry) uee.next();
                encodedParams.append(URLEncoder.encode((String) entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode((String) entry.getValue(), paramsEncoding));
                if (hasNext = uee.hasNext()) {
                    encodedParams.append('&');
                }
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, e);
        }
    }

    public static byte[] buildPostParams(HttpParams params) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            buildParams(baos, params);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void buildParams(OutputStream baos, HttpParams params) throws Exception {
        Map<String, String> text = params.getTextParams();
        if (text != null && !text.isEmpty()) {
            Set<String> e = text.keySet();
            Iterator<String> iterator = e.iterator();
            String key;
            String value;
            StringBuilder sb;
            while (iterator.hasNext()) {
                key = iterator.next();
                value = text.get(key);
                if (!TextUtils.isEmpty(key)) {
                    sb = new StringBuilder();
                    sb.setLength(0);
                    sb.append(MP_BOUNDARY).append("\r\n");
                    sb.append("content-disposition: form-data; name=\"").append(key).append("\"\r\n\r\n");
                    sb.append(value).append("\r\n");
                    baos.write(sb.toString().getBytes());
                }
            }
        }
        Map<String, Object> multi = params.getMutiParams();
        if (multi != null && !multi.isEmpty()) {
            Set<String> e = multi.keySet();
            Iterator<String> iterator = e.iterator();
            StringBuilder sb;
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = multi.get(key);
                if (value instanceof Bitmap) {
                    sb = new StringBuilder();
                    sb.append(MP_BOUNDARY).append("\r\n");
                    sb.append("content-disposition: form-data; name=\"").append(key).append("\"; filename=\"file\"\r\n");
                    sb.append("Content-Type: application/octet-stream; charset=utf-8\r\n\r\n");
                    baos.write(sb.toString().getBytes());
                    Bitmap stream = (Bitmap) value;
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    stream.compress(Bitmap.CompressFormat.PNG, 100, stream1);
                    byte[] bytes = stream1.toByteArray();
                    baos.write(bytes);
                    baos.write("\r\n".getBytes());
                } else if (value instanceof File) {
                    File file = (File) value;
                    String mimeType = MimeTypeHelper.getContentType(file);
                    if (TextUtils.isEmpty(mimeType)) {
                        mimeType = "application/octet-stream; charset=utf-8";
                    }
                    sb = new StringBuilder();
                    sb.append(MP_BOUNDARY).append("\r\n");
                    sb.append("content-disposition: form-data; name=\"").append(key).append("\"; filename=\"file\"\r\n");
                    sb.append("Content-Type: " + mimeType);
                    sb.append("\r\n\r\n");
                    baos.write(sb.toString().getBytes());
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        stream1.write(buffer, 0, len);
                    }
                    fis.close();
                    byte[] bytes = stream1.toByteArray();
                    baos.write(bytes);
                    baos.write("\r\n".getBytes());
                } else if (value instanceof ByteArrayOutputStream) {
                    sb = new StringBuilder();
                    sb.append(MP_BOUNDARY).append("\r\n");
                    sb.append("content-disposition: form-data; name=\"").append(key).append("\"; filename=\"file\"\r\n");
                    sb.append("Content-Type: application/octet-stream; charset=utf-8\r\n\r\n");
                    baos.write(sb.toString().getBytes());
                    ByteArrayOutputStream stream2 = (ByteArrayOutputStream) value;
                    baos.write(stream2.toByteArray());
                    baos.write("\r\n".getBytes());
                    stream2.close();
                }
            }
        }
        baos.write(("\r\n" + END_MP_BOUNDARY).getBytes());
    }


}

