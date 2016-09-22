package com.zjb.volley.core.request;

import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.zjb.volley.Volley;
import com.zjb.volley.bean.HttpParams;
import com.zjb.volley.bean.parser.BaseParser;
import com.zjb.volley.core.HttpHeaderParser;
import com.zjb.volley.core.HttpManager;
import com.zjb.volley.core.exception.AuthFailureError;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.core.response.NetworkResponse;
import com.zjb.volley.log.HttpLogger;
import com.zjb.volley.utils.GsonUtil;
import com.zjb.volley.utils.RequestHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;


/**
 * time: 15/6/15
 * description: 网络请求返回的数据被封装到HttpResponse当中，这个类将HttpResponse当中的data封装到成一个T
 *
 * @author sunjianfei
 */
public class HttpGsonRequest<T> extends BaseRequest<T> {

    protected HttpParams mHttpParams;

    protected String mNodeName;

    protected BaseParser<T> mParser;


    public HttpGsonRequest(String url) {
        super(url);
    }

    public HttpGsonRequest(String url, HttpParams params) {
        super(RequestHelper.buildUrl(url, params));
    }

    public Class<?> getClazz() {
        return null;
    }

    public HttpGsonRequest(int method, String url, HttpParams params) {
        super(method, url);
        this.mHttpParams = params;
        if (Method.GET == method) {
            this.mUrl = HttpManager.buildGetURL(url, params, getParamsEncoding());
        }
    }

    public HttpGsonRequest(int method, BaseParser<T> parser, String url, HttpParams params) {
        super(method, url);
        this.mParser = parser;
        this.mHttpParams = params;
        if (Method.GET == method) {
            this.mUrl = HttpManager.buildGetURL(url, params, getParamsEncoding());
        }
    }

    public HttpGsonRequest(int method, BaseParser<T> parser, String url, String mNodeName, HttpParams params) {
        super(method, url);
        this.mNodeName = mNodeName;
        this.mParser = parser;
        this.mHttpParams = params;
        if (Method.GET == method) {
            this.mUrl = HttpManager.buildGetURL(url, params, getParamsEncoding());
        }
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mHttpParams != null) {
            return mHttpParams.getTextParams();
        }
        return super.getParams();
    }


    @Override
    public HttpResponse<T> parseNetworkResponse(NetworkResponse response) {
        HttpResponse<T> mResponse = new HttpResponse<T>();
        if (response == null) {
            return mResponse;
        }
        String data;
        try {
            data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            data = new String(response.data);
        }
        try {
            JSONObject result = new JSONObject(data);
            if (!TextUtils.isEmpty(mNodeName)) {
                if (result.has(mNodeName)) {
                    result = result.getJSONObject(mNodeName);
                }
            }
            mResponse.status = response.statusCode;
            if (result.has("status")) {
                mResponse.status = result.optInt("status", HttpResponse.CODE_FAILED);
            }
            if (result.has("msg")) {
                mResponse.message = result.optString("msg", "");
            } else if (result.has("message")) {
                mResponse.message = result.optString("message", "");
            }

            if (result.has("serverTime")) {
                mResponse.serverTime = result.optLong("serverTime");
            }
            //如果需要封装数据的nodeName不是"data"可以在此基础上扩展
            if (result.has("data")) {
                data = result.optString("data");
            }
            Class<?> clazz = getClazz();
            if (!TextUtils.isEmpty(data)) {
                //1.首先解析占位符
                if (null != Volley.sPlaceholderParser) {
                    data = Volley.sPlaceholderParser.parsePlaceholderJson(data);
                }
                //2.解析器首先解析
                if (mParser != null) {
                    mResponse.data = mParser.parser(data);
                } else if (clazz != null) {
                    if (String.class == clazz) {
                        mResponse.data = (T) data;
                    } else {
                        mResponse.data = GsonUtil.fromJson(data, (Class<T>) clazz);
                    }
                } else {
                    Type type = ((ParameterizedType) ((Object) this).getClass()
                            .getGenericSuperclass()).getActualTypeArguments()[0];
                    mResponse.data = GsonUtil.fromJson(data, type);
                }
            }
        } catch (JsonSyntaxException e) {
            HttpLogger.e("gson解析错误：" + e.getMessage());
        } catch (Exception e) {
        }
        return mResponse;
    }

    private void printJson(JSONObject obj) {
        try {
            Object o, object;
            JSONArray array;
            Iterator it = obj.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                o = obj.get(key);
                HttpLogger.e(key + ":" + o.toString() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
