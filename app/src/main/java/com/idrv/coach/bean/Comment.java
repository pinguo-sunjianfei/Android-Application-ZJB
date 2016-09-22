package com.idrv.coach.bean;

/**
 * time:2016/6/4
 * description:评论实体
 *
 * @author sunjianfei
 */
public class Comment {
    String id;
    String nickname;
    String headimgurl;
    String content;
    boolean isFake;
    //消息类型
    int messageType;
    String time;
    //电话
    String phone;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFake() {
        return isFake;
    }

    public void setIsFake(boolean isFake) {
        this.isFake = isFake;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
