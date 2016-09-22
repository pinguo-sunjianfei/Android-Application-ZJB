package com.idrv.coach.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.idrv.coach.R;
import com.idrv.coach.ui.BaseWebActivity;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.helper.DialogHelper;

/**
 * time:15-10-22
 * description:
 *
 * @author sunjianfei
 */
public class ProgressWebView extends BridgeWebView {
    private ProgressBar mProgressbar;

    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWebChromeClient(new WebChromeClient());
    }

    public void setProgressbar(ProgressBar progressbar) {
        this.mProgressbar = progressbar;
    }

    public ValueCallback<Uri> getUploadMessage() {
        return mUploadMessage;
    }

    public ValueCallback<Uri[]> getUploadMessageForAndroid5() {
        return mUploadMessageForAndroid5;
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressbar.setVisibility(GONE);
            } else {
                if (mProgressbar.getVisibility() == GONE)
                    mProgressbar.setVisibility(VISIBLE);
                mProgressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }


        //扩展浏览器上传文件
        //3.0++版本
        @SuppressWarnings("static-access")
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            Logger.e("3.0++");
            openFileChooserImpl(uploadMsg);
        }

        //3.0--版本
        @SuppressWarnings("static-access")
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            Logger.e("3.0");
            openFileChooserImpl(uploadMsg);
        }

        @SuppressWarnings("static-access")
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooserImpl(uploadMsg);
        }

        // For Android > 5.0
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, android.webkit.WebChromeClient.FileChooserParams fileChooserParams) {
            Logger.e(">5.0");
            openFileChooserImplForAndroid5(uploadMsg);
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            DialogHelper.create(DialogHelper.TYPE_NORMAL)
                    .title(getResources().getString(R.string.web_title_default))
                    .content(message)
                    .bottomButton(getResources().getString(R.string.sure), getResources().getColor(R.color.black_54))
                    .onDismissListener(dg -> result.confirm())
                    .bottomBtnClickListener((dialog, v) -> dialog.dismiss()).show();
            return true;
        }
    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Activity activity = (Activity) getContext();
        activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), BaseWebActivity.FILE_CHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        Activity activity = (Activity) getContext();
        activity.startActivityForResult(chooserIntent, BaseWebActivity.FILE_CHOOSER_RESULTCODE_FOR_ANDROID_5);
    }


}
