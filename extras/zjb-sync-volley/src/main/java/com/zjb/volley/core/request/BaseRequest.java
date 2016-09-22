package com.zjb.volley.core.request;


import com.zjb.volley.core.response.Response;

/**
 * time: 15/6/15
 * description: 封装了请求的基础类
 *
 * @author sunjianfei
 */
public abstract class BaseRequest<T> extends Request<T> {

    protected static final int INITIAL_TIMEOUT = 10000;
    protected static final int MAX_RETRY = 0;
    protected static final float BACKOFF_MULT = 1.0f;


    protected String mFriendlyName;


    public BaseRequest(String url) {
        super(Method.GET, url, null);
        this.setRetryPolicy(new DefaultRetryPolicy(INITIAL_TIMEOUT, MAX_RETRY, BACKOFF_MULT));
    }


    public BaseRequest(int method, String url) {
        super(method, url, null);
        this.setRetryPolicy(new DefaultRetryPolicy(INITIAL_TIMEOUT, MAX_RETRY, BACKOFF_MULT));
    }

    public BaseRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
        this.setRetryPolicy(new DefaultRetryPolicy(INITIAL_TIMEOUT, MAX_RETRY, BACKOFF_MULT));
    }

    public String getFriendlyName() {
        return mFriendlyName;
    }

}
