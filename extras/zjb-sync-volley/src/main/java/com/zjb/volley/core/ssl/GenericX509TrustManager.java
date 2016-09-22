package com.zjb.volley.core.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * time: 15/7/10
 * description:
 *
 * @author sunjianfei
 */
public class GenericX509TrustManager implements X509TrustManager {
    private final X509TrustManager mOriginalX509TrustManager;
    private final KeyStore mTrustStore;

    public GenericX509TrustManager(KeyStore trustStore) throws NoSuchAlgorithmException, KeyStoreException {
        this.mTrustStore = trustStore;
        TrustManagerFactory originalTrustManagerFactory = TrustManagerFactory.getInstance("X509");
        originalTrustManagerFactory.init((KeyStore) null);
        TrustManager[] originalTrustManagers = originalTrustManagerFactory.getTrustManagers();
        this.mOriginalX509TrustManager = (X509TrustManager) originalTrustManagers[0];
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            this.mOriginalX509TrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException e1) {
            try {
                X509Certificate[] ex = this.reorderCertificateChain(chain);
                CertPathValidator validator = CertPathValidator.getInstance("PKIX");
                CertificateFactory factory = CertificateFactory.getInstance("X509");
                CertPath certPath = factory.generateCertPath(Arrays.asList(ex));
                PKIXParameters params = new PKIXParameters(this.mTrustStore);
                params.setRevocationEnabled(false);
                validator.validate(certPath, params);
            } catch (Exception e) {
                throw e1;
            }
        }

    }

    private X509Certificate[] reorderCertificateChain(X509Certificate[] chain) {
        X509Certificate[] reorderedChain = new X509Certificate[chain.length];
        List certificates = Arrays.asList(chain);
        int position = chain.length - 1;
        X509Certificate rootCert = this.findRootCert(certificates);
        reorderedChain[position] = rootCert;

        for (X509Certificate cert = rootCert; (cert = this.findSignedCert(cert, certificates)) != null && position > 0; reorderedChain[position] = cert) {
            --position;
        }

        return reorderedChain;
    }

    private X509Certificate findRootCert(List<X509Certificate> certificates) {
        X509Certificate rootCert = null;
        Iterator iterator = certificates.iterator();

        while (iterator.hasNext()) {
            X509Certificate cert = (X509Certificate) iterator.next();
            X509Certificate signer = this.findSigner(cert, certificates);
            if (signer == null || signer.equals(cert)) {
                rootCert = cert;
                break;
            }
        }

        return rootCert;
    }

    private X509Certificate findSignedCert(X509Certificate signingCert, List<X509Certificate> certificates) {
        X509Certificate signed = null;
        Iterator iterator = certificates.iterator();

        while (iterator.hasNext()) {
            X509Certificate cert = (X509Certificate) iterator.next();
            Principal signingCertSubjectDN = signingCert.getSubjectDN();
            Principal certIssuerDN = cert.getIssuerDN();
            if (certIssuerDN.equals(signingCertSubjectDN) && !cert.equals(signingCert)) {
                signed = cert;
                break;
            }
        }

        return signed;
    }

    private X509Certificate findSigner(X509Certificate signedCert, List<X509Certificate> certificates) {
        X509Certificate signer = null;
        Iterator iterator = certificates.iterator();

        while (iterator.hasNext()) {
            X509Certificate cert = (X509Certificate) iterator.next();
            Principal certSubjectDN = cert.getSubjectDN();
            Principal issuerDN = signedCert.getIssuerDN();
            if (certSubjectDN.equals(issuerDN)) {
                signer = cert;
                break;
            }
        }

        return signer;
    }
}
