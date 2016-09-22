package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time:2016/6/27
 * description:传播工具
 *
 * @author sunjianfei
 */
public class SpreadTool implements Parcelable {
    String id;
    String title;
    String url;
    String description;
    String image;
    //价格，单位分
    int price;
    //兑换积分
    int credit;
    boolean share;
    String startTime;
    String endTime;
    AdvShareInfo shareInfo;
    // 支付方式 wxPay-微信支付；cPay-积分；bPay-余额
    String payType;
    String bgImage;
    //会员免费
    boolean memberFree;
    //0、传播工具 1、宣传海报
    int type;

    public static final int TYPE_SPREAD = 0;
    public static final int TYPE_POSTER = 1;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public AdvShareInfo getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(AdvShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public boolean isMemberFree() {
        return memberFree;
    }

    public void setMemberFree(boolean memberFree) {
        this.memberFree = memberFree;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SpreadTool() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeString(this.description);
        dest.writeString(this.image);
        dest.writeInt(this.price);
        dest.writeInt(this.credit);
        dest.writeByte(this.share ? (byte) 1 : (byte) 0);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeString(this.payType);
        dest.writeString(this.bgImage);
        dest.writeByte(this.memberFree ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
    }

    protected SpreadTool(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.description = in.readString();
        this.image = in.readString();
        this.price = in.readInt();
        this.credit = in.readInt();
        this.share = in.readByte() != 0;
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.shareInfo = in.readParcelable(AdvShareInfo.class.getClassLoader());
        this.payType = in.readString();
        this.bgImage = in.readString();
        this.memberFree = in.readByte() != 0;
        this.type = in.readInt();
    }

    public static final Creator<SpreadTool> CREATOR = new Creator<SpreadTool>() {
        @Override
        public SpreadTool createFromParcel(Parcel source) {
            return new SpreadTool(source);
        }

        @Override
        public SpreadTool[] newArray(int size) {
            return new SpreadTool[size];
        }
    };
}
