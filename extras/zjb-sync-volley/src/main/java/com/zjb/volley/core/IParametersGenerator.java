package com.zjb.volley.core;

import com.zjb.volley.bean.HttpParams;

/**
 * time: 2015/10/16
 * description:将基本参数添加到httpParams当中，并且生成一个新的HttpParams
 *
 * @author sunjianfei
 */
public interface IParametersGenerator {
    /**
     * 添加基本参数
     *
     * @param parameters 基本参数
     * @return
     */
    void addBaseParameters(HttpParams parameters);

    /**
     * 生成签名字段
     *
     * @param url        请求字段
     * @param parameters 参数
     * @return
     */

    HttpParams generateParameter(String url, HttpParams parameters);

    /**
     * 根据URL，判断是否在将参数放入容器之前对数据进行encode
     *
     * @param url 网址
     * @return
     */
    boolean isNeedURLEncode(String url);
}
