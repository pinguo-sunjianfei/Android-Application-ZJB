package com.idrv.coach.data.manager;

import android.os.Handler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.Location;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.zjb.volley.utils.GsonUtil;

import rx.Observable;

/**
 * time:2016/3/25
 * description:高德地图定位
 *
 * @author sunjianfei
 */
public class LocationManager implements AMapLocationListener, Runnable {
    private static final String TAG = LocationManager.class.getSimpleName();
    private boolean isWriteData = false;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //用于判断定位超时
    private Handler handler = new Handler();

    private static LocationManager instance = new LocationManager();

    public static LocationManager getInstance() {
        return instance;
    }

    public void init() {
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
    }

    public void startLocation() {
        //如果正在定位,则只需注册即可
        if (null != mLocationClient) {
            return;
        }
        handler.removeCallbacks(this);
        //初始化定位
        mLocationClient = new AMapLocationClient(ZjbApplication.gContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                Logger.e("经度 = " + amapLocation.getLongitude() + "\n" + "纬度 = " + amapLocation.getLatitude());
                if (!isWriteData) {
                    writeData(amapLocation);
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Logger.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
            RxBusManager.post(EventConstant.KEY_LOCATION, getLocationBean(amapLocation));
        }
    }

    @Override
    public void run() {
        Logger.e(TAG, " get location failed , stop ");
        stopLocation();// 销毁掉定位
    }

    private void stopLocation() {
        if (null != mLocationOption) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    private Location getLocationBean(AMapLocation aMapLocation) {
        Location location = new Location();
        location.setProvince(aMapLocation.getProvince());
        location.setCity(aMapLocation.getCity());
        location.setCode(aMapLocation.getErrorCode());
        return location;
    }

    /**
     * 订阅之后会返回地理位置信息
     *
     * @param object
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> registerLocation(Object object, Class<T> clazz) {
        Observable<T> observable = RxBusManager.register(object, EventConstant.KEY_LOCATION, clazz);
        startLocation();
        return observable;
    }

    private void writeData(AMapLocation aMapLocation) {
        isWriteData = true;
        String lon = String.valueOf(aMapLocation.getLongitude());
        String lat = String.valueOf(aMapLocation.getLatitude());

        PreferenceUtil.putLong(SPConstant.KEY_LOCATION_TIME, System.currentTimeMillis());
        //存放经纬度
        PreferenceUtil.putString(SPConstant.KEY_LON, lon);
        PreferenceUtil.putString(SPConstant.KEY_LAT, lat);
        //存放其他位置信息
        String locationStr = aMapLocation.toStr();
        PreferenceUtil.putString(SPConstant.KEY_LOCATION, locationStr);
        AppInitManager.setLocation(GsonUtil.fromJson(locationStr, Location.class));


        AppInitManager.getSdkEntity().setLongtitude(lon);
        AppInitManager.getSdkEntity().setLatitude(lat);
        isWriteData = false;
    }
}
