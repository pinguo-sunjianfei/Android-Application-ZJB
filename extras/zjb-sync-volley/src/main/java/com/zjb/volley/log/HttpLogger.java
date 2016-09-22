package com.zjb.volley.log;


import android.util.Log;

import java.util.Iterator;
import java.util.Map;

import com.zjb.volley.Volley;
import com.zjb.volley.core.request.Request;


/**
 * time: 15/7/10
 * description: http请求的日志管理类
 *
 * @author sunjianfei
 */
public class HttpLogger {
    public static final String TAG = "Request";

    /**
     * 请求发生之前，打印请求的相关信息
     *
     * @param request 请求
     */
    public static void printParams(Request<?> request) {
        if (Volley.sDebug) {
            StringBuilder builder = new StringBuilder();
            builder.append("\nHttp url : ").append(request.getUrl());
            String method;
            switch (request.getMethod()) {
                case Request.Method.GET:
                    method = "GET";
                    break;
                case Request.Method.POST:
                    method = "POST";
                    break;
                case Request.Method.PUT:
                    method = "PUT";
                    break;
                case Request.Method.DELETE:
                    method = "DELETE";
                    break;
                case Request.Method.HEAD:
                    method = "HEAD";
                    break;
                case Request.Method.OPTIONS:
                    method = "OPTIONS";
                    break;
                case Request.Method.TRACE:
                    method = "TRACE";
                    break;
                case Request.Method.PATCH:
                    method = "PATCH";
                    break;
                default:
                    method = "GET";
            }

            builder.append("\nHttp method : ").append(method);
            try {
                Map map = request.getHeaders();
                if (null != map && !map.isEmpty()) {
                    StringBuilder headerBuilder = new StringBuilder();
                    headerBuilder.append("\nHttp headers: ");
                    Iterator it = map.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        headerBuilder.append("[");
                        headerBuilder.append((String) entry.getKey());
                        headerBuilder.append(" = ");
                        headerBuilder.append((String) entry.getValue());
                        headerBuilder.append("] ");
                    }

                    builder.append(headerBuilder.toString());
                }
                byte[] params = request.getBody();
                if (params != null) {
                    builder.append("\nHttp Params: ");
                    builder.append(new String(params));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            e(builder.toString());
        }
    }

    /**
     * 数据返回，尚未分发时的数据信息
     *
     * @param statusCode 状态码
     * @param url        请求连接
     * @param content    相应数据
     */
    public static void printResponse(int statusCode, String url, String content) {
        if (Volley.sDebug) {
            StringBuilder builder = new StringBuilder();
            builder.append("\nstatus:")
                    .append(statusCode)
                    .append("\nurl:")
                    .append(url)
                    .append("\ndata:")
                    .append(content);
            e(builder.toString());
        }
    }


    public static void e(String msg) {
        if (Volley.sDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (Volley.sDebug) {
            Log.e(tag, msg);
        }
    }

    public static void e(Exception e) {
        if (Volley.sDebug) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public static void e(Throwable tr) {
        if (Volley.sDebug) {
            Log.e(TAG, Log.getStackTraceString(tr));
        }
    }

}
