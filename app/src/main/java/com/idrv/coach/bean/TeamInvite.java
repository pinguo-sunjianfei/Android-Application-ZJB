package com.idrv.coach.bean;

/**
 * time:2016/3/28
 * description:团队邀请
 *
 * @author sunjianfei
 */
public class TeamInvite {
    String ownerName;
    String teamName;
    String teamId;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
}
