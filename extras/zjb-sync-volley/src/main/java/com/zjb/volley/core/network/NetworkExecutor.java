package com.zjb.volley.core.network;

import android.os.SystemClock;

import com.zjb.volley.core.cache.Cache;
import com.zjb.volley.core.exception.AuthFailureError;
import com.zjb.volley.core.exception.NetworkError;
import com.zjb.volley.core.exception.NoConnectionError;
import com.zjb.volley.core.exception.TimeoutError;
import com.zjb.volley.core.exception.VolleyError;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.response.CustomResponse;
import com.zjb.volley.core.response.NetworkResponse;
import com.zjb.volley.core.stack.HttpStack;
import com.zjb.volley.log.HttpLogger;

import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * time: 15/7/10
 * description: 网络请求类型
 *
 * @author sunjianfei
 */
public class NetworkExecutor extends BaseNetwork {

    public NetworkExecutor(HttpStack httpStack) {
        super(httpStack);
    }

    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();
        while (true) {
            CustomResponse customResponse = null;
            byte[] responseContent = null;
            Map responseHeaders = Collections.emptyMap();
            try {
                HashMap<String, String> map = new HashMap<String, String>();
                this.addCacheHeaders(map, request.getCacheEntry());
                this.addGzipHeader(map);
                if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
                    map.putAll(request.getHeaders());
                }
                customResponse = this.mHttpStack.performRequest(request, map);
                StatusLine statusLine = customResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                responseHeaders = convertHeaders(customResponse.getAllHeaders());
                if (statusCode != 304) {
                    responseContent = customResponse.getData();
                    if (responseContent == null) {
                        responseContent = new byte[0];
                    }
                    long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
                    this.logSlowRequests(requestLifetime, request, responseContent, statusLine);
                    if (statusCode >= 200 && statusCode <= 299) {
                        HttpLogger.printResponse(statusCode, request.getOriginUrl(), new String(responseContent, "UTF-8"));
                        return new NetworkResponse(statusCode, responseContent, responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
                    }
                    throw new IOException(new String(responseContent));
                }
                Cache.Entry requestLifetime = request.getCacheEntry();
                if (requestLifetime == null) {
                    HttpLogger.printResponse(statusCode, request.getOriginUrl(), null);
                    return new NetworkResponse(304, null, responseHeaders, true, SystemClock.elapsedRealtime() - requestStart);
                }
                requestLifetime.responseHeaders.putAll(responseHeaders);
                HttpLogger.printResponse(statusCode, request.getOriginUrl(), new String(requestLifetime.data, "UTF-8"));
                return new NetworkResponse(304, requestLifetime.data, requestLifetime.responseHeaders, true, SystemClock.elapsedRealtime() - requestStart);
            } catch (SocketTimeoutException e) {
                HttpLogger.e(e);
                attemptRetryOnException(request, new TimeoutError());
            } catch (ConnectTimeoutException e) {
                HttpLogger.e(e);
                attemptRetryOnException(request, new TimeoutError());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Bad URL " + request.getUrl(), e);
            } catch (IOException e) {
                NetworkResponse networkResponse = null;
                if (customResponse == null) {
                    throw new NoConnectionError(e);
                }
                int statusCode = customResponse.getStatusLine().getStatusCode();
                HttpLogger.e(String.format("Unexpected response code %d for %s", new Object[]{statusCode, request.getUrl()}));
                HttpLogger.e(e);
                if (responseContent == null || responseContent.length <= 0) {
                    throw new NetworkError(networkResponse);
                }
                networkResponse = new NetworkResponse(statusCode,
                        responseContent, responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
                if (statusCode != 401 && statusCode != 403) {
                    return networkResponse;
                }
                attemptRetryOnException(request, new AuthFailureError(networkResponse));
            }
        }
    }


}
