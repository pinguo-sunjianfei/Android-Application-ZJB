package com.zjb.loader.core.listener;

import android.graphics.Bitmap;
import android.view.View;

import com.zjb.loader.internal.core.assist.FailReason;
import com.zjb.loader.internal.core.listener.ImageLoadingListener;


/**
 * Created by xiechaojun on 15-9-17.
 * description:解决直接实现ImageLoadingListener会空出很多不需要实现的方法
 *
 * @author crab
 */
public class ImageLoadingAdapterListener implements ImageLoadingListener {
    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }
}
