package com.idrv.coach.bean;

/**
 * time:2016/5/23
 * description:相册
 *
 * @author sunjianfei
 */
public class Album {
    //O:学员风采相册   1:教练风采相册
    int albumType;
    int pictureNumber;
    String cover;

    public int getAlbumType() {
        return albumType;
    }

    public void setAlbumType(int albumType) {
        this.albumType = albumType;
    }

    public int getPictureNumber() {
        return pictureNumber;
    }

    public void setPictureNumber(int pictureNumber) {
        this.pictureNumber = pictureNumber;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
