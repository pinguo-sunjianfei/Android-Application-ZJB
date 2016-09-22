package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.idrv.coach.bean.DrivingTestIns;
import com.idrv.coach.bean.DrivingTestInsDetail;
import com.idrv.coach.bean.DrivingTestInsurance;
import com.idrv.coach.bean.User;
import com.idrv.coach.bean.parser.DrivingTestInsDetailParser;
import com.idrv.coach.bean.parser.DrivingTestInsParser;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
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
import rx.functions.Action0;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/3/29
 * description:
 *
 * @author sunjianfei
 */
public class DrivingTestInsModel extends BaseModel {
    private DrivingTestIns data;
    int currentPage = 0;

    public DrivingTestInsModel() {
        data = new DrivingTestIns();
    }

    public DrivingTestIns getData() {
        return data;
    }

    public Observable<String> commit() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_DRIVING_TEST_INS)
                .put("coachId", LoginManager.getInstance().getUid())
                .put("name", data.getName())
                .put("idCard", data.getIdCard())
                .put("phone", data.getCoachPhone())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public boolean checkUserInfoInValid() {
        String userJson = PreferenceUtil.getString(SPConstant.KEY_USER);
        User mUser;
        try {
            if (!TextUtils.isEmpty(userJson)) {
                mUser = GsonUtil.fromJson(userJson, User.class);

                String tel = mUser.getPhone();

                if (!TextUtils.isEmpty(tel)) {
                    return false;
                }
            } else {
                return true;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 学车险下拉刷新
     *
     * @param clearAdapter
     * @return
     */
    public Observable<List<DrivingTestInsurance>> refresh(Action0 clearAdapter) {
        currentPage = 0;
        return request()
                .doOnNext(__ -> clearAdapter.call());
    }

    public Observable<List<DrivingTestInsurance>> loadMore() {
        currentPage++;
        return request()
                .filter(ValidateUtil::isValidate);
    }

    /**
     * 学车险
     *
     * @return
     */
    private Observable<List<DrivingTestInsurance>> request() {
        //1.创建Request
        HttpGsonRequest<List<DrivingTestInsurance>> mRefreshRequest = RequestBuilder.<List<DrivingTestInsurance>>create()
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_DRIVING_TEST_INS_LIST)
                .parser(new DrivingTestInsParser())
                .put("coachId", LoginManager.getInstance().getUid())
                .put("page", currentPage)
                .put("count", 30)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 防爆险详情
     *
     * @return
     */
    public Observable<List<DrivingTestInsDetail>> getInsDetail() {
        //1.创建Request
        HttpGsonRequest<List<DrivingTestInsDetail>> mRefreshRequest = RequestBuilder.<List<DrivingTestInsDetail>>create()
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_DRIVING_INS_DETAIL)
                .parser(new DrivingTestInsDetailParser())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

}
