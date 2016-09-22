package com.idrv.coach.bean;

/**
 * time:2016/8/1
 * description:首页消息的每个子View
 *
 * @author sunjianfei
 */
public class ChildView {
    String text;
    String color;
    String icon;
    //每个组件对应的位置
    int areaId;
    // 0-左对齐,1-居中显示,2-右对齐
    int align;
    //每个组件点击的跳转链接
    String schema;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
