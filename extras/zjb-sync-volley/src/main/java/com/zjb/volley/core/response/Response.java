package com.zjb.volley.core.response;


import com.zjb.volley.core.cache.Cache;
import com.zjb.volley.core.exception.VolleyError;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public class Response<T> {
    public final T result;
    public final Cache.Entry cacheEntry;
    public final VolleyError error;
    public boolean intermediate = false;

    public static <T> Response<T> success(T result, Cache.Entry cacheEntry) {
        return new Response(result, cacheEntry);
    }

    public static <T> Response<T> error(VolleyError error) {
        return new Response(error);
    }

    public boolean isSuccess() {
        return this.error == null;
    }

    private Response(T result, Cache.Entry cacheEntry) {
        this.result = result;
        this.cacheEntry = cacheEntry;
        this.error = null;
    }

    private Response(VolleyError error) {
        this.result = null;
        this.cacheEntry = null;
        this.error = error;
    }

    public interface ErrorListener {
        void onErrorResponse(VolleyError var1);
    }

    public interface Listener<T> {
        void onResponse(T var1);
    }
}
