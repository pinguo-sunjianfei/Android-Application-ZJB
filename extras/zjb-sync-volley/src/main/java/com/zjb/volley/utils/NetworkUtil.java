package com.zjb.volley.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.text.TextUtils;

import com.zjb.volley.log.HttpLogger;

import java.lang.reflect.Method;

/**
 * time: 15/7/10
 * description:网络状态相关的工具类
 *
 * @author sunjianfei
 */
public class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();

    public static ConnectivityManager getConnManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static boolean isConnected(Context context) {
        NetworkInfo net = getConnManager(context).getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    public static boolean isConnectedOrConnecting(Context context) {
        NetworkInfo[] nets = getConnManager(context).getAllNetworkInfo();
        if (nets != null) {
            NetworkInfo[] networkInfos = nets;
            int length = nets.length;
            for (int i = 0; i < length; ++i) {
                NetworkInfo net = networkInfos[i];
                if (net.isConnectedOrConnecting()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isWifiConnected(Context context) {
        NetworkInfo net = getConnManager(context).getActiveNetworkInfo();
        return net != null && net.getType() == 1 && net.isConnected();
    }

    public static boolean isMobileConnected(Context context) {
        NetworkInfo net = getConnManager(context).getActiveNetworkInfo();
        return net != null && net.getType() == 0 && net.isConnected();
    }

    public static boolean isAvailable(Context context) {
        return isWifiAvailable(context) || isMobileAvailable(context) && isMobileEnabled(context);
    }

    public static boolean isWifiAvailable(Context context) {
        NetworkInfo[] nets = getConnManager(context).getAllNetworkInfo();
        if (nets != null) {
            NetworkInfo[] networkInfos = nets;
            int length = nets.length;

            for (int i = 0; i < length; ++i) {
                NetworkInfo net = networkInfos[i];
                if (net.getType() == 1) {
                    return net.isAvailable();
                }
            }
        }

        return false;
    }

    public static boolean isMobileAvailable(Context context) {
        NetworkInfo[] nets = getConnManager(context).getAllNetworkInfo();
        if (nets != null) {
            NetworkInfo[] networkInfos = nets;
            int length = nets.length;

            for (int i = 0; i < length; ++i) {
                NetworkInfo net = networkInfos[i];
                if (net.getType() == 0) {
                    return net.isAvailable();
                }
            }
        }

        return false;
    }

    public static boolean isMobileEnabled(Context context) {
        try {
            Method e = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled", new Class[0]);
            e.setAccessible(true);
            return ((Boolean) e.invoke(getConnManager(context), new Object[0])).booleanValue();
        } catch (Exception var2) {
            var2.printStackTrace();
            return true;
        }
    }

    private static boolean printNetworkInfo(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo in = connectivity.getActiveNetworkInfo();
            HttpLogger.e(TAG, "Active network info: " + in);
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; ++i) {
                    HttpLogger.e(TAG, "NetworkInfo[" + i + "]isAvailable : " + info[i].isAvailable());
                    HttpLogger.e(TAG, "NetworkInfo[" + i + "]isConnected : " + info[i].isConnected());
                    HttpLogger.e(TAG, "NetworkInfo[" + i + "]isConnectedOrConnecting : " + info[i].isConnectedOrConnecting());
                    HttpLogger.e(TAG, "NetworkInfo[" + i + "]: " + info[i]);
                }

                HttpLogger.e(TAG, "\n");
            } else {
                HttpLogger.e(TAG, "All network information are null.");
            }
        }

        return false;
    }

    public static NetworkType getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable() ? (info.getType() == 0 ? NetworkType.MOBILE : (info.getType() == 1 ? NetworkType.WIFI : NetworkType.OTHER)) : NetworkType.NONE;
    }

    public static String[] getProxyHostAndPort(Context context) {
        return getNetworkType(context) == NetworkType.WIFI ?
                new String[]{"", "-1"}
                : new String[]{Proxy.getDefaultHost(), "" + Proxy.getDefaultPort()};
    }

    public static boolean isWapNet(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == 1) {
                return false;
            } else {
                String currentAPN = info.getExtraInfo();
                return !TextUtils.isEmpty(currentAPN) && (currentAPN.equals("cmwap") || currentAPN.equals("uniwap") || currentAPN.equals("3gwap"));
            }
        } else {
            return false;
        }
    }

    public static APNWrapper getAPN(Context context) {
        APNWrapper wrapper = new APNWrapper();
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(Uri.parse("content://telephony/carriers/preferapn"),
                    new String[]{"name", "apn", "proxy", "port"}, null, null, null);
        } catch (Exception e) {
        }

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.isAfterLast()) {
                wrapper.name = "N/A";
                wrapper.apn = "N/A";
            } else {
                String name = cursor.getString(0);
                String apn = cursor.getString(1);
                wrapper.name = name == null ? "" : name.trim();
                wrapper.apn = apn == null ? "" : apn.trim();
            }

            cursor.close();
        } else {
            wrapper.name = "N/A";
            wrapper.apn = "N/A";
        }

        wrapper.proxy = Proxy.getDefaultHost();
        wrapper.proxy = TextUtils.isEmpty(wrapper.proxy) ? "" : wrapper.proxy;
        wrapper.port = Proxy.getDefaultPort();
        wrapper.port = wrapper.port > 0 ? wrapper.port : 80;
        return wrapper;
    }

    public static class APNWrapper {
        public String name;
        public String apn;
        public String proxy;
        public int port;

        public String getApn() {
            return this.apn;
        }

        public String getName() {
            return this.name;
        }

        public int getPort() {
            return this.port;
        }

        public String getProxy() {
            return this.proxy;
        }

        APNWrapper() {
        }

        public String toString() {
            return "{name=" + this.name + ";apn=" + this.apn + ";proxy=" + this.proxy + ";port=" + this.port + "}";
        }
    }

    public static enum NetworkType {
        NONE(1),
        MOBILE(2),
        WIFI(4),
        OTHER(8);

        public int value;

        private NetworkType(int value) {
            this.value = value;
        }
    }
}
