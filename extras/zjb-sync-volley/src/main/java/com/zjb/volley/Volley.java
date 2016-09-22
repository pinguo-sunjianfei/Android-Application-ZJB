package com.zjb.volley;

import android.content.Context;

import com.zjb.volley.core.IParametersGenerator;
import com.zjb.volley.core.IPlaceholderParser;

/**
 * time: 2015/10/16
 * description:同步网络请求框架pinguo-sync-volley的入口
 *
 * @author sunjianfei
 */
public class Volley {

    public static Context gContext;
    public static boolean sDebug;
    public static IParametersGenerator sParametersGenerator;
    public static IPlaceholderParser sPlaceholderParser;
    public static String sValidateHost;

    /**
     * 同步Volley的初始化
     *
     * @param context      必须是Application的子类
     * @param debug        是否是调试模式
     * @param generator    请求参数的生成器(可能需要添加基本字段和签名字段)
     * @param validateHost https请求模式下需要与服务器进行双向认证的主机名
     */
    public static void init(Context context, boolean debug, IParametersGenerator generator
            , IPlaceholderParser parser, String validateHost) {
        Volley.gContext = context;
        Volley.sDebug = debug;
        Volley.sParametersGenerator = generator;
        Volley.sValidateHost = validateHost;
        Volley.sPlaceholderParser = parser;
    }

    public static void release() {
        Volley.gContext = null;
        Volley.sParametersGenerator = null;
        Volley.sValidateHost = null;
    }
}
