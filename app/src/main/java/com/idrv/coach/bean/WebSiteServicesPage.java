package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/4/21
 * description:
 *
 * @author sunjianfei
 */
public class WebSiteServicesPage {
    List<Services> coachBusiness;
    List<Services> ableBusiness;

    public List<Services> getAbleBusiness() {
        return ableBusiness;
    }

    public void setAbleBusiness(List<Services> ableBusiness) {
        this.ableBusiness = ableBusiness;
    }

    public List<Services> getCoachBusiness() {
        return coachBusiness;
    }

    public void setCoachBusiness(List<Services> coachBusiness) {
        this.coachBusiness = coachBusiness;
    }
}
