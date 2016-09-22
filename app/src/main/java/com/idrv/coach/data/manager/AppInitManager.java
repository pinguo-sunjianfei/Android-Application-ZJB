package com.idrv.coach.data.manager;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.idrv.coach.BuildConfig;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.BaseParameterGenerator;
import com.idrv.coach.bean.Location;
import com.idrv.coach.bean.SdkEntity;
import com.idrv.coach.bean.User;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.pool.RequestPool;
import com.idrv.coach.utils.DebugUtil;
import com.idrv.coach.utils.FileUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.SystemUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.tendcloud.tenddata.TCAgent;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.loader.internal.utils.L;
import com.zjb.volley.Volley;
import com.zjb.volley.utils.GsonUtil;

/**
 * Created by sunjianfei on 16-2-26.
 * 初始化基本参数
 */
public class AppInitManager {
    private static AppInitManager sInstance;
    private static SdkEntity mSdkEntity = new SdkEntity();
    private static Location mLocation = new Location();

    //是否初始化
    private boolean mIsInitialized;

    public static AppInitManager getInstance() {
        if (sInstance == null) {
            sInstance = new AppInitManager();
        }
        return sInstance;
    }

    public static void destroy() {
        if (null != sInstance) {
            sInstance = null;
        }
    }

    public void initializeApp(Context context) {
        if (mIsInitialized) return;
        //1.标志位
        mIsInitialized = true;
        //1.先申请权限
        requestPermission(context);
        //1.初始化 Logger（Logger的初始化要放在ZJB-volley的前面)
        Logger.initLog(BuildConfig.DEBUG_ENABLE, FileUtil.getPathByType(FileUtil.DIR_TYPE_LOG));
        //2.初始化ImageLoader
        ZjbImageLoader.init(context.getApplicationContext(), FileUtil.getPathByType(FileUtil.DIR_TYPE_CACHE));
        //3.初始化ZJB-image-loader当中的日志系统
        L.writeDebugLogs(DebugUtil.isDebug());
        //4.初始化Volley
        Volley.init(ZjbApplication.gContext, BuildConfig.DEBUG_ENABLE, new BaseParameterGenerator(), UrlParserManager.getInstance(), ApiConstant.HOST);
        //5.http request 连接池初始化
        RequestPool.gRequestPool.init();
        //6.记录崩溃日志
//        UEHandler handler = new UEHandler(context.getApplicationContext());
//        Thread.setDefaultUncaughtExceptionHandler(handler);

        //7.初始化SDK相关
        initialize();
        //8.避免zjb目录下的文件被扫描进入系统文件
        FileUtil.hideMediaFile();
        //9.初始化个推
        PushCenterManager.getInstance().init(ZjbApplication.gContext);
        //10.初始化高德定位
        LocationManager.getInstance().init();
        //11.初始化相册
        LocalPhotoManager.getInstance().initialize();

        //12.初始化TalkData统计
        TCAgent.init(context);
        //13.错误上报,如果是debug模式。关闭错误上报
        if (DebugUtil.isDebug()) {
            TCAgent.setReportUncaughtExceptions(true);
        }

//        if (DebugUtil.isDebug()) {
//            LeakCanary.install(ZjbApplication.gContext);
//        }
    }

    public void initialize() {
        //1.处理eid
        String eid = PreferenceUtil.getString(SPConstant.KEY_EID);
        mSdkEntity.setDevice(TextUtils.isEmpty(eid) ? "eid" : eid);
        mSdkEntity.setAppVersion(BuildConfig.VERSION_CODE + "");
        mSdkEntity.setCid(PreferenceUtil.getString(SPConstant.KEY_PUSH_CLIENT_ID));
        mSdkEntity.setPlatform("android");
        mSdkEntity.setAppName("zhujiabang");
        mSdkEntity.setSystemVersion(Build.VERSION.RELEASE);
        mSdkEntity.setBuild(Build.MODEL);
        if (!LoginManager.getInstance().isLoginValidate()) {
            //用户没有登陆
            updateBeforeLogin();
        } else {
            User user = LoginManager.getInstance().getLoginUser();
            updateAfterLogin(user.getUid(), user.getToken());
        }
    }

    public void updateBeforeLogin() {
        //1.将非公共字段置空
        mSdkEntity.setUid(null);
        mSdkEntity.setToken(null);
    }

    public void updateAfterLogin(String userId, String token) {
        String eid = PreferenceUtil.getString(SPConstant.KEY_EID);
        if (TextUtils.isEmpty(eid)) {
            eid = SystemUtil.getIMEI();
        }
        if (TextUtils.isEmpty(eid)) {
            eid = SystemUtil.getMacAddress();
        }
        PreferenceUtil.putString(SPConstant.KEY_EID, eid);
        mSdkEntity.setUid(TextUtils.isEmpty(userId) ? "userid" : userId);
        mSdkEntity.setToken(TextUtils.isEmpty(token) ? "token" : token);
    }

    private void setLocationData() {
        long time = PreferenceUtil.getLong(SPConstant.KEY_LOCATION_TIME);
        if (time == 0) {
            //如果没有定位过,开启定位
            LocationManager.getInstance().startLocation();
        } else {
            String lon = PreferenceUtil.getString(SPConstant.KEY_LON);
            String lat = PreferenceUtil.getString(SPConstant.KEY_LAT);

            long currentTime = System.currentTimeMillis();
            int hour = (int) ((currentTime - time) / (1000 * 60 * 60));
            Logger.e("间隔时间 = " + hour);
            if (hour >= 2) {
                //如果离上次定位超过2个小时,则重新定位
                LocationManager.getInstance().startLocation();
            } else {
                //如果没有拿到文件中存储的经纬度,则重新定位
                if (TextUtils.isEmpty(lon) || TextUtils.isEmpty(lat)) {
                    LocationManager.getInstance().startLocation();
                } else {
                    String locationStr = PreferenceUtil.getString(SPConstant.KEY_LOCATION);
                    if (!TextUtils.isEmpty(locationStr)) {
                        //重新赋值
                        mLocation = GsonUtil.fromJson(locationStr, Location.class);
                    }
                    mSdkEntity.setLongtitude(lon);
                    mSdkEntity.setLatitude(lat);
                }
            }
        }
    }

    public static SdkEntity getSdkEntity() {
        return mSdkEntity;
    }

    public static Location getLocation() {
        return mLocation;
    }

    public static void setLocation(Location location) {
        mLocation = location;
    }

    /**
     * 申请 READ_PHONE_STATE 权限,获取IMEI,
     *
     * @param context
     */
    private void openPhoneStatePerMission(Context context) {
        RxPermissions.getInstance(context)
                .request(Manifest.permission.READ_PHONE_STATE)
                .subscribe(granted -> {
                    if (granted) {
                        String eid = SystemUtil.getIMEI();
                        if (TextUtils.isEmpty(eid)) {
                            eid = SystemUtil.getMacAddress();
                        }
                        PreferenceUtil.putString(SPConstant.KEY_EID, eid);
                    } else {
                        Logger.e("permission has refused!");
                    }
                }, Logger::e);
    }

    /**
     * 申请定位权限
     */
    private void openLocationPermission(Context context) {
        RxPermissions.getInstance(context)
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        setLocationData();
                    } else {
                        Logger.e("permission has refused!");
                    }
                }, Logger::e);
    }

    private void requestPermission(Context context) {
        openPhoneStatePerMission(context);
        openLocationPermission(context);
    }

}
