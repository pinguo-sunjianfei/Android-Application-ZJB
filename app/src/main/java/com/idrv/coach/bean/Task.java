package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/1/25.
 */
public class Task implements Parcelable {


    /**
     * id : 41
     * taskName : 2016年必须学车的9个理由
     * describe : 接任务，将其分享给您的学员，看哪些学员在跟您互动，哪些学员为您点灯，哪些学员帮您传播。彰显您的专业性之外，能增进您与学员的感情，还能大大提升您的影响力哦。学员分享给他的朋友，您的教练信息还能被更多人看见，提高学员的转介绍。
     * taskType : 1
     * taskImgUrl : http://7xq9f0.com2.z0.glb.qiniucdn.com/3.4%E4%BB%BB%E5%8A%A1%E9%85%8D%E5%9B%BE.jpg
     * shareTitle : 随便哪个拎出来，我都抗拒不了啊~
     * shareIcon : http://7xq9f0.com2.z0.glb.qiniucdn.com/3.4%E5%88%86%E4%BA%AB.jpg
     * shareDescribe : 2016年必须学车的9个理由
     * shelvesAt : 2016-03-04 07:50:00.0
     * deadTime : 2016-03-05 07:49:59.0
     * created : 2016-02-29 16:58:58.0
     * updated : 2016-02-29 16:59:01.0
     * detailUrl : http://1.jpg
     */

    private int id;
    private String taskName;
    private String describe;
    private String taskType;
    private String taskImgUrl;
    private String shareTitle;
    private String shareIcon;
    private String shareDescribe;
    private String shelvesAt;
    private String deadTime;
    private String created;
    private String updated;
    private String detailUrl;
    private String shareUrl;

    public void setId(int id) {
        this.id = id;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public void setTaskImgUrl(String taskImgUrl) {
        this.taskImgUrl = taskImgUrl;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public void setShareIcon(String shareIcon) {
        this.shareIcon = shareIcon;
    }

    public void setShareDescribe(String shareDescribe) {
        this.shareDescribe = shareDescribe;
    }

    public void setShelvesAt(String shelvesAt) {
        this.shelvesAt = shelvesAt;
    }

    public void setDeadTime(String deadTime) {
        this.deadTime = deadTime;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public int getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescribe() {
        return describe;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getTaskImgUrl() {
        return taskImgUrl;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public String getShareIcon() {
        return shareIcon;
    }

    public String getShareDescribe() {
        return shareDescribe;
    }

    public String getShelvesAt() {
        return shelvesAt;
    }

    public String getDeadTime() {
        return deadTime;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public Task() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.taskName);
        dest.writeString(this.describe);
        dest.writeString(this.taskType);
        dest.writeString(this.taskImgUrl);
        dest.writeString(this.shareTitle);
        dest.writeString(this.shareIcon);
        dest.writeString(this.shareDescribe);
        dest.writeString(this.shelvesAt);
        dest.writeString(this.deadTime);
        dest.writeString(this.created);
        dest.writeString(this.updated);
        dest.writeString(this.detailUrl);
        dest.writeString(this.shareUrl);
    }

    protected Task(Parcel in) {
        this.id = in.readInt();
        this.taskName = in.readString();
        this.describe = in.readString();
        this.taskType = in.readString();
        this.taskImgUrl = in.readString();
        this.shareTitle = in.readString();
        this.shareIcon = in.readString();
        this.shareDescribe = in.readString();
        this.shelvesAt = in.readString();
        this.deadTime = in.readString();
        this.created = in.readString();
        this.updated = in.readString();
        this.detailUrl = in.readString();
        this.shareUrl = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
