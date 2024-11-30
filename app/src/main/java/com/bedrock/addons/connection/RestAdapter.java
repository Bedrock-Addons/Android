package com.bedrock.addons.connection;

import com.bedrock.addons.BuildConfig;
import com.bedrock.addons.data.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestAdapter {

    private static final long CONNECT_TIMEOUT_MS = 5000; // 5 seconds
    private static final long WRITE_TIMEOUT_MS = 10000; // 10 seconds
    private static final long READ_TIMEOUT_MS = 30000; // 30 seconds

    public static API createAPI() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        httpClientBuilder.writeTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        httpClientBuilder.readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }

        OkHttpClient okHttpClient = httpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(API.class);
    }
}
