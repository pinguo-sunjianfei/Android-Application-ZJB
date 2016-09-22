package com.idrv.coach.bean.event;

/**
 * time: 15/6/6
 * description: 封装了事件分发的实体
 *
 * @author sunjianfei
 */
public class BaseEvent<T> {
    public static final int DATA_OK = 200;
    public static final int DATA_ERROR = 400;

    /*type 字段主要用来传递该事件所属的类型--用来标示同一个界面的事件类型*/
    protected int type;
    /*code 用来判断网络返回数据的正确性，200表示数据正确返回，其他数字的意义可以自行定义*/
    protected int code;
    /*data 用来表示event传递的数据，例如网络返回时封装的数据*/
    protected T data;
    /*extra 用来表示额外的信息，比如网络错误通常会添加extra传递错误信息*/
    protected Object extra;
    /*key 主要用来判断事件的接收条件，比如，很多照片详情界面同时发出请求访问网络，每个界面都能收到
    * EventBus的消息，这时候通过key = photoId来判断是否符合该界面的接收条件*/
    protected String key;

    public int getType() {
        return type;
    }

    public BaseEvent() {
    }

    public BaseEvent(int type, int code, String key, T data, Object extra) {
        this.key = key;
        this.code = code;
        this.type = type;
        this.data = data;
        this.extra = extra;
    }

    public int getCode() {
        return code;
    }

    public BaseEvent setCode(int code) {
        this.code = code;
        return this;
    }

    public T getData() {
        return data;
    }

    public Object getExtra() {
        return extra;
    }

    public String getKey() {
        return key;
    }

    public BaseEvent setType(int type) {
        this.type = type;
        return this;
    }

    public BaseEvent setKey(String key) {
        this.key = key;
        return this;
    }

    public BaseEvent<T> setData(T data) {
        this.data = data;
        return this;
    }

    public BaseEvent setExtra(Object extra) {
        this.extra = extra;
        return this;
    }

    public static class Builder<T> {
        private int type;
        private int code;
        private T data;
        private Object extra;
        private String key;

        public Builder(int type) {
            this.type = type;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setData(T data) {
            this.data = data;
            return this;
        }

        public Builder setExtra(Object extra) {
            this.extra = extra;
            return this;
        }

        public Builder setCode(int code) {
            this.code = code;
            return this;
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public BaseEvent<T> build() {
            return new BaseEvent<T>(code, type, key, data, extra);
        }
    }
}
