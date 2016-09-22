package com.idrv.coach.bean;

/**
 * time:2016/8/4
 * description:发现页item
 *
 * @author sunjianfei
 */
public class DiscoverMainItems {
    int id;
    String title;
    String icon;
    String description;
    String url;
    String conditionality;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getConditionality() {
        return conditionality;
    }

    public void setConditionality(String conditionality) {
        this.conditionality = conditionality;
    }

    public Picture toPicture() {
        Picture picture = new Picture();
        picture.setUrl(icon);
        return picture;
    }
}
