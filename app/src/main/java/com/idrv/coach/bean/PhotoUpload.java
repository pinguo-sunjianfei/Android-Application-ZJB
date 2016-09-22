package com.idrv.coach.bean;

/**
 * time: 2016/3/17
 * description:
 *
 * @author bigflower
 */
public class PhotoUpload {
    String url;
    String token;
    String localPath;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
