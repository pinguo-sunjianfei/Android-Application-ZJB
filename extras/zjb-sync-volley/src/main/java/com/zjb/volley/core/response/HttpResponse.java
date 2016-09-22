package com.zjb.volley.core.response;

/**
 * time: 15/6/15
 * description: 封装返回数据（这里封装的是返回的最外层数据，可以根据返回的字段自行扩展）
 *
 * @author sunjianfei
 */
public class HttpResponse<T> {
    public static final int CODE_FAILED = -1;
    public int status;
    public String message;
    public long serverTime;
    public T data;
    public Inform inform;

    public HttpResponse() {
    }

    public HttpResponse(int status) {
        this.status = status;
    }

    public static class Inform {
        private double time;

        public double getTime() {
            return time;
        }

        public void setTime(double time) {
            this.time = time;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Inform getInform() {
        return inform;
    }

    public void setInform(Inform inform) {
        this.inform = inform;
    }
}
