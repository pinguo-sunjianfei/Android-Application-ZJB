package com.zjb.volley.core.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * time: 15/7/10
 * description:无证书条件下X509TrustManager的默认实现
 *
 * @author sunjianfei
 */
public class SimpleX509TrustManager implements X509TrustManager {
    public SimpleX509TrustManager() {
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}