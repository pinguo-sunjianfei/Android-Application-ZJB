package com.zjb.volley.core.stack;

import java.io.IOException;
import java.util.Map;

import com.zjb.volley.core.exception.VolleyError;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.response.CustomResponse;


/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public interface HttpStack {
    CustomResponse performRequest(Request<?> request, Map<String, String> params) throws IOException, VolleyError;
}
