package com.zjb.volley.download;

/**
 * time: 15/7/15
 * description:异步下载任务基础类
 *
 * @author sunjianfei
 */


import android.os.AsyncTask;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseDownloadTask extends AsyncTask<Void, Float, DownloadResult> {
    protected IDownloadListener mListener;
    protected String mUrl;
    protected AtomicBoolean isCanceled;

    public BaseDownloadTask(String url) {
        this(url, null);
    }

    public BaseDownloadTask(String url, IDownloadListener listener) {
        this.isCanceled = new AtomicBoolean(false);
        this.mUrl = url;
        this.mListener = listener;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setDownloadListener(IDownloadListener listener) {
        this.mListener = listener;
    }

    public final DownloadResult syncExecute() {
        return this.doInBackground(new Void[0]);
    }

    public BaseDownloadTask cloneTask() throws Exception {
        throw new Exception("Method is not implemented.");
    }

    protected void onCancelled() {
        this.isCanceled.set(true);
        this.mListener = null;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        if (!this.isCancelled() && !this.isCanceled.get() && null != this.mListener) {
            this.mListener.onDownloadStarted();
        }

    }

    protected void onPostExecute(DownloadResult result) {
        super.onPostExecute(result);
        if (!this.isCancelled() && !this.isCanceled.get() && null != this.mListener) {
            this.mListener.onDownloadFinished(result);
        }

    }

    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);
        if (!this.isCancelled() && !this.isCanceled.get() && null != this.mListener) {
            this.mListener.onProgressUpdate(values);
        }

    }
}

