package com.zjb.volley.bean.parser;


/**
 * time: 15/7/23
 * description: 网络数据返回，解析json数据的解析器，parser方法里面的json字符串是
 *  json字符串当中data字段
 *
 * @author sunjianfei
 */
public abstract class BaseParser<T> {

    public abstract T parser(String json);

}
