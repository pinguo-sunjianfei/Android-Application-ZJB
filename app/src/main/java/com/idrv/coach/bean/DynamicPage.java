package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/3/28
 * description:动态页面
 *
 * @author sunjianfei
 */
public class DynamicPage {
    int praiseSum;
    //团队邀请
    TeamInvite teamInvite;
    //点赞头像
    List<String> praiserAvators;
    //item
    List<Trend> trends;

    public int getPraiseSum() {
        return praiseSum;
    }

    public void setPraiseSum(int praiseSum) {
        this.praiseSum = praiseSum;
    }

    public TeamInvite getTeamInvite() {
        return teamInvite;
    }

    public void setTeamInvite(TeamInvite teamInvite) {
        this.teamInvite = teamInvite;
    }

    public List<String> getPraiserAvators() {
        return praiserAvators;
    }

    public void setPraiserAvators(List<String> praiserAvators) {
        this.praiserAvators = praiserAvators;
    }

    public List<Trend> getTrends() {
        return trends;
    }

    public void setTrends(List<Trend> trends) {
        this.trends = trends;
    }
}
