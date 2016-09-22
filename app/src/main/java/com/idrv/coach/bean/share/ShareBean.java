package com.idrv.coach.bean.share;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * time: 15/6/11
 * description: 封装分享实体
 *
 * @author sunjianfei
 */
public class ShareBean implements Serializable {

    private static final long serialVersionUID = 7284290162174000604L;

    private String mTargetUrl;

    private String mTitle;

    private String mSummary;
    private ArrayList<String> mImageUrls;
    private Bitmap mBitmap;

    private String mAccessToken;

    private String mImagePath;

    private String mUserName;

    private ShareBean() {
    }


    private ShareBean(String mTargetUrl, String mTitle, String mSummary, ArrayList<String> imageUrls
            , Bitmap mBitmap, String mAccessToken, String imagePath, String userName) {
        this.mTargetUrl = mTargetUrl;
        this.mTitle = mTitle;
        this.mSummary = mSummary;
        this.mImageUrls = imageUrls;
        this.mBitmap = mBitmap;
        this.mAccessToken = mAccessToken;
        this.mImagePath = imagePath;
        this.mUserName = userName;
    }

    public static class Builder {
        private String targetUrl;

        private String title;

        private String summary;
        private ArrayList<String> imageUrls;
        private Bitmap bitmap;

        private String accessToken;
        private String imagePath;
        private String userName;

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setTargetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder setImageUrls(ArrayList<String> imageUrls) {
            this.imageUrls = imageUrls;
            return this;
        }

        public Builder setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public Builder setImagePath(String path) {
            this.imagePath = path;
            return this;
        }

        public Builder setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public ShareBean build() {
            return new ShareBean(targetUrl, title, summary, imageUrls, bitmap, accessToken, imagePath, userName);
        }
    }

    public String getUserName() {
        return mUserName;
    }

    public String getTargetUrl() {
        return mTargetUrl;
    }

    public void setTargetUrl(String mTargetUrl) {
        this.mTargetUrl = mTargetUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSummary() {
        return mSummary;
    }

    public ArrayList<String> getImageUrls() {
        return mImageUrls;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        this.mImagePath = imagePath;
    }
}
