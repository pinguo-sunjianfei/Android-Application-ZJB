package com.zjb.volley.core;


import com.zjb.volley.bean.ErrorCode;

/**
 * time: 15/7/20
 * description:
 *
 * @author sunjianfei
 */
public interface IFetchDataListener {

    void success();


    void error(ErrorCode errorCode);
}
