package com.zjb.volley.download;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.zjb.volley.log.HttpLogger;
import com.zjb.volley.utils.NetworkUtil;
import com.zjb.volley.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;

/**
 * time: 2015/11/3
 * description:同步下载模块，下载线程由其他线程管理器处理
 *
 * @author sunjianfei
 */
public class SyncDownloadTask {
    private static final String TAG = DownloadTask.class.getSimpleName();
    private static final int BYTE_SIZE = 8192;
    private HttpURLConnection mUrlConnection;
    private float mFileSize = 0.0F;
    private float mTempFileSize = 0.0F;
    private String mTempFilePath = null;
    private String mUrl;
    private Context mContext;

    public SyncDownloadTask(Context context, String url, String filePath) {
        this.mContext = context;
        this.mUrl = url;
        this.mTempFilePath = filePath;
        File file = new File(this.mTempFilePath);
        if (file.exists()) {
            this.mTempFileSize = file.length();
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                HttpLogger.e(TAG, "DownloadTask create file: " + this.mTempFilePath + Log.getStackTraceString(e));
            }
        }
    }

    /**
     * 开始下载文件
     *
     * @return 下载返回结果
     */
    public DownloadResult download() {
        DownloadResult result = new DownloadResult();
        result.url = this.mUrl;
        download(result);
        return result;
    }

    /**
     * 下载
     *
     * @param result 下载返回结果
     */
    protected void download(DownloadResult result) {
        InputStream stream = null;
        try {
            this.mUrlConnection = this.openConnection(this.mUrl);
            int code = this.mUrlConnection.getResponseCode();
            if (200 == code || 206 == code) {
                int length = this.mUrlConnection.getContentLength();
                HttpLogger.e(TAG, "File total length : " + length);
                if (length >= 0) {
                    this.mFileSize += length;
                }
                stream = this.mUrlConnection.getInputStream();
                this.writeFile(stream, result);
            }
        } catch (IOException e) {
            result.code = -1;
            result.message = e.getMessage();
            result.path = "";
            HttpLogger.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    HttpLogger.e(TAG, "Download IOException." + Log.getStackTraceString(e));
                }
            }
            if (this.mUrlConnection != null) {
                this.mUrlConnection.disconnect();
                this.mUrlConnection = null;
            }

        }

    }

    protected void writeFile(InputStream inputStream, DownloadResult result) {
        FileOutputStream fos = null;
        try {
            byte[] buffer = new byte[BYTE_SIZE];
            Arrays.fill(buffer, (byte) 0);
            int len;
            boolean sdExist;
            for (sdExist = true; (len = inputStream.read(buffer, 0, buffer.length)) != -1; ) {
                if (!Util.isSDCardExist() && this.mTempFilePath.contains("sdcard")) {
                    sdExist = false;
                    break;
                }

                if (this.mTempFileSize > 0.0f) {
                    fos = new FileOutputStream(this.mTempFilePath, true);
                } else {
                    fos = new FileOutputStream(this.mTempFilePath, false);
                }

                fos.write(buffer, 0, len);
                fos.flush();
                fos.close();
                fos = null;
                this.mTempFileSize += (float) len;
                if (this.mFileSize < this.mTempFileSize) {
                    this.mFileSize = this.mTempFileSize;
                }
            }

            if (sdExist) {
                result.code = 0;
                result.message = "Download Success.";
                result.path = this.mTempFilePath;
            } else {
                result.code = -1;
                result.message = "Download Failed.";
                result.path = "";
            }
        } catch (IOException e) {
            result.code = -1;
            result.message = e.getMessage();
            result.path = "";
            HttpLogger.e(TAG, "Write file IOException." + Log.getStackTraceString(e));
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    HttpLogger.e(TAG, "Write file IOException" + Log.getStackTraceString(e));
                }
            }

        }

    }

    protected HttpURLConnection openConnection(String urlStr) throws IOException {
        if (TextUtils.isEmpty(urlStr)) {
            return null;
        } else {
            URL url = new URL(urlStr);
            HttpURLConnection connection;
            int port;
            if (NetworkUtil.isWapNet(this.mContext)) {
                String hostAndPort = url.toString();
                int host = hostAndPort.startsWith("https") ? 8 : 7;
                if (host == 7) {
                    port = hostAndPort.indexOf('/', host);
                    StringBuilder sb = new StringBuilder("http://10.0.0.172");
                    sb.append(hostAndPort.substring(port));
                    URL proxy = new URL(sb.toString());
                    connection = (HttpURLConnection) proxy.openConnection();
                    connection.setRequestProperty("X-Online-Host", hostAndPort.substring(host, port));
                } else {
                    connection = (HttpURLConnection) url.openConnection();
                }
            } else {
                String[] hostAndPort = NetworkUtil.getProxyHostAndPort(this.mContext);
                String host = hostAndPort[0];
                port = Integer.parseInt(hostAndPort[1]);
                if (host != null && host.length() != 0 && port != -1) {
                    InetSocketAddress isa = new InetSocketAddress(host, port);
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, isa);
                    connection = (HttpURLConnection) url.openConnection(proxy);
                } else {
                    connection = (HttpURLConnection) url.openConnection();
                }
            }

            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(200000);
            connection.setRequestProperty("Accept", "*, */*");
            connection.setRequestProperty("accept-charset", "utf-8");
            connection.setRequestMethod("GET");
            connection.connect();
            return connection;
        }
    }
}
