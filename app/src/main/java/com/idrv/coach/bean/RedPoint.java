package com.idrv.coach.bean;

/**
 * time:2016/3/25
 * description:记录小红点的显示
 *
 * @author sunjianfei
 */
public class RedPoint {
    //动态
    private int dynamicStatus;
    private String headimgurl;
    //业务
    private int businessStatus;
    //资讯
    private int newsStatus;
    //福利
    private int welfareStatus;

    public int getDynamicStatus() {
        return dynamicStatus;
    }

    public void setDynamicStatus(int dynamicStatus) {
        this.dynamicStatus = dynamicStatus;
    }

    public int getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(int businessStatus) {
        this.businessStatus = businessStatus;
    }

    public int getNewsStatus() {
        return newsStatus;
    }

    public void setNewsStatus(int newsStatus) {
        this.newsStatus = newsStatus;
    }

    public int getWelfareStatus() {
        return welfareStatus;
    }

    public void setWelfareStatus(int welfareStatus) {
        this.welfareStatus = welfareStatus;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }
}
