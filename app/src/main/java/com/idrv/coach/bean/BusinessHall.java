package com.idrv.coach.bean;

/**
 * time:2016/3/14
 * description:任务大厅里的任务
 *
 * @author sunjianfei
 */
public class BusinessHall {
    String icon;
    String name;
    String url;
    boolean toggle;
    String created;
    String updated;

    boolean share;
    String shareTitle;
    String shareContent;
    String shareUrl;
    String shareImageUrl;

    //是否为假数据
    boolean isFake;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public boolean isToggle() {
        return toggle;
    }

    public boolean isFake() {
        return isFake;
    }

    public void setIsFake(boolean isFake) {
        this.isFake = isFake;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
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
}
