package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.idrv.coach.BuildConfig;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.CheckUpdate;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.utils.FileUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.download.DownloadTask;
import com.zjb.volley.download.IDownloadListener;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/4/5
 * description:
 *
 * @author sunjianfei
 */
public class MainModel {
    public static final String TAG = MainModel.class.getSimpleName();

    /**
     * 自动检测升级
     */
    public Observable<CheckUpdate> checkUpdate() {
        //1.构建一个请求
        HttpGsonRequest<CheckUpdate> mUpdateRequest = RequestBuilder.create(CheckUpdate.class)
                .url(ApiConstant.API_CHECK_UPDATE)
                .put("versionCode", BuildConfig.VERSION_CODE)
                .build();
        //2.进行请求
        return gRequestPool.request(mUpdateRequest)
                .filter(resp -> resp != null
                        && null != resp.data
                        && !TextUtils.isEmpty(resp.data.getUrl()))
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 下载新的安装包
     *
     * @param url apk对应的链接
     */
    public void downloadApk(String url, IDownloadListener downloadListener) {
        if (!TextUtils.isEmpty(url)) {
            DownloadTask task = new DownloadTask(ZjbApplication.gContext,
                    url, FileUtil.getApkPath(), downloadListener);
            task.execute();
        }
    }
}
