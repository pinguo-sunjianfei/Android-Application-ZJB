package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time:2016/4/21
 * description:
 *
 * @author sunjianfei
 */
public class WebParamBuilder implements Parcelable {
    String url;
    String title;
    int pageTag;
    WebShareBean shareBean;
    boolean needBaseParam = true;
    boolean needReload;
    boolean needShareCallBack;

    public static WebParamBuilder create() {
        return new WebParamBuilder();
    }

    public String getUrl() {
        return url;
    }

    public WebParamBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public WebParamBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public WebShareBean getShareBean() {
        return shareBean;
    }

    public WebParamBuilder setShareBean(WebShareBean shareBean) {
        this.shareBean = shareBean;
        return this;
    }

    public boolean isNeedBaseParam() {
        return needBaseParam;
    }

    public WebParamBuilder setNeedBaseParam(boolean needBaseParam) {
        this.needBaseParam = needBaseParam;
        return this;
    }

    public boolean isNeedReload() {
        return needReload;
    }

    public WebParamBuilder setNeedReload(boolean needReload) {
        this.needReload = needReload;
        return this;
    }

    public boolean isNeedShareCallBack() {
        return needShareCallBack;
    }

    public WebParamBuilder setNeedShareCallBack(boolean needShareCallBack) {
        this.needShareCallBack = needShareCallBack;
        return this;
    }

    public int getPageTag() {
        return pageTag;
    }

    public WebParamBuilder setPageTag(int pageTag) {
        this.pageTag = pageTag;
        return this;
    }

    public WebParamBuilder() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.title);
        dest.writeInt(this.pageTag);
        dest.writeParcelable(this.shareBean, 0);
        dest.writeByte(needBaseParam ? (byte) 1 : (byte) 0);
        dest.writeByte(needReload ? (byte) 1 : (byte) 0);
        dest.writeByte(needShareCallBack ? (byte) 1 : (byte) 0);
    }

    protected WebParamBuilder(Parcel in) {
        this.url = in.readString();
        this.title = in.readString();
        this.pageTag = in.readInt();
        this.shareBean = in.readParcelable(WebShareBean.class.getClassLoader());
        this.needBaseParam = in.readByte() != 0;
        this.needReload = in.readByte() != 0;
        this.needShareCallBack = in.readByte() != 0;
    }

    public static final Creator<WebParamBuilder> CREATOR = new Creator<WebParamBuilder>() {
        public WebParamBuilder createFromParcel(Parcel source) {
            return new WebParamBuilder(source);
        }

        public WebParamBuilder[] newArray(int size) {
            return new WebParamBuilder[size];
        }
    };
}
