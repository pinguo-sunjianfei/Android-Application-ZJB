package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sunjianfei on 2016/1/4.
 * 用户信息
 */
public class User implements Parcelable {

    private String uid;
    private int sex;
    private String phone;
    private String nickname;
    private String thirdPlatforms;
    private String contact;
    private String city;
    private int inviteCodeId;
    private String token;
    private String email;
    private String name;
    private String province;
    private String role;
    private String headimgurl;
    private String idNumber;
    // 0:非会员，1:普通会员 ……
    private int member;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setThirdPlatforms(String thirdPlatforms) {
        this.thirdPlatforms = thirdPlatforms;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setInviteCodeId(int inviteCodeId) {
        this.inviteCodeId = inviteCodeId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getUid() {
        return uid;
    }

    public int getSex() {
        return sex;
    }

    public String getPhone() {
        return phone;
    }

    public String getNickname() {
        return nickname;
    }

    public String getThirdPlatforms() {
        return thirdPlatforms;
    }

    public String getContact() {
        return contact;
    }

    public String getCity() {
        return city;
    }

    public int getInviteCodeId() {
        return inviteCodeId;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public String getRole() {
        return role;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    public User() {
    }

    public static User getFakeUser() {
        User user = new User();
        user.setCity("成都");
        user.setContact("15882372099");
        user.setHeadimgurl("http://7xrj6u.com2.z0.glb.qiniucdn.com/FkccQhRQFujWq_YE4knY4ItfrPUE");
        user.setInviteCodeId(1);
        user.setNickname("淡蓝色的星期四");
        user.setPhone("15882372099");
        user.setSex(3);
        user.setToken("iOnLeyJleHAiOjE0NjMxMjk0MjIsInVpZCI6ImJlMzU2NzY1ZTllYzExZTViZTJjMDAxNjNlMDAxYmNlIn0.6Iqfox9hRxGoMbWJCiq6FMncFCd5LcJqI3ys1A-skGA");
        user.setUid("be356765e9ec11e5be2c00163e001bce");
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeInt(this.sex);
        dest.writeString(this.phone);
        dest.writeString(this.nickname);
        dest.writeString(this.thirdPlatforms);
        dest.writeString(this.contact);
        dest.writeString(this.city);
        dest.writeInt(this.inviteCodeId);
        dest.writeString(this.token);
        dest.writeString(this.email);
        dest.writeString(this.name);
        dest.writeString(this.province);
        dest.writeString(this.role);
        dest.writeString(this.headimgurl);
        dest.writeString(this.idNumber);
        dest.writeInt(this.member);
    }

    protected User(Parcel in) {
        this.uid = in.readString();
        this.sex = in.readInt();
        this.phone = in.readString();
        this.nickname = in.readString();
        this.thirdPlatforms = in.readString();
        this.contact = in.readString();
        this.city = in.readString();
        this.inviteCodeId = in.readInt();
        this.token = in.readString();
        this.email = in.readString();
        this.name = in.readString();
        this.province = in.readString();
        this.role = in.readString();
        this.headimgurl = in.readString();
        this.idNumber = in.readString();
        this.member = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
