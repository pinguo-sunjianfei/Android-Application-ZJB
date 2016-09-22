package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/8/1
 * description:
 *
 * @author sunjianfei
 */
public class HomePage {
    int visitCus;
    int shareCus;
    int loginSum;
    String topImgUrl;
    String bgImaUrl;
    List<Message> messages;

    public int getVisitCus() {
        return visitCus;
    }

    public void setVisitCus(int visitCus) {
        this.visitCus = visitCus;
    }

    public int getShareCus() {
        return shareCus;
    }

    public void setShareCus(int shareCus) {
        this.shareCus = shareCus;
    }

    public int getLoginSum() {
        return loginSum;
    }

    public void setLoginSum(int loginSum) {
        this.loginSum = loginSum;
    }

    public String getTopImgUrl() {
        return topImgUrl;
    }

    public void setTopImgUrl(String topImgUrl) {
        this.topImgUrl = topImgUrl;
    }

    public String getBgImaUrl() {
        return bgImaUrl;
    }

    public void setBgImaUrl(String bgImaUrl) {
        this.bgImaUrl = bgImaUrl;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void shareIncrease() {
        shareCus++;
    }
}
