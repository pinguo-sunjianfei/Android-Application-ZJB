package com.idrv.coach.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.idrv.coach.ZjbApplication.gContext;

/**
 * time: 15/7/18
 * description:
 *
 * @author sunjianfei
 */
public class SystemUtil {
    /**
     * 检查是否是正确的email格式
     *
     * @param email
     * @return
     */
    public static boolean checkEmailFormat(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        String reg = "^[0-9a-z_-][_.0-9a-z-]{0,31}@([0-9a-z][0-9a-z-]{0,30}\\.){1,4}[a-z]{2,4}$";
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 判断字符串是否含有中文
     */
    public static boolean isContainsChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find()) {
            flg = true;
        }
        return flg;
    }

    /**
     * 检查是否是正确手机号
     *
     * @param phoneNumber
     * @return
     */
    public static boolean checkPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        String reg = "^[0-9]{11}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static String getLocationInfo() {
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        return language + "-" + country;
    }

    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) gContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (TextUtils.isEmpty(imei)) {
            imei = "";
        }
        return imei;
    }

    public static String getMacAddress() {
        WifiManager wifiManager = (WifiManager) gContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return (wifiInfo == null) ? "" : wifiInfo.getMacAddress();
    }

    public static String getMetaData(String key) {
        try {
            Context context = gContext.getApplicationContext();
            //1.得到PackageManager
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            //2.得到PackageInfo
//            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            //3.得到ApplicationInfo
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                    packageName, PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            return metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMD5Str(String password) {
        String strResult = "";
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes("UTF-8"));
            byte[] bzpassword_1 = md5.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0;
                 i < bzpassword_1.length;
                 ++i) {
                sb.append(String.format("%02x", bzpassword_1[i]));
            }
            md5.update(sb.toString().getBytes("UTF-8"));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }

        StringBuilder builder = new StringBuilder();
        for (String s : cpuInfo) {
            builder.append(s.toLowerCase() + ",");
        }
        return builder.toString();
    }

    public static boolean isX86CPU() {
        String cpu = getCpuInfo();
        if (!TextUtils.isEmpty(cpu)) {
            if (cpu.contains("x86")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 把一个double类型转化为对应的string类型
     */
    public static String doubleParseToString(double d1) {
        Double d = new Double(d1);
        BigDecimal bd = new BigDecimal(d.toString());
        return bd.toPlainString();
    }

    public static String getZhCN() {
        String language = Locale.CHINA.getLanguage().toLowerCase();
        String county = Locale.CHINA.getCountry();
        return language + "_" + county;
    }

    public static boolean isAppRunningForeground(Context context) {
        ActivityManager var1 = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = var1.getRunningTasks(1);
        return context.getPackageName().equalsIgnoreCase(((ActivityManager.RunningTaskInfo) list.get(0)).baseActivity.getPackageName());
    }

    /**
     * java实体转换成map
     */
    public static Map<String, String> beanToMap(Object object) {
        Map<String, String> params = new HashMap<>();
        if (null == object) {
            return params;
        }
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldObj = field.get(object);
                if (fieldObj != null && fieldObj.getClass() == String.class) {
                    String fieldValue = (String) fieldObj;
                    if (!TextUtils.isEmpty(fieldValue)) {
                        if (!TextUtils.isEmpty(fieldValue)) {
                            fieldValue = fieldValue.replaceAll("\"", "%22");
                        }
                        params.put(fieldName, fieldValue);
                    }
                }
            }
            return params;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }
}
