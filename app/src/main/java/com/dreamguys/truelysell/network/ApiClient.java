package com.dreamguys.truelysell.network;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.dreamguys.truelysell.utils.AppConstants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClientNoHeader() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

//        HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier(){
//            public boolean verify(String string,SSLSession ssls) {
//                return true;
//            }
//        });

//        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//            public boolean verify(final String hostname, final SSLSession session) {
//                if (hostname.equals("truelysell.dreamguystech.com")) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        return buildRetrofit(httpClient);
    }

    public static Retrofit getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

//        HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier(){
//            public boolean verify(String string,SSLSession ssls) {
//                return true;
//            }
//        });
//
//        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//            public boolean verify(final String hostname, final SSLSession session) {
//                if (hostname.equals("truelysell.dreamguystech.com")) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        return buildRetrofit(httpClient);
    }

    @NonNull
    private static Retrofit buildRetrofit(OkHttpClient.Builder httpClient) {
        httpClient.connectTimeout(1, TimeUnit.MINUTES);
        httpClient.readTimeout(5, TimeUnit.MINUTES);
        httpClient.writeTimeout(5, TimeUnit.MINUTES);

//        HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier(){
//            public boolean verify(String string,SSLSession ssls) {
////                if (string.equals("truelysell.dreamguystech.com")) {
//                    return true;
////                } else {
////                    return false;
////                }
//            }
//        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }



}