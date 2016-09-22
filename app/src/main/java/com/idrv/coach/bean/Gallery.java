package com.idrv.coach.bean;

import java.util.TreeMap;

/**
 * time: 2015/9/23
 * description:
 *
 * @author sunjianfei
 */
public class Gallery {
    private int mId;
    private String mPath;
    private int mGalleryId;
    private String mGalleryName;
    private int mCount;
    // private TreeSet<Integer> mPictures = new TreeSet<>((lhs, rhs) -> rhs - lhs);
    private TreeMap<Integer, String> mPictures = new TreeMap<>((lhs, rhs) -> rhs - lhs);

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public int getGalleryId() {
        return mGalleryId;
    }

    public void setGalleryId(int galleryId) {
        this.mGalleryId = galleryId;
    }

    public String getGalleryName() {
        return mGalleryName;
    }

    public void setGalleryName(String galleryName) {
        this.mGalleryName = galleryName;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
    }


    public TreeMap<Integer, String> getPictures() {
        return mPictures;
    }

    public void setPictures(TreeMap<Integer, String> pictures) {
        this.mPictures = pictures;
    }

    @Override
    public String toString() {
        return mGalleryName;
    }
}
