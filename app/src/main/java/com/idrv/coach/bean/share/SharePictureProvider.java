package com.idrv.coach.bean.share;

import android.os.Parcel;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;

/**
 * time: 15/6/11
 * description: 进行图片分享的时候提供分享实体
 *
 * @author sunjianfei
 */
public class SharePictureProvider implements IShareProvider {
    private String mPhotoPath;
    private String targetUrl;


    public SharePictureProvider(String photoPath, String targetUrl) {
        this.mPhotoPath = photoPath;
        this.targetUrl = targetUrl;
    }

    @Override
    public ShareBean createSinaShareBean() {
        ShareBean.Builder builder = new ShareBean.Builder();
        return builder.build();
    }

    @Override
    public ShareBean createWeixinShareBean() {
        ShareBean.Builder builder = new ShareBean.Builder();
        builder.setImagePath(mPhotoPath);
        builder.setTargetUrl(targetUrl);
        builder.setTitle(ZjbApplication.gContext.getString(R.string.views_photo));
        return builder.build();
    }

    @Override
    public ShareBean createTimelineShareBean() {
        ShareBean.Builder builder = new ShareBean.Builder();
        builder.setImagePath(mPhotoPath);
        builder.setTargetUrl(targetUrl);
        builder.setTitle(ZjbApplication.gContext.getString(R.string.views_photo));
        return builder.build();
    }

    @Override
    public ShareBean createQzoneShareBean() {
        ShareBean.Builder builder = new ShareBean.Builder();
        builder.setImagePath(mPhotoPath);
        builder.setTargetUrl(targetUrl);
        builder.setTitle(ZjbApplication.gContext.getString(R.string.views_photo));
        return builder.build();
    }

    @Override
    public int getShareType() {
        return SHARE_CONTENT_TYPE_IMAGE;
    }


    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPhotoPath);
    }

    protected SharePictureProvider(Parcel in) {
        this.mPhotoPath = in.readString();
    }

    public static final Creator<SharePictureProvider> CREATOR = new Creator<SharePictureProvider>() {
        public SharePictureProvider createFromParcel(Parcel source) {
            return new SharePictureProvider(source);
        }

        public SharePictureProvider[] newArray(int size) {
            return new SharePictureProvider[size];
        }
    };
}
