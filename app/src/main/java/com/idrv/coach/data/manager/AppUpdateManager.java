package com.idrv.coach.data.manager;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.idrv.coach.BuildConfig;
import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.CheckUpdate;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.utils.FileUtil;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.NotifyHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.download.DownloadResult;
import com.zjb.volley.download.DownloadTask;
import com.zjb.volley.download.IDownloadListener;

import java.io.File;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.ZjbApplication.gContext;
import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/7/27
 * description:App版本更新管理类
 *
 * @author sunjianfei
 */
public class AppUpdateManager {
    /* apk升级包下载的监听*/
    private IDownloadListener mDownloadListener;
    private Dialog mProgressDialog;

    {
        this.mDownloadListener = new IDownloadListener() {
            @Override
            public void onDownloadStarted() {
            }

            @Override
            public void onDownloadFinished(DownloadResult downloadResult) {
                //1.停止通知
                NotifyHelper.notifyProgress(gContext, 0, 0);
                //2.安装文件
                String path = downloadResult.path;
                if (!TextUtils.isEmpty(path)) {
                    File file = new File(path);
                    install(file);
                }
            }

            @Override
            public void onProgressUpdate(Float... floats) {
                NotifyHelper.notifyProgress(gContext, floats[0], floats[1]);
            }
        };
    }

    public static AppUpdateManager newInstance() {
        return new AppUpdateManager();
    }

    /**
     * 安装下载下来的APK文件
     *
     * @param file 下载的apk文件
     */
    private void install(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        gContext.startActivity(intent);
    }

    /**
     * 自动检测升级
     */
    private Observable<CheckUpdate> updateRequest() {
        //1.构建一个请求
        HttpGsonRequest<CheckUpdate> mUpdateRequest = RequestBuilder.create(CheckUpdate.class)
                .url(ApiConstant.API_CHECK_UPDATE)
                .put("versionCode", BuildConfig.VERSION_CODE)
                .build();
        //2.进行请求
        return gRequestPool.request(mUpdateRequest)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 下载新的安装包
     *
     * @param url apk对应的链接
     */
    private void downloadApk(String url, IDownloadListener downloadListener) {
        if (!TextUtils.isEmpty(url)) {
            DownloadTask task = new DownloadTask(ZjbApplication.gContext,
                    url, FileUtil.getApkPath(), downloadListener);
            task.execute();
        }
    }

    /**
     * 检查版本更新
     *
     * @param isBlock 是否阻塞
     */
    public void checkUpdate(boolean isBlock) {
        if (isBlock) {
            showProgressDialog(R.string.check_update_now);
        }
        updateRequest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(checkUpdate -> onCheckUpdateNext(checkUpdate, isBlock), e -> dismissProgressDialog());

    }

    private void onCheckUpdateNext(final CheckUpdate checkUpdate, boolean isBackground) {
        try {
            if (null == checkUpdate || TextUtils.isEmpty(checkUpdate.getUrl())) {
                //如果没有新版本
                if (isBackground) {
                    UIHelper.shortToast(R.string.already_newest_version);
                }
                return;
            }
            long lastDetectTime = PreferenceUtil.getLong(SPConstant.KEY_LAST_CHECK_UPDATE);
//            if ((System.currentTimeMillis() - lastDetectTime) > 2 * 24 * 60 * 60 * 1000L) {
//                //TODO 先保留着,间隔一天弹一次
//            }
            //先关闭加载圈
            dismissProgressDialog();
            //显示升级对话框
            showCheckUpdateDialog(checkUpdate);
            PreferenceUtil.putLong(SPConstant.KEY_LAST_CHECK_UPDATE, System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCheckUpdateDialog(final CheckUpdate checkUpdate) {
        DialogHelper dialogHelper = DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .title(gContext.getString(R.string.check_update))
                .content(TextUtils.isEmpty(checkUpdate.getDescription()) ?
                        gContext.getString(R.string.check_update_default_content) : checkUpdate.getDescription());
        if (!checkUpdate.isUpgrade()) {
            dialogHelper.leftButton(gContext.getString(R.string.dialog_cancel), 0x89000000)
                    .cancelable(true)
                    .canceledOnTouchOutside(true)
                    .rightButton(gContext.getString(R.string.download), ContextCompat.getColor(gContext, R.color.themes_main))
                    .leftBtnClickListener((dialog, view) -> dialog.dismiss())
                    .rightBtnClickListener((dialog, view) -> {
                        dialog.dismiss();
                        UIHelper.shortToast(R.string.down_load_now);
                        downloadApk(checkUpdate.getUrl(), mDownloadListener);
                    })
                    .show();
        } else {
            dialogHelper.bottomButton(gContext.getString(R.string.download), ContextCompat.getColor(gContext, R.color.themes_main))
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .bottomBtnClickListener((dialog, view) -> {
                        dialog.dismiss();
                        downloadApk(checkUpdate.getUrl(), mDownloadListener);
                    })
                    .show();
        }
    }

    /**
     * 显示progress
     */
    private void showProgressDialog(int resId) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .progressText(gContext.getString(resId))
                .show();
    }

    /**
     * Dismiss progress dialog.
     */
    private void dismissProgressDialog() {
        if (isProgressDialogShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * Is progress dialog showing.
     *
     * @return
     */
    private boolean isProgressDialogShowing() {
        return null != mProgressDialog && mProgressDialog.isShowing();
    }
}
