package com.zjb.volley.download;

/**
 * time: 15/7/15
 * description:
 *
 * @author sunjianfei
 */

public class DownloadResult {
    public static final int CODE_FAILED = -1;
    public static final int CODE_SUCCESS = 0;
    public String url;
    public int code = -1;
    public String message = "";
    public String path = "";

    public DownloadResult() {
    }

    public DownloadResult(String url, int code, String msg) {
        this.url = url;
        this.code = code;
        this.message = msg;
    }

    public String toString() {
        return "DownloadResult{url=" + this.url + ", code=" + this.code + ", message=\'" + this.message + '\'' + '}';
    }
}
