package com.xw.sspku_dormselect.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by xw on 2017/12/2.
 */

public class SelectDormApp extends Application{
    private static final String TAG = "MyAPP";
    private static SelectDormApp myApplication;//单例入口

    private String userName;
    private String password;

    public int choosenType;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d( TAG, "SelectDormApp->OnCreate");
        myApplication = this;

        choosenType = 0;

        handleSSLHandshake();

        userName = null;
        password = null;
    }

    // 只能通过此方法获取该单例类的对象
    public static SelectDormApp getInstance(){
        return myApplication;
    }

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }

            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
}
