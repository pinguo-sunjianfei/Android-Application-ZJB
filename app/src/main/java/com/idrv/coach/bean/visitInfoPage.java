package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/5/23
 * description:
 *
 * @author sunjianfei
 */
public class visitInfoPage {
    int visitNum;
    List<Visitor> visitors;

    public int getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(int visitNum) {
        this.visitNum = visitNum;
    }

    public List<Visitor> getVisitors() {
        return visitors;
    }

    public void setVisitors(List<Visitor> visitors) {
        this.visitors = visitors;
    }
}
