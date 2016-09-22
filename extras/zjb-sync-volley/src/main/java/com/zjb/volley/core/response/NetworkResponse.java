package com.zjb.volley.core.response;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public class NetworkResponse implements Serializable {
    private static final long serialVersionUID = -20150728102000L;
    public int statusCode;
    public byte[] data;
    public Map<String, String> headers;
    public boolean notModified;
    public long networkTimeMs;

    public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModified, long networkTimeMs) {
        this.statusCode = statusCode;
        this.data = data;
        this.headers = headers;
        this.notModified = notModified;
        this.networkTimeMs = networkTimeMs;
    }

    public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModified) {
        this(statusCode, data, headers, notModified, 0L);
    }

    public NetworkResponse(byte[] data) {
        this(200, data, Collections.<String, String>emptyMap(), false, 0L);
    }

    public NetworkResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public NetworkResponse(byte[] data, Map<String, String> headers) {
        this(200, data, headers, false, 0L);
    }
}
