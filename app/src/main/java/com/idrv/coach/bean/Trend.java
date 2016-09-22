package com.idrv.coach.bean;

import com.idrv.coach.utils.ValidateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/3/28
 * description:动态item
 *
 * @author sunjianfei
 */
public class Trend {
    String coachId;
    String coachNickName;
    String coachHeadimgurl;
    String studentNickName;
    String studentHeadimgurl;
    String taskName;
    String taskImgUrl;
    String taskId;
    String teamName;
    String impact;
    String time;
    boolean isPraised;
    String targetId;
    int visitSum;
    int praiseSum;
    List<String> avators;
    List<String> nickNames;
    //type=0:学员与教练的互动 type=1:教练入团动态 type=2:教练动态 第一种情况(教练还未加入团队)
    int type;
    //action=0 学员访问 action=1 学员点赞 action=2 学员转发
    int action;
    boolean hasQrCode;
    String content;
    String studentPhone;


    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public String getCoachNickName() {
        return coachNickName;
    }

    public void setCoachNickName(String coachNickName) {
        this.coachNickName = coachNickName;
    }

    public String getCoachHeadimgurl() {
        return coachHeadimgurl;
    }

    public void setCoachHeadimgurl(String coachHeadimgurl) {
        this.coachHeadimgurl = coachHeadimgurl;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getAvators() {
        return avators;
    }

    public void setAvators(List<String> avators) {
        this.avators = avators;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public List<String> getNickNames() {
        return nickNames;
    }

    public void setNickNames(List<String> nickNames) {
        this.nickNames = nickNames;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskImgUrl() {
        return taskImgUrl;
    }

    public void setTaskImgUrl(String taskImgUrl) {
        this.taskImgUrl = taskImgUrl;
    }

    public String getStudentNickName() {
        return studentNickName;
    }

    public void setStudentNickName(String studentNickName) {
        this.studentNickName = studentNickName;
    }

    public String getStudentHeadimgurl() {
        return studentHeadimgurl;
    }

    public void setStudentHeadimgurl(String studentHeadimgurl) {
        this.studentHeadimgurl = studentHeadimgurl;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public void setIsPraised(boolean isPraised) {
        this.isPraised = isPraised;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getVisitSum() {
        return visitSum;
    }

    public void setVisitSum(int visitSum) {
        this.visitSum = visitSum;
    }

    public void addPraiseSum() {
        this.praiseSum += 1;
    }

    public boolean isHasQrCode() {
        return hasQrCode;
    }

    public void setHasQrCode(boolean hasQrCode) {
        this.hasQrCode = hasQrCode;
    }

    public int getPraiseSum() {
        return praiseSum;
    }

    public void setPraiseSum(int praiseSum) {
        this.praiseSum = praiseSum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getNameStr() {
        if (ValidateUtil.isValidate(nickNames)) {
            StringBuilder builder = new StringBuilder();
            int size = nickNames.size() > 6 ? 6 : nickNames.size();
            for (int i = 0; i < size; i++) {
                if (i == 0) {
                    builder.append("  ");
                }
                String name = nickNames.get(i);
                builder.append(name);
                if (i != size - 1) {
                    builder.append("，");
                }
            }
            return builder.toString();
        }
        return "";
    }

    public void addLikeName(String nickName) {
        if (null == nickNames) {
            nickNames = new ArrayList<>();
        }
        nickNames.add(0, nickName);
    }
}
