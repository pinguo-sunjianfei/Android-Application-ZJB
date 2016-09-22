package com.idrv.coach.bean;

/**
 * time:2016/7/11
 * description:
 *
 * @author sunjianfei
 */
public class Effect {
    String color;
    float left;
    float top;
    float right;
    float bottom;
    //是否可以编辑
    boolean enable;
    //0表示是文字 1表示是图片 2表示编辑框
    int effectType;
    //如果是文字,是否需要字体加粗
    boolean bold;
    //如果是文字,是否需要斜体
    boolean italic;
    //文字的最长限制
    int maxSize;
    //如果是文字,字体的大小
    float textSize;
    //如果是图片,图片地址
    String imageUrl;
    //图片类型
    int imageType;
    //默认的文案
    String defaultText;
    String schema;

    //控件类型
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_PIC = 1;
    public static final int TYPE_EDIT = 2;

    //图片类型
    public static final int IMAGE_TYPE_NATIVE = 0;
    public static final int IMAGE_TYPE_HTTP = 1;
    public static final int IMAGE_TYPE_QR = 2;


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getEffectType() {
        return effectType;
    }

    public void setEffectType(int effectType) {
        this.effectType = effectType;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
