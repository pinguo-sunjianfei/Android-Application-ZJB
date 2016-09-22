package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time:2016/4/9
 * description:H5需要分享的
 *
 * @author sunjianfei
 */
public class WebShareBean implements Parcelable {
    String shareTitle;
    String shareContent;
    String shareUrl;
    String shareImageUrl;
    int pageSubject;

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

    public int getPageSubject() {
        return pageSubject;
    }

    public void setPageSubject(int pageSubject) {
        this.pageSubject = pageSubject;
    }

    public WebShareBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.shareTitle);
        dest.writeString(this.shareContent);
        dest.writeString(this.shareUrl);
        dest.writeString(this.shareImageUrl);
        dest.writeInt(this.pageSubject);
    }

    protected WebShareBean(Parcel in) {
        this.shareTitle = in.readString();
        this.shareContent = in.readString();
        this.shareUrl = in.readString();
        this.shareImageUrl = in.readString();
        this.pageSubject = in.readInt();
    }

    public static final Creator<WebShareBean> CREATOR = new Creator<WebShareBean>() {
        public WebShareBean createFromParcel(Parcel source) {
            return new WebShareBean(source);
        }

        public WebShareBean[] newArray(int size) {
            return new WebShareBean[size];
        }
    };
}
