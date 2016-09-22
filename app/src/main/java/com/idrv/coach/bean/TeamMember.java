package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time: 2016/3/23
 * description:
 *
 * @author bigflower
 */
public class TeamMember implements Parcelable {

    /**
     * id : 8c27cae3ea5d11e5be57acbc32acbfc9
     * nickname : HW测试
     * headimgurl : http://
     * sex : 0
     */
    private String id;
    private String nickname;
    private String headimgurl;
    private int sex;
    private int status;

    private boolean isSelected;

    public TeamMember(String nickname, String headimgurl) {
        this.nickname = nickname;
        this.headimgurl = headimgurl;
    }

    public TeamMember(String id, String nickname, String headimgurl, int sex) {
        this.id = id;
        this.nickname = nickname;
        this.headimgurl = headimgurl;
        this.sex = sex;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public int getSex() {
        return sex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TeamMember{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", sex=" + sex +
                '}';
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMember member = (TeamMember) o;
        if (id == member.id) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode() * 31;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nickname);
        dest.writeString(this.headimgurl);
        dest.writeInt(this.sex);
        dest.writeInt(this.status);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
    }

    protected TeamMember(Parcel in) {
        this.id = in.readString();
        this.nickname = in.readString();
        this.headimgurl = in.readString();
        this.sex = in.readInt();
        this.status = in.readInt();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<TeamMember> CREATOR = new Creator<TeamMember>() {
        public TeamMember createFromParcel(Parcel source) {
            return new TeamMember(source);
        }

        public TeamMember[] newArray(int size) {
            return new TeamMember[size];
        }
    };
}
