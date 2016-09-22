package com.zjb.volley.core;

/**
 * time:2016/7/28
 * description:
 * 解析带占位符的字符串
 *
 * @author sunjianfei
 */
public interface IPlaceholderParser {
    /**
     * 解析带占位符的Url
     *
     * @param url
     * @return
     */
    String parsePlaceholderUrl(String url);

    /**
     * 解析带占位符的json
     *
     * @param json
     * @return
     */
    String parsePlaceholderJson(String json);
}
