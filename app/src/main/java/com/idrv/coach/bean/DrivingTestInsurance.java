package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time:2016/3/30
 * description:学车险
 *
 * @author sunjianfei
 */
public class DrivingTestInsurance implements Parcelable {
    String id;
    String coachId;
    String name;
    String idCard;
    String phone;
    String created;
    String detailUrl;
    //0:待审核 1:审核中 2:审核失败 3:待缴费 4:待出单5:出单成功6:出单失败 (1和2是允许用户进行修改的，其他的状态弹框提示状态信息)
    int state;
    String reason;
    String outTradeNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public DrivingTestInsurance() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.coachId);
        dest.writeString(this.name);
        dest.writeString(this.idCard);
        dest.writeString(this.phone);
        dest.writeString(this.created);
        dest.writeString(this.detailUrl);
        dest.writeInt(this.state);
        dest.writeString(this.reason);
        dest.writeString(this.outTradeNo);
    }

    protected DrivingTestInsurance(Parcel in) {
        this.id = in.readString();
        this.coachId = in.readString();
        this.name = in.readString();
        this.idCard = in.readString();
        this.phone = in.readString();
        this.created = in.readString();
        this.detailUrl = in.readString();
        this.state = in.readInt();
        this.reason = in.readString();
        this.outTradeNo = in.readString();
    }

    public static final Creator<DrivingTestInsurance> CREATOR = new Creator<DrivingTestInsurance>() {
        public DrivingTestInsurance createFromParcel(Parcel source) {
            return new DrivingTestInsurance(source);
        }

        public DrivingTestInsurance[] newArray(int size) {
            return new DrivingTestInsurance[size];
        }
    };
}
