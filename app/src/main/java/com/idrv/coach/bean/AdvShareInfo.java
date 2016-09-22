package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time:2016/4/26
 * description:广告里的分享的内容
 *
 * @author sunjianfei
 */
public class AdvShareInfo implements Parcelable {
    String id;
    String shareTitle;
    String shareContent;
    String shareUrl;
    String shareImageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareImageUrl() {
        return shareImageUrl;
    }

    public void setShareImageUrl(String shareImageUrl) {
        this.shareImageUrl = shareImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.shareTitle);
        dest.writeString(this.shareContent);
        dest.writeString(this.shareUrl);
        dest.writeString(this.shareImageUrl);
    }

    public AdvShareInfo() {
    }

    protected AdvShareInfo(Parcel in) {
        this.id = in.readString();
        this.shareTitle = in.readString();
        this.shareContent = in.readString();
        this.shareUrl = in.readString();
        this.shareImageUrl = in.readString();
    }

    public static final Parcelable.Creator<AdvShareInfo> CREATOR = new Parcelable.Creator<AdvShareInfo>() {
        @Override
        public AdvShareInfo createFromParcel(Parcel source) {
            return new AdvShareInfo(source);
        }

        @Override
        public AdvShareInfo[] newArray(int size) {
            return new AdvShareInfo[size];
        }
    };
}
