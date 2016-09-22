package com.idrv.coach.bean;

/**
 * Created by sunjianfei on 15-9-21.
 * description:照片认证实体对象
 *
 * @author crab
 */
public class UploadPhotoAuth {
    private String url;
    private String etag;
    private String token;
    private String bucket;
    private String expires;

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

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}
