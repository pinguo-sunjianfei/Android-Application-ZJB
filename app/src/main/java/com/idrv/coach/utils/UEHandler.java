package com.idrv.coach.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.zjb.volley.utils.NetworkUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * time: 15/7/17
 * description: 崩溃日志
 *
 * @author sunjianfei
 */
public class UEHandler implements Thread.UncaughtExceptionHandler {

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    public UEHandler(Context context) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        StringBuilder sb = new StringBuilder();
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printStream);
                cause = cause.getCause();
            }
            sb.append("Exception time:" + mDateFormat.format(new Date()) + " Thread Name:" + thread.getName()
                    + " Thread id:" + thread.getId() + "\n");
            sb.append(collectCrashDeviceInfo(mContext) + "\n");
            sb.append(new String(data) + "\n");
            Logger.f(sb.toString(), true);
            data = null;
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                Logger.e(e);
            }
        }
        // 弹出程序crash的对话框
        //mDefaultHandler.uncaughtException(thread, ex);
    }

    /**
     * 收集程序崩溃的设备信息
     *
     * @param context
     */
    public String collectCrashDeviceInfo(Context context) {
        StringBuilder sb = new StringBuilder();
        try {
            final PackageManager pm = context.getPackageManager();
            final PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            sb.append("版本").append(info.versionName).append(",").append(info.versionCode).append(",");
            sb.append("型号").append(Build.MODEL).append(",");
            sb.append("系统").append(Build.VERSION.RELEASE).append(",");
            sb.append(getNetworkType());
        } catch (Exception e) {
            // 忽略异常
        }
        return sb.toString();
    }

    private String getNetworkType() {
        if (NetworkUtil.isWifiConnected(mContext)) {
            return "wifi";
        } else {
            String apn = NetworkUtil.getAPN(mContext).apn;
            return apn;
        }
    }
}

