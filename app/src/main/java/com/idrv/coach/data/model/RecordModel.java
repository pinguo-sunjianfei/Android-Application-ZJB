package com.idrv.coach.data.model;

import com.idrv.coach.R;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.FileUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.utils.GsonUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time: 2016/3/28
 * description:
 *  录音
 * @author bigflower
 */
public class RecordModel extends UpLoadModel {

    public static final String tempRecrodName = "cacheRecord";
    public static final String downloadRecrodName = "downloadRecord";
    // 录音最长时间，单位 s
    public final int MAX_RECORD_TIME = 60;
    public final int STATE_RECORD = 0;
    public final int STATE_VOICE = 1;
    public final int[] voiceLevels = {R.drawable.img_record_vol1, R.drawable.img_record_vol2,
            R.drawable.img_record_vol3, R.drawable.img_record_vol4, R.drawable.img_record_vol5};

    // 将网络音频下载到本地，此为本地链接
    public String gDownloadRecordPath;
    // 录音到本地的
    public String gRecordLocalTemp = "";
    // 当前使用的路径, 这个是用来判断的
    public String gNowPath;
    // 当前的状态，录音还是播放
    public int gState;


    public RecordModel() {
        // 这里只是要一个路径而已
        gRecordLocalTemp = FileUtil.getPathByType(FileUtil.DIR_TYPE_TEMP) + tempRecrodName;
        // 下载的就存在覆盖，所以本地有责删除之
        gDownloadRecordPath = FileUtil.createPath(FileUtil.DIR_TYPE_TEMP, downloadRecrodName);
    }

    /**
     * 修改教练信息，录音成功后用
     *
     * @return
     */
    public Observable<Coach> putCoachInfo() {
        //1.创建Request
        HttpGsonRequest<Coach> mRefreshRequest = RequestBuilder.create(Coach.class)
                .requestMethod(Request.Method.PUT)
                .url(ApiConstant.API_PUT_COACH_INFO)
                .put("teachingDeclaration", mImgUrl)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(__ -> saveCoachInfo())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 保存教练信息: 录音
     */
    private void saveCoachInfo() {
        try {
            Coach coach = LoginManager.getInstance().getCoach();
            coach.setTeachingDeclaration(mImgUrl);
            // save
            PreferenceUtil.putString(SPConstant.KEY_COACH, GsonUtil.toJson(coach));
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }

    /**
     * 判断是否上传了
     *
     * @return
     */
    public boolean isNotUpload() {
        return gRecordLocalTemp.equals(gNowPath);
    }
}
