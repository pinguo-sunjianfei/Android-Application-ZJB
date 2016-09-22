package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/7/14
 * description:宣传海报
 *
 * @author sunjianfei
 */
public class PosterPage {
    int id;
    String title;
    String imageUrl;
    int transmissionToolId;
    int width;
    int height;
    int left;
    int top;
    int right;
    String created;
    List<Effect> effects;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getTransmissionToolId() {
        return transmissionToolId;
    }

    public void setTransmissionToolId(int transmissionToolId) {
        this.transmissionToolId = transmissionToolId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public void setEffects(List<Effect> effects) {
        this.effects = effects;
    }
}
