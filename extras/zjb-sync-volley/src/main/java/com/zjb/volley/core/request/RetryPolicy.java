package com.zjb.volley.core.request;


import com.zjb.volley.core.exception.VolleyError;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public interface RetryPolicy {
    int getCurrentTimeout();

    int getCurrentRetryCount();

    void retry(VolleyError var1) throws VolleyError;
}
