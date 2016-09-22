package com.idrv.coach.bean;

/**
 * time:2016/6/7
 * description:咨询
 *
 * @author sunjianfei
 */
public class Consultation {
    int id;
    String nickname;
    String headimgurl;
    String phone;
    String description;
    String created;
    boolean isFake;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean isFake() {
        return isFake;
    }

    public void setIsFake(boolean isFake) {
        this.isFake = isFake;
    }
}
