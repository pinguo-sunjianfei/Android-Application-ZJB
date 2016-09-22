package com.idrv.coach.data.model;

import com.google.gson.reflect.TypeToken;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.FileUtil;
import com.idrv.coach.utils.PreferenceUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time: 2016/3/14
 * description: 驾校选择的model
 *      初始化：获得教练信息，并从assets中获得驾校名称的数据
 *
 * @author bigflower
 */
public class DriveSchoolModel extends BaseModel {

    public Coach gCoach;
    // 缓存的教练之前选择的驾校
    public String gSchName = "";
    // 总数据
    public List<String> gSchNames = new ArrayList<>();
    // 显示的数据
    public List<String> gShowList = new ArrayList<>();

    public DriveSchoolModel() {
        gCoach = LoginManager.getInstance().getCoach();
        gSchName = gCoach.getDrivingSchool();

        // 获得驾校
        initSchoolNames();
    }

    /**
     * 从assets中获取数据，存到 List 中
     */
    private void initSchoolNames() {
        String driveSchStr = FileUtil.getTextFromAssets(ZjbApplication.gContext, "voice.json");
        gSchNames = GsonUtil.fromJson(driveSchStr, new TypeToken<List<String>>() {
        }.getType());
        gShowList.addAll(gSchNames);
    }

    /**
     * 修改教练信息，http
     *
     * @param value
     * @return
     */
    public Observable<Coach> putCoachInfo(String value) {
        //1.创建Request
        HttpGsonRequest<Coach> mRefreshRequest = RequestBuilder.create(Coach.class)
                .requestMethod(Request.Method.PUT)
                .url(ApiConstant.API_PUT_COACH_INFO)
                .put("drivingSchool", value)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(__ -> {
                    saveCoachInfo(value);
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 保存教练信息
     *
     * @param value
     */
    private void saveCoachInfo(String value) {
        gCoach.setDrivingSchool(value);
        PreferenceUtil.putString(SPConstant.KEY_COACH, GsonUtil.toJson(gCoach));
    }

}
