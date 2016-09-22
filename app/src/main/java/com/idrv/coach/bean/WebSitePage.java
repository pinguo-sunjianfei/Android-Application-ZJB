package com.idrv.coach.bean;

import com.idrv.coach.utils.ValidateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/6/6
 * description:我的网站
 *
 * @author sunjianfei
 */
public class WebSitePage {
    String loginDay;
    String convey;
    int pictureNum;
    int commitNum;
    int askNum;
    List<Picture> pictureList;
    List<Comment> messageList;
    List<WebSitePhoto> webSitePhotos;

    public String getLoginDay() {
        return loginDay;
    }

    public void setLoginDay(String loginDay) {
        this.loginDay = loginDay;
    }

    public String getConvey() {
        return convey;
    }

    public void setConvey(String convey) {
        this.convey = convey;
    }

    public int getPictureNum() {
        return pictureNum;
    }

    public void setPictureNum(int pictureNum) {
        this.pictureNum = pictureNum;
    }

    public int getCommitNum() {
        return commitNum;
    }

    public void setCommitNum(int commitNum) {
        this.commitNum = commitNum;
    }

    public int getAskNum() {
        return askNum;
    }

    public void setAskNum(int askNum) {
        this.askNum = askNum;
    }

    public List<Picture> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<Picture> pictureList) {
        this.pictureList = pictureList;
    }

    public List<WebSitePhoto> getWebSitePhotos() {
        return webSitePhotos;
    }

    public void setWebSitePhotos(List<WebSitePhoto> webSitePhotos) {
        this.webSitePhotos = webSitePhotos;
    }

    public List<Comment> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Comment> messageList) {
        this.messageList = messageList;
    }

    public static List<WebSitePhoto> changePhoto(List<Picture> pictures) {
        int groupNum = 4; //以每4个分一组
        List<WebSitePhoto> webSitePhotos = new ArrayList<>();
        if (ValidateUtil.isValidate(pictures)) {
            int remainder = pictures.size() % groupNum;
            int size = pictures.size() / groupNum;
            size = remainder == 0 ? size : size + 1;

            for (int i = 0; i < size; i++) {
                WebSitePhoto webSitePhoto = new WebSitePhoto();
                int lastIndex = i * groupNum;
                if (i == size - 1 && remainder > 0) {
                    webSitePhoto.setPictures(pictures.subList(lastIndex, lastIndex + remainder));
                } else {
                    webSitePhoto.setPictures(pictures.subList(lastIndex, lastIndex + groupNum));
                }
                webSitePhotos.add(webSitePhoto);
            }
        }
        return webSitePhotos;
    }
}
