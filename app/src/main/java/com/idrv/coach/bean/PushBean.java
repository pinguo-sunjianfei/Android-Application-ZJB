package com.idrv.coach.bean;

import com.zjb.volley.utils.GsonUtil;

/**
 * time:2016/3/15
 * description:推送的透传消息实体
 *
 * @author sunjianfei
 */
public class PushBean {
    String icon;
    String title;
    String content;
    String schema;
    int type;
    Message message;

    public static PushBean getPushBean(String json) {
        if (json == null) {
            return null;
        }

        return GsonUtil.fromJson(json, PushBean.class);
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message messages) {
        this.message = messages;
    }
}
