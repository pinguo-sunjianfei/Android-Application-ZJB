package com.zjb.volley.core.stack;


import android.text.TextUtils;

import com.facebook.stetho.urlconnection.ByteArrayRequestEntity;
import com.facebook.stetho.urlconnection.SimpleRequestEntity;
import com.facebook.stetho.urlconnection.StethoURLConnectionManager;
import com.zjb.volley.Volley;
import com.zjb.volley.core.PoolingByteArrayOutputStream;
import com.zjb.volley.core.exception.AuthFailureError;
import com.zjb.volley.core.exception.ServerError;
import com.zjb.volley.core.exception.VolleyError;
import com.zjb.volley.core.pool.ByteArrayPool;
import com.zjb.volley.core.request.BaseRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.response.CustomResponse;
import com.zjb.volley.core.ssl.SSLContextFactory;
import com.zjb.volley.log.HttpLogger;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;


/**
 * time: 15/7/10
 * description: HttpUrlConnection方式请求数据
 *
 * @author sunjianfei
 */
public class HttpUrlStack implements HttpStack {
    private StethoURLConnectionManager stethoManager;
    protected static int DEFAULT_POOL_SIZE = 4096;
    protected ByteArrayPool mPool;

    public HttpUrlStack() {
        this(new ByteArrayPool(DEFAULT_POOL_SIZE));
    }

    public HttpUrlStack(ByteArrayPool pool) {
        this.mPool = pool;
    }

    public CustomResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, VolleyError {
        //1.创建StethoManager,用于stetho调试
        if (Volley.sDebug) {
            stethoManager = new StethoURLConnectionManager(((BaseRequest) request).getFriendlyName());
        }
        //2.打印请求的头部字段和参数信息
        HttpLogger.printParams(request);
        //3.创建URL并处理https
        URL parsedUrl = new URL(request.getUrl());
        if ("https".equals(parsedUrl.getProtocol())) {
            String host = parsedUrl.getHost();
            boolean auth = true;
            if (!TextUtils.isEmpty(host) && !host.endsWith(Volley.sValidateHost)) {
                HttpLogger.e("Request", "https not oauth!");
                auth = false;
            }
            handleHttps(auth);
        }
        //4.得到头字段集合
        HashMap<String, String> map = new HashMap<String, String>();
        map.putAll(request.getHeaders());
        map.putAll(additionalHeaders);
        //5.连接网络
        HttpURLConnection mConnection = this.openConnection(parsedUrl, request);
        try {
            //6.stetho保存访问前的状态
            if (Volley.sDebug) {
                SimpleRequestEntity requestEntity = null;
                if (request.getBody() != null) {
                    requestEntity = new ByteArrayRequestEntity(request.getBody());
                }
                stethoManager.preConnect(mConnection, requestEntity);
            }
            //7.将头字段的信息加入到请求信息当中
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                mConnection.addRequestProperty(key, map.get(key));
            }
            //8.设置请求方式
            setConnectionParametersForRequest(mConnection, request);
            ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
            int responseCode = mConnection.getResponseCode();
            if (responseCode == -1) {
                IOException e = new IOException("Could not retrieve response code from HttpUrlConnection.");
                if (Volley.sDebug) {
                    stethoManager.httpExchangeFailed(e);
                }
                throw e;
            } else {
                BasicStatusLine responseStatus = new BasicStatusLine(protocolVersion, mConnection.getResponseCode(), mConnection.getResponseMessage());
                CustomResponse response = new CustomResponse(responseStatus);
                Iterator it = mConnection.getHeaderFields().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry header = (Map.Entry) it.next();
                    if (header.getKey() != null) {
                        BasicHeader h = new BasicHeader((String) header.getKey(),
                                (String) ((List) header.getValue()).get(0));
                        response.addHeader(h);
                    }
                }
                entityFromConnection(mConnection, response);
                if (Volley.sDebug) {
                    stethoManager.postConnect();
                }
                return response;
            }
        } finally {
            if (mConnection != null) {
                mConnection.disconnect();
            }
        }
    }

    /**
     * 对https请求添加双向认证的机制
     *
     * @param auth 是否需要双向认证
     */
    private void handleHttps(boolean auth) {
        try {
            //1.添加hostnameVerifier
            HostnameVerifier e = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(e);
            //2.处理SSL
            SSLContextFactory factory = SSLContextFactory.getInstance();
            SSLContext context = null;
            if (auth) {
                context = factory.getSSLContext();
            }
            if (context == null) {
                context = factory.makeContext();
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            HttpLogger.e(e);
        }

    }

    protected static Map<String, String> convertHeaders(Header[] headers) {
        TreeMap<String, String> result = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headers.length; ++i) {
            result.put(headers[i].getName(), headers[i].getValue());
        }
        return result;
    }

    public byte[] getBytes(InputStream is, boolean hasGzip) throws Exception {
        if (hasGzip) {
            is = new GZIPInputStream(is);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        is.close();
        bos.flush();
        byte[] result = bos.toByteArray();
        bos.close();
        return result;
    }


    private void entityFromConnection(HttpURLConnection connection, CustomResponse response) throws VolleyError {
        boolean hasGzip = hasGzip(response.getHeaders("Content-Encoding"));
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
            if (Volley.sDebug) {
                inputStream = stethoManager.interpretResponseStream(inputStream);
            }
        } catch (IOException e) {
            inputStream = connection.getErrorStream();
            if (Volley.sDebug) {
                stethoManager.httpExchangeFailed(e);
            }
        }
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(this.mPool);
        byte[] buffer = null;
        try {
            if (hasGzip) {
                inputStream = new GZIPInputStream(inputStream);
            }
            if (inputStream == null) {
                throw new ServerError();
            } else {
                buffer = this.mPool.getBuf(1024);
                int count;
                while ((count = inputStream.read(buffer)) != -1) {
                    bytes.write(buffer, 0, count);
                }
                byte[] buffers = bytes.toByteArray();
                response.setData(buffers);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.mPool.returnBuf(buffer);
                if (inputStream != null) {
                    inputStream.close();
                }
                bytes.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    protected HttpURLConnection createConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    private HttpURLConnection openConnection(URL url, Request<?> request) throws IOException {
        HttpURLConnection connection = this.createConnection(url);
        int timeoutMs = request.getTimeoutMs();
        connection.setConnectTimeout(timeoutMs);
        connection.setReadTimeout(timeoutMs);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        return connection;
    }

    static void setConnectionParametersForRequest(HttpURLConnection connection, Request<?> request) throws IOException, AuthFailureError {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                byte[] postBody = request.getBody();
                if (postBody != null) {
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty("Content-Type", request.getBodyContentType());
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.write(postBody);
                    out.close();
                }
                break;
            case Request.Method.GET:
                connection.setRequestMethod("GET");
                break;
            case Request.Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case Request.Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            case Request.Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case Request.Method.HEAD:
                connection.setRequestMethod("HEAD");
                break;
            case Request.Method.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;
            case Request.Method.TRACE:
                connection.setRequestMethod("TRACE");
                break;
            case Request.Method.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown request method.");

        }

    }

    private boolean hasGzip(Header[] headers) {
        if (headers == null || headers.length <= 0) {
            return false;
        }
        for (Header header : headers) {
            if ("gzip".equals(header.getValue())) {
                return true;
            }
        }
        return false;
    }

    private static void addBodyIfExists(HttpURLConnection connection, Request<?> request) throws IOException, AuthFailureError {
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty("Content-Type", request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();
        }
    }
}

