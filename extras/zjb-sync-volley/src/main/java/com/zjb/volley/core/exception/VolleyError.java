package com.zjb.volley.core.exception;


import com.zjb.volley.core.response.NetworkResponse;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public class VolleyError extends Exception {
    public final NetworkResponse networkResponse;
    private long networkTimeMs;

    public VolleyError() {
        this.networkResponse = null;
    }

    public VolleyError(NetworkResponse response) {
        this.networkResponse = response;
    }

    public VolleyError(String exceptionMessage) {
        super(exceptionMessage);
        this.networkResponse = null;
    }

    public VolleyError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        this.networkResponse = null;
    }

    public VolleyError(Throwable cause) {
        super(cause);
        this.networkResponse = null;
    }

    void setNetworkTimeMs(long networkTimeMs) {
        this.networkTimeMs = networkTimeMs;
    }

    public long getNetworkTimeMs() {
        return this.networkTimeMs;
    }
}
