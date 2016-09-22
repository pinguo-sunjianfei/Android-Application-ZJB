package com.zjb.volley.bean;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * time: 15/6/15
 * description: 封装了请求参数
 *
 * @author sunjianfei
 */
public class HttpParams implements Serializable {
    public static final String PARAMS_BLANK = "params_blank";

    private static final long serialVersionUID = -6376078803512393464L;

    protected Map<String, String> mTextParams = new HashMap<String, String>();

    protected Map<String, Object> mMultiParams = new HashMap<String, Object>();

    public HttpParams() {
    }

    public boolean isExisted(String key) {
        return !TextUtils.isEmpty(mTextParams.get(key));
    }

    public HttpParams put(String key, String value) {
        try {
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                if (PARAMS_BLANK.equals(value)) {
                    value = "";
                }
                mTextParams.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public HttpParams put(String key, float value) {
        put(key, value + "");
        return this;
    }

    public HttpParams put(String key, long value) {
        put(key, value + "");
        return this;
    }

    public HttpParams put(String key, int value) {
        put(key, value + "");
        return this;
    }

    public HttpParams put(String key, double value) {
        put(key, value + "");
        return this;
    }

    public HttpParams put(String key, boolean value) {
        put(key, value + "");
        return this;
    }

    public HttpParams put(String key, File file) {
        mMultiParams.put(key, file);
        return this;
    }

    public HttpParams put(String key, Bitmap bitmap) {
        mMultiParams.put(key, bitmap);
        return this;
    }

    public HttpParams put(String key, ByteArrayOutputStream bis) {
        mMultiParams.put(key, bis);
        return this;
    }

    public HttpParams put(Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public Map<String, String> getTextParams() {
        return Collections.unmodifiableMap(mTextParams);
    }

    public Map<String, Object> getMutiParams() {
        return Collections.unmodifiableMap(mMultiParams);
    }


    public void clear() {
        this.mTextParams.clear();
        this.mMultiParams.clear();
    }
}
