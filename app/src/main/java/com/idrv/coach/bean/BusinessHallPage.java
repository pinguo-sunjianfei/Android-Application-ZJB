package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/3/17
 * description:任务大厅
 *
 * @author sunjianfei
 */
public class BusinessHallPage {
    int todayCount;
    List<BusinessHall> boxes;

    public int getTodayCount() {
        return todayCount;
    }

    public void setTodayCount(int todayCount) {
        this.todayCount = todayCount;
    }

    public List<BusinessHall> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<BusinessHall> boxes) {
        this.boxes = boxes;
    }
}
