package com.idrv.coach.bean;

/**
 * time:2016/3/23
 * description:保单信息
 *
 * @author sunjianfei
 */
public class InsuranceInfo {
    String uid;
    String name;
    String phone;
    // 1-新保险 2-续保
    int type;
    //0-审核中 1-已报价 2-已缴费 3-已出单 4-已失效
    int status;
    IsPrice isPrice;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public IsPrice getIsPrice() {
        return isPrice;
    }

    public void setIsPrice(IsPrice isPrice) {
        this.isPrice = isPrice;
    }
}
