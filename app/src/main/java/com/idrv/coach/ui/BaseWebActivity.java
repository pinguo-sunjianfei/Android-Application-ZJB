package com.idrv.coach.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;

import com.idrv.coach.R;

import butterknife.ButterKnife;

/**
 * time:2016/3/15
 * description:普通的H5继承此类
 *
 * @author sunjianfei
 */
public abstract class BaseWebActivity<ViewModel> extends AbsWebActivity<ViewModel> {
    public final static int FILE_CHOOSER_RESULTCODE = 1;
    public final static int FILE_CHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_h5);
        ButterKnife.inject(this);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        ValueCallback<Uri> mUploadMessage = mWebView.getUploadMessage();
        ValueCallback<Uri[]> mUploadMessageForAndroid5 = mWebView.getUploadMessageForAndroid5();
        if (requestCode == FILE_CHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = null == intent || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);

        } else if (requestCode == FILE_CHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
        }
    }
}
