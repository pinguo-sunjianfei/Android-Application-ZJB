package com.idrv.coach.data.model;

import com.idrv.coach.bean.City;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.Location;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.db.TDSchool;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.utils.GsonUtil;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/5/25
 * description:
 *
 * @author sunjianfei
 */
public class SchoolModel {
    Location location;
    Coach mCoach;

    public SchoolModel() {
        mCoach = LoginManager.getInstance().getCoach();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean hasLocation() {
        return location != null;
    }

    /**
     * 从数据库查询城市列表
     *
     * @return
     */
    public Observable<List<City>> getCityList() {
        return TDSchool.getCities()
                .filter(ValidateUtil::isValidate)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 根据城市id，查询对应的驾校
     */
    public Observable<List<String>> getSchoolList(String cid) {
        return TDSchool.getSchools(cid)
                .filter(ValidateUtil::isValidate)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改教练驾校
     *
     * @param school
     * @return
     */
    public Observable<Coach> modifyCoachInfo(String school) {
        //1.创建Request
        HttpGsonRequest<Coach> mRefreshRequest = RequestBuilder.create(Coach.class)
                .requestMethod(Request.Method.PUT)
                .url(ApiConstant.API_PUT_COACH_INFO)
                .put("drivingSchool", school)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(__ -> saveCoachInfo(school))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public String getOldSchool() {
        return mCoach.getDrivingSchool();
    }

    private void saveCoachInfo(String school) {
        try {
            mCoach.setDrivingSchool(school);
            LoginManager.getInstance().setCoach(mCoach);
            PreferenceUtil.putString(SPConstant.KEY_COACH, GsonUtil.toJson(mCoach));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
