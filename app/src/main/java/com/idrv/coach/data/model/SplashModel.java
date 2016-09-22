package com.idrv.coach.data.model;

import android.graphics.Bitmap;
import android.view.View;

import com.idrv.coach.bean.AdvBean;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.loader.core.listener.ImageLoadingAdapterListener;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.utils.GsonUtil;

import rx.Observable;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/6/21
 * description:
 *
 * @author sunjianfei
 */
public class SplashModel {

    public void getSplashData() {
        request().map(AdvBean::getImageUrl)
                .filter(ValidateUtil::isValidate)
                .doOnNext(url -> {
                    ZjbImageLoader.create(url)
                            .setImageLoadinglistener(listener)
                            .load();
                }).subscribe(Logger::e, Logger::e);
    }


    /**
     * 刷新网络数据
     */
    private Observable<AdvBean> request() {
        //1.创建Request
        HttpGsonRequest<AdvBean> mRefreshRequest = RequestBuilder.create(AdvBean.class)
                .url(ApiConstant.API_SPLASH_ADV)
                .put("type",0)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(resp -> {
                    AdvBean bean = resp.getData();
                    if (null == bean) {
                        //如果为空,则清除缓存广告
                        PreferenceUtil.remove(SPConstant.KEY_SPLASH);
                    } else {
                        PreferenceUtil.putString(SPConstant.KEY_SPLASH, GsonUtil.toJson(bean));
                    }
                    return bean;
                });

    }

    ImageLoadingAdapterListener listener = new ImageLoadingAdapterListener() {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            PreferenceUtil.putString(SPConstant.KEY_SPLASH_PIC, imageUri);
        }
    };

}
