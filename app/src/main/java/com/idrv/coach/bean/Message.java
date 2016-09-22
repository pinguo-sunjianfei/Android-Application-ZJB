package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/8/1
 * description:首页消息体
 *
 * @author sunjianfei
 */
public class Message {
    int messageType;
    //头像
    String icon;
    //主昵称
    String source;
    //副昵称
    String channel;
    //时间
    String time;
    //跳转的uri
    String schema;
    List<ChildView> components;
    //消息气泡
    String bubble;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<ChildView> getComponents() {
        return components;
    }

    public void setComponents(List<ChildView> components) {
        this.components = components;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getBubble() {
        return bubble;
    }

    public void setBubble(String bubble) {
        this.bubble = bubble;
    }
}
