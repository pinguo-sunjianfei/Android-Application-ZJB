package com.zjb.volley.core.request;

import android.net.Uri;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.zjb.volley.core.cache.Cache;
import com.zjb.volley.core.exception.AuthFailureError;
import com.zjb.volley.core.exception.VolleyError;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.core.response.NetworkResponse;
import com.zjb.volley.core.response.Response;


/**
 * Base class for all network requests.
 *
 * @param <T> The type of parsed response this request expects.
 */
public abstract class Request<T> implements Comparable<Request<T>> {
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    private final int mMethod;
    protected String mUrl;
    protected String mRedirectUrl;
    private String mIdentifier;
    private final int mDefaultTrafficStatsTag;
    private final Response.ErrorListener mErrorListener;
    private Integer mSequence;
    private boolean mShouldCache;
    private boolean mCanceled;
    private boolean mResponseDelivered;
    private RetryPolicy mRetryPolicy;
    private Cache.Entry mCacheEntry;
    private Object mTag;
    private static long sCounter;


    public Request(int method, String url, Response.ErrorListener listener) {
        this.mShouldCache = true;
        this.mCanceled = false;
        this.mResponseDelivered = false;
        this.mCacheEntry = null;
        this.mMethod = method;
        this.mUrl = url;
        this.mIdentifier = createIdentifier(method, url);
        this.mErrorListener = listener;
        this.setRetryPolicy(new DefaultRetryPolicy());
        this.mDefaultTrafficStatsTag = findDefaultTrafficStatsTag(url);
    }


    public int getMethod() {
        return this.mMethod;
    }

    public Request<?> setTag(Object tag) {
        this.mTag = tag;
        return this;
    }

    public Object getTag() {
        return this.mTag;
    }

    public Response.ErrorListener getErrorListener() {
        return this.mErrorListener;
    }

    public int getTrafficStatsTag() {
        return this.mDefaultTrafficStatsTag;
    }

    private static int findDefaultTrafficStatsTag(String url) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                String host = uri.getHost();
                if (host != null) {
                    return host.hashCode();
                }
            }
        }

        return 0;
    }

    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        this.mRetryPolicy = retryPolicy;
        return this;
    }


    public final Request<?> setSequence(int sequence) {
        this.mSequence = sequence;
        return this;
    }

    public final int getSequence() {
        if (this.mSequence == null) {
            throw new IllegalStateException("getSequence called before setSequence");
        } else {
            return this.mSequence;
        }
    }


    public String getUrl() {
        return this.mRedirectUrl != null ? this.mRedirectUrl : this.mUrl;
    }

    public String getOriginUrl() {
        return this.mUrl;
    }

    public String getIdentifier() {
        return this.mIdentifier;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.mRedirectUrl = redirectUrl;
    }

    public String getCacheKey() {
        return this.mMethod + ":" + this.mUrl;
    }

    public Request<?> setCacheEntry(Cache.Entry entry) {
        this.mCacheEntry = entry;
        return this;
    }

    public Cache.Entry getCacheEntry() {
        return this.mCacheEntry;
    }

    public void cancel() {
        this.mCanceled = true;
    }

    public boolean isCanceled() {
        return this.mCanceled;
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        return Collections.emptyMap();
    }


    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + this.getParamsEncoding();
    }

    public byte[] getBody() throws AuthFailureError {
        return this.encodeParameters(this.getParams(), this.getParamsEncoding());
    }

    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder encodedParams = new StringBuilder();
        try {
            Iterator uee = params.entrySet().iterator();
            boolean hasNext = uee.hasNext();
            while (hasNext) {
                Map.Entry entry = (Map.Entry) uee.next();
                encodedParams.append(URLEncoder.encode((String) entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode((String) entry.getValue(), paramsEncoding));
                if (hasNext = uee.hasNext()) {
                    encodedParams.append('&');
                }
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, e);
        }
    }

    public final Request<?> setShouldCache(boolean shouldCache) {
        this.mShouldCache = shouldCache;
        return this;
    }

    public final boolean shouldCache() {
        return this.mShouldCache;
    }

    public Request.Priority getPriority() {
        return Request.Priority.NORMAL;
    }

    public final int getTimeoutMs() {
        return this.mRetryPolicy.getCurrentTimeout();
    }

    public RetryPolicy getRetryPolicy() {
        return this.mRetryPolicy;
    }

    public void markDelivered() {
        this.mResponseDelivered = true;
    }

    public boolean hasHadResponseDelivered() {
        return this.mResponseDelivered;
    }

    public abstract HttpResponse<T> parseNetworkResponse(NetworkResponse response);

    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return volleyError;
    }


    public void deliverError(VolleyError error) {
        if (this.mErrorListener != null) {
            this.mErrorListener.onErrorResponse(error);
        }

    }

    public int compareTo(Request<T> other) {
        Request.Priority left = this.getPriority();
        Request.Priority right = other.getPriority();
        return left == right ? this.mSequence.intValue() - other.mSequence.intValue() : right.ordinal() - left.ordinal();
    }

    public String toString() {
        String trafficStatsTag = "0x" + Integer.toHexString(this.getTrafficStatsTag());
        return (this.mCanceled ? "[X] " : "[ ] ") + this.getUrl() + " " + trafficStatsTag + " " + this.getPriority() + " " + this.mSequence;
    }

    private static String createIdentifier(int method, String url) {
        return sha1Hash("Request:" + method + ":" + url + ":" + System.currentTimeMillis() + ":" + sCounter++);
    }

    public static enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request<?> request = (Request<?>) o;
        if (mMethod != request.mMethod) return false;
        if ((mUrl != null ? !mUrl.equals(request.mUrl) : request.mUrl != null)) return false;
        try {

            return Arrays.equals(getBody(), request.getBody());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
            return false;
        }
    }

    /**
     * 对Request生成一个唯一的id
     *
     * @return
     * @throws AuthFailureError
     */
    public String genRequestId() {
        StringBuilder builder = new StringBuilder(mUrl)
                .append(mMethod);
        try {
            builder.append(convertToHex(getBody()));
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        return builder.toString();

    }

    private final static char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    private static String convertToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_CHARS[v >>> 4];
            hexChars[j * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String sha1Hash(String text) {
        String hash = null;
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            final byte[] bytes = text.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            hash = convertToHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }
}
