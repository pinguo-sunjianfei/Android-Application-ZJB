package com.idrv.coach.bean;

/**
 * time:2016/4/21
 * description:
 *
 * @author sunjianfei
 */
public class ESite {
    // 是否有新消息
    boolean hasNewMessage;
    // 今日是否已分享资讯
    boolean todayShared;
    // 学员风采照片数量
    int studentPictureCount;
    // 教练风采照片数量
    int coachPictureCount;
    // 已添加业务数量
    int business;
    // 今日访问量
    int todayVisits;
    // 今日转发量
    int todayShares;
    // 累计访问量
    int totalVisits;
    // 累计转发量
    int totalShares;

    public boolean isHasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }

    public boolean isTodayShared() {
        return todayShared;
    }

    public void setTodayShared(boolean todayShared) {
        this.todayShared = todayShared;
    }

    public int getStudentPictureCount() {
        return studentPictureCount;
    }

    public void setStudentPictureCount(int studentPictureCount) {
        this.studentPictureCount = studentPictureCount;
    }

    public int getCoachPictureCount() {
        return coachPictureCount;
    }

    public void setCoachPictureCount(int coachPictureCount) {
        this.coachPictureCount = coachPictureCount;
    }

    public int getBusiness() {
        return business;
    }

    public void setBusiness(int business) {
        this.business = business;
    }

    public int getTodayVisits() {
        return todayVisits;
    }

    public void setTodayVisits(int todayVisits) {
        this.todayVisits = todayVisits;
    }

    public int getTodayShares() {
        return todayShares;
    }

    public void setTodayShares(int todayShares) {
        this.todayShares = todayShares;
    }

    public int getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(int totalVisits) {
        this.totalVisits = totalVisits;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }
}
