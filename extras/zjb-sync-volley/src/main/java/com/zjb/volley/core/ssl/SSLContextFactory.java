package com.zjb.volley.core.ssl;

import android.content.res.AssetManager;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.zjb.volley.Volley;
import com.zjb.volley.log.HttpLogger;


/**
 * time: 15/7/10
 * description:ssl构造工厂
 *
 * @author sunjianfei
 */
public class SSLContextFactory {
    private static SSLContextFactory sInstance = null;
    private SSLContext mSSLContext;

    private SSLContextFactory() {
    }

    public static SSLContextFactory getInstance() {
        if (null == sInstance) {
            synchronized (SSLContextFactory.class) {
                if (sInstance == null) {
                    sInstance = new SSLContextFactory();
                }
            }
        }
        return sInstance;
    }

    public SSLContext makeContext() throws Exception {
        SSLContext context;
        try {
            //默认的加密方式为SSLv3 TLSv1
            context = SSLContext.getInstance("SSLv3");
        } catch (Exception e) {
            context = SSLContext.getInstance("TLSv1");
        }

        context.init(null, new X509TrustManager[]{new SimpleX509TrustManager()}, new SecureRandom());
        return context;
    }

    public SSLContext getSSLContext() {
        if (mSSLContext == null) {
            mSSLContext = makeSSLContext();
        }
        return mSSLContext;
    }

    /**
     * 得到PKCS12的KeyStore,即.p12文件
     *
     * @param path     assets路径
     * @param password p12文件的路径
     * @return
     */
    private KeyStore loadPKCS12Store(String path, String password) throws IOException {
        KeyStore keyStore = null;
        InputStream ins = null;
        try {
            keyStore = KeyStore.getInstance("PKCS12");
            AssetManager manager = Volley.gContext.getAssets();
            ins = manager.open(path);
            keyStore.load(ins, password.toCharArray());
        } catch (IOException ie) {
            throw ie;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return keyStore;
    }

    /**
     * 得到BKS的KeyStore,即.truststore文件
     *
     * @param path     assets路径
     * @param password truststore文件的路径
     * @return
     */
    private KeyStore loadBKSStore(String path, String password) throws IOException {
        KeyStore keyStore = null;
        InputStream ins = null;
        try {
            keyStore = KeyStore.getInstance("BKS");
            AssetManager manager = Volley.gContext.getAssets();
            ins = manager.open(path);
            keyStore.load(ins, password.toCharArray());
        } catch (IOException ie) {
            throw ie;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return keyStore;
    }


    /**
     * 获取SSLContext
     */
    private SSLContext makeSSLContext() {
        try {
            KeyStore keyStore = loadPKCS12Store("ssl/client.p12", "123456");
            KeyStore trustStore = loadBKSStore("ssl/client.truststore", "123456");
            if (keyStore == null || trustStore == null) {
                return makeContext();
            }
            //1.创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            //2.创建TrustManagerFactory并初始化
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            //3.得到KeyManagerFactory并初始化
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keyStore, "123456".toCharArray());
            //4.初始化sslContext并返回
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            return sslContext;
        } catch (IOException ie) {
            try {
                return makeContext();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            HttpLogger.e("证书认证失败!");
        }
        return null;
    }


    /**
     * 通过文件形式得到SSLContext
     *
     * @param clientCertFile
     * @param clientCertPassword
     * @param caCertString
     * @return
     * @throws Exception
     */
    public SSLContext makeContext(File clientCertFile,
                                  String clientCertPassword,
                                  String caCertString) throws Exception {
        KeyStore keyStore = this.loadPKCS12KeyStore(clientCertFile, clientCertPassword);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        kmf.init(keyStore, clientCertPassword.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();
        KeyStore trustStore = this.loadPEMTrustStore(caCertString);
        TrustManager[] trustManagers = new TrustManager[]{new GenericX509TrustManager(trustStore)};
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    public SSLContext makeContext(String caCertString) throws Exception {
        KeyStore trustStore = this.loadPEMTrustStore(caCertString);
        TrustManager[] trustManagers = new TrustManager[]{new GenericX509TrustManager(trustStore)};
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, null);
        return sslContext;
    }

    private KeyStore loadPEMTrustStore(String certificateString) throws Exception {
        byte[] der = this.loadPemCertificate(new ByteArrayInputStream(certificateString.getBytes()));
        ByteArrayInputStream derInputStream = new ByteArrayInputStream(der);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(derInputStream);
        String alias = cert.getSubjectX500Principal().getName();
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);
        trustStore.setCertificateEntry(alias, cert);
        return trustStore;
    }

    private KeyStore loadPKCS12KeyStore(File certificateFile, String clientCertPassword) throws Exception {
        KeyStore keyStore = null;
        FileInputStream fis = null;
        try {
            keyStore = KeyStore.getInstance("PKCS12");
            fis = new FileInputStream(certificateFile);
            keyStore.load(fis, clientCertPassword.toCharArray());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
            }
        }
        return keyStore;
    }

    byte[] loadPemCertificate(InputStream certificateStream) throws IOException {
        BufferedReader br = null;
        byte[] buffer;
        try {
            StringBuilder buf = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(certificateStream));

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (!line.startsWith("--")) {
                    buf.append(line);
                }
            }
            String pem = buf.toString();
            buffer = Base64.decode(pem, 0);
        } finally {
            if (br != null) {
                br.close();
            }

        }

        return buffer;
    }
}
