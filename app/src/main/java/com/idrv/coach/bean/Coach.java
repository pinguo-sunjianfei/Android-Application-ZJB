package com.idrv.coach.bean;

/**
 * time: 2016/3/2
 * description:
 * <p>
 * 教练信息
 *
 * @author bigflower
 */
public class Coach {
    //还未提交过申请
    public static final int STATE_DEFAULT = 0;
    //正在审核中
    public static final int STATE_APPLY = 1;
    //审核通过,已经认证
    public static final int STATE_AUTH_SUCCESS = 2;
    //审核失败
    public static final int STATE_AUTH_FAILED = 3;

    /**
     * drivingSchool : 长征驾校
     * trainingSite : 长征驾校
     * testSite : 长征
     * teachingDeclaration : http://im.vodio.6675668 教学宣言
     * coachingBadge : http://img.habha.66668 教练证
     * qq : 1456674885
     * qrCode : http://im.vodio.6675668
     * coachingDate : 2001-7-0
     */

    private String drivingSchool;
    private String trainingSite;
    private String testSite;
    private String teachingDeclaration;
    private String qq;
    private String qrCode;
    private String coachingDate;
    private int authenticationState;

    public void setDrivingSchool(String drivingSchool) {
        this.drivingSchool = drivingSchool;
    }

    public void setTrainingSite(String trainingSite) {
        this.trainingSite = trainingSite;
    }

    public void setTestSite(String testSite) {
        this.testSite = testSite;
    }

    public void setTeachingDeclaration(String teachingDeclaration) {
        this.teachingDeclaration = teachingDeclaration;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setCoachingDate(String coachingDate) {
        this.coachingDate = coachingDate;
    }

    public String getDrivingSchool() {
        return drivingSchool;
    }

    public String getTrainingSite() {
        return trainingSite;
    }

    public String getTestSite() {
        return testSite;
    }

    public String getTeachingDeclaration() {
        return teachingDeclaration;
    }

    public String getQq() {
        return qq;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getCoachingDate() {
        return coachingDate;
    }

    public int getAuthenticationState() {
        return authenticationState;
    }

    public void setAuthenticationState(int authenticationState) {
        this.authenticationState = authenticationState;
    }

    /**
     * 测试用数据
     */
    public Coach(String drivingSchool, String trainingSite, String testSite, String teachingDeclaration, String coachingBadge, String qq, String qrCode, String coachingDate) {
        this.drivingSchool = drivingSchool;
        this.trainingSite = trainingSite;
        this.testSite = testSite;
        this.teachingDeclaration = teachingDeclaration;
        this.qq = qq;
        this.qrCode = qrCode;
        this.coachingDate = coachingDate;
    }

    public Coach() {

    }
}
