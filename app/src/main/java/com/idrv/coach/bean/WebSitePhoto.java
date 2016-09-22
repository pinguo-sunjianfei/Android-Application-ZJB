package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/6/4
 * description:
 *
 * @author sunjianfei
 */
public class WebSitePhoto {
    List<Picture> pictures;
    boolean isFake;

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public boolean isFake() {
        return isFake;
    }

    public void setIsFake(boolean isFake) {
        this.isFake = isFake;
    }
}
