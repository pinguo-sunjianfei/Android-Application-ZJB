package com.idrv.coach.bean.share;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * time: 15/6/11
 * description: 分享Web的bean
 *
 * @author sunjianfei
 */
public class ShareWebProvider implements IShareProvider {
    private String url;
    private String imagePath;
    private String title;
    private String desc;

    @Override
    public ShareBean createSinaShareBean() {
        ShareBean.Builder builder = new ShareBean.Builder();
        return builder.build();
    }

    @Override
    public ShareBean createWeixinShareBean() {
        ShareBean.Builder builder = new ShareBean.Builder();
        builder.setTitle(title);
        builder.setImagePath(imagePath);
        builder.setSummary(desc);
        builder.setTargetUrl(url);
        return builder.build();
    }

    @Override
    public ShareBean createTimelineShareBean() {
        ShareBean.Builder builder = new ShareBean.Builder();
        ArrayList<String> paths = new ArrayList<>();
        paths.add(imagePath);
        builder.setImagePath(imagePath);
        builder.setTargetUrl(url);
        builder.setTitle(title);
        builder.setImageUrls(paths);
        return builder.build();
    }

    @Override
    public ShareBean createQzoneShareBean() {
        ShareBean.Builder builder = new ShareBean.Builder();
        builder.setTitle(title);
        builder.setImagePath(imagePath);
        builder.setSummary(desc);
        builder.setTargetUrl(url);
        return builder.build();
    }

    @Override
    public int getShareType() {
        return SHARE_CONTENT_TYPE_WEB;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.imagePath);
        dest.writeString(this.title);
        dest.writeString(this.desc);
    }

    public ShareWebProvider() {
    }

    protected ShareWebProvider(Parcel in) {
        this.url = in.readString();
        this.imagePath = in.readString();
        this.title = in.readString();
        this.desc = in.readString();
    }

    public static final Creator<ShareWebProvider> CREATOR = new Creator<ShareWebProvider>() {
        public ShareWebProvider createFromParcel(Parcel source) {
            return new ShareWebProvider(source);
        }

        public ShareWebProvider[] newArray(int size) {
            return new ShareWebProvider[size];
        }
    };
}
