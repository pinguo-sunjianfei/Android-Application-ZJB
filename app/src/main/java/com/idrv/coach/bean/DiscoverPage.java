package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/8/4
 * description:发现页
 *
 * @author sunjianfei
 */
public class DiscoverPage {
    List<Banner> banner;
    List<DiscoverMainItems> mainItems;
    List<DiscoverItem> itemlist;

    public List<Banner> getBanner() {
        return banner;
    }

    public void setBanner(List<Banner> banner) {
        this.banner = banner;
    }

    public List<DiscoverMainItems> getMainItems() {
        return mainItems;
    }

    public void setMainItems(List<DiscoverMainItems> mainItems) {
        this.mainItems = mainItems;
    }

    public List<DiscoverItem> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<DiscoverItem> itemlist) {
        this.itemlist = itemlist;
    }
}
