package com.idrv.coach.utils;


import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import java.util.HashMap;
import java.util.Map;

public class QiNiuUtil {

    private static QiNiuUtil mInstance;
    private UploadManager uploadManager;

    public QiNiuUtil() {
        uploadManager = new UploadManager();
    }


    public static synchronized QiNiuUtil getInstance() {
        if (mInstance == null)
            mInstance = new QiNiuUtil();
        return mInstance;
    }

    public void qiNiuUpLoad(String token, String key, String filePath,
                            UpCompletionHandler callBack) {
        qiNiuUpLoad(token, key, filePath, callBack, null);
        // 上传
    }

    public void qiNiuUpLoad(String token, String key, String filePath,
                            UpCompletionHandler callBack, UpProgressHandler progressHandler) {
        // 获得
        if (filePath == null || filePath.isEmpty()) {
            Logger.e("upLoad", "地址为空");
            return;
        }
        Map<String, String> map = new HashMap<>();
        // 上传
        uploadManager.put(filePath, key, token,
                callBack, new UploadOptions(map, null, false, progressHandler
                        , null));
    }

}
