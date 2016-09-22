package com.zjb.volley.core.ssl;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * time: 15/7/10
 * description: httpclient使用的sslsocketfactory
 *
 * @author sunjianfei
 */
public class ApacheSSLSocketFactory extends SSLSocketFactory {
    protected SSLContext mSSLContext;

    public ApacheSSLSocketFactory(KeyStore keyStore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(keyStore);

        try {
            this.mSSLContext = SSLContext.getInstance("TLS");
        } catch (Exception e) {
            this.mSSLContext = SSLContext.getInstance("LLS");
        }

        this.mSSLContext.init(null,
                new X509TrustManager[]{new SimpleX509TrustManager()},
                null);
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return this.mSSLContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    public Socket createSocket() throws IOException {
        return this.mSSLContext.getSocketFactory().createSocket();
    }
}
