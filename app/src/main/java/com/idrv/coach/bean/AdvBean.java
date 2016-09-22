package com.idrv.coach.bean;

/**
 * time:2016/4/26
 * description:广告
 *
 * @author sunjianfei
 */
public class AdvBean {
    String id;
    String title;
    String imageUrl;
    String schema;
    String describe;
    String type;
    boolean share;
    String deadtime;
    AdvShareInfo shareInfo;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public String getDeadtime() {
        return deadtime;
    }

    public void setDeadtime(String deadtime) {
        this.deadtime = deadtime;
    }

    public AdvShareInfo getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(AdvShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
