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
 * time: 15/7/15
 * description:下载任务类
 *
 * @author sunjianfei
 */

public class DownloadTask extends BaseDownloadTask {
    private static final String TAG = DownloadTask.class.getSimpleName();
    protected static final int BYTE_SIZE = 8192;
    protected Context mContext;
    protected HttpURLConnection mUrlConnection;
    protected float mFileSize = 0.0F;
    protected float mTempFileSize = 0.0F;
    protected String mTempFilePath = null;

    public DownloadTask(Context context, String url, String filePath, IDownloadListener listener) {
        super(url, listener);
        this.mContext = context.getApplicationContext();
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

    public void abort(boolean isInterrupt) {
        this.cancel(isInterrupt);
        this.isCanceled.set(true);
        this.mListener = null;
        if (isInterrupt && this.mUrlConnection != null) {
            this.mUrlConnection.disconnect();
            this.mUrlConnection = null;
        }

    }

    protected DownloadResult doInBackground(Void... params) {
        DownloadResult result = new DownloadResult();
        result.url = this.mUrl;
        if (!this.isCancelled()) {
            this.download(result);
        } else {
            result.code = -1;
            result.message = "The download task has been cancelled.";
            result.path = "";
        }

        return result;
    }

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

                if (this.mUrlConnection != null) {
                    this.mUrlConnection.disconnect();
                    this.mUrlConnection = null;
                }
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
            for (sdExist = true; (len = inputStream.read(buffer, 0, buffer.length)) != -1 && !this.isCancelled();
                 this.publishProgress(mTempFileSize, mFileSize)) {
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

