package com.idrv.coach.bean;

/**
 * time:2016/5/20
 * description:钱包明细
 *
 * @author sunjianfei
 */
public class PurseDetails {
    String uid;
    String bid;
    float inAccount;
    String category;
    int type;
    String studentPhone;
    String studentName;
    String created;
    boolean isGroup;
    String groupName;
    String label;
    String icon;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getInAccount() {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        String result = df.format(inAccount);
        if (inAccount > 0) {
            result = "+" + result;
        }
        return result;
    }

    public void setInAccount(float inAccount) {
        this.inAccount = inAccount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
