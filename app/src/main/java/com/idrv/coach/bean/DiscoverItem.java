package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/3/31
 * description:发现item的数据
 *
 * @author sunjianfei
 */
public class DiscoverItem {
    String label;
    List<DiscoverMainItems> items;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<DiscoverMainItems> getItems() {
        return items;
    }

    public void setItems(List<DiscoverMainItems> items) {
        this.items = items;
    }
}
