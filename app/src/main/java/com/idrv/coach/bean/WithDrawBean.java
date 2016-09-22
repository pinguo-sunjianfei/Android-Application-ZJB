package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time:2016/3/11
 * description:提现实体
 *
 * @author sunjianfei
 */
public class WithDrawBean implements Parcelable {
    //提取金额总数
    private String moneySum;
    //银行名称
    private String bankName;
    //银行卡号
    private String bankId;
    //开卡人姓名
    private String name;
    //身份证号
    private String cardId;
    //手机号
    private String telNum;

    public String getMoneySum() {
        return moneySum;
    }

    public void setMoneySum(String moneySum) {
        this.moneySum = moneySum;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.moneySum);
        dest.writeString(this.bankName);
        dest.writeString(this.bankId);
        dest.writeString(this.name);
        dest.writeString(this.cardId);
        dest.writeString(this.telNum);
    }

    public WithDrawBean() {
    }

    protected WithDrawBean(Parcel in) {
        this.moneySum = in.readString();
        this.bankName = in.readString();
        this.bankId = in.readString();
        this.name = in.readString();
        this.cardId = in.readString();
        this.telNum = in.readString();
    }

    public static final Parcelable.Creator<WithDrawBean> CREATOR = new Parcelable.Creator<WithDrawBean>() {
        public WithDrawBean createFromParcel(Parcel source) {
            return new WithDrawBean(source);
        }

        public WithDrawBean[] newArray(int size) {
            return new WithDrawBean[size];
        }
    };
}
