package com.idrv.coach.data.manager;

import com.idrv.coach.bean.Location;
import com.idrv.coach.bean.ReportLogin;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.db.TDReportLogin;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/3/29
 * description:上报登录
 *
 * @author sunjianfei
 */
public class ReportLoginManager {
    private static ReportLoginManager instance = new ReportLoginManager();

    public static ReportLoginManager getInstance() {
        return instance;
    }

    public void report() {
        TDReportLogin.getReportLogin()
                .subscribe(this::reportLogin, Logger::e);
    }

    private ReportLogin buildReport() {
        ReportLogin login = new ReportLogin();
        login.setHost(ApiConstant.API_REPORT_LOGIN);
        login.setLongtitude(PreferenceUtil.getString(SPConstant.KEY_LON));
        login.setLatitude(PreferenceUtil.getString(SPConstant.KEY_LAT));
        login.setCreated(TimeUtil.getCurrentTimeStr());
        login.setIsNewData(true);

        Location location = AppInitManager.getLocation();
        login.setCountry(location.getCountry());
        login.setProvince(location.getProvince());
        login.setCity(location.getCity());
        login.setCityCode(location.getCityCode());
        login.setStreet(location.getStreet());
        login.setStreetNum(location.getStreetNum());
        login.setAoiName(location.getAoiName());
        login.setDistrict(location.getDistrict());
        login.setAdCode(location.getAdCode());
        return login;
    }

    private void reportLogin(List<ReportLogin> list) {
        if (!ValidateUtil.isValidate(list)) {
            list = new ArrayList<>();
        }
        list.add(buildReport());
        Logger.e("开始上报登录");
        for (ReportLogin login : list) {
            request(login).subscribe(__ -> TDReportLogin.deleteSync(login), __ -> TDReportLogin.insertSync(login));
        }
    }

    private Observable<String> request(ReportLogin login) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(login.getHost())
                .put("latitude", login.getLatitude())
                .put("longtitude", login.getLongtitude())
                .put("address", login.getAddress())
                .put("created", login.getCreated())
                .put("country", login.getCountry())
                .put("province", login.getProvince())
                .put("city", login.getCity())
                .put("district", login.getDistrict())
                .put("street", login.getStreet())
                .put("streetNum", login.getStreetNum())
                .put("cityCode", login.getCityCode())
                .put("adCode", login.getAdCode())
                .put("aoiName", login.getAoiName())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp)
                .map(HttpResponse::getData);
    }
}
