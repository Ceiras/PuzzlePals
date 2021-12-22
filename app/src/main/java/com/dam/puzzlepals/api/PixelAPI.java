package com.dam.puzzlepals.api;

import com.dam.puzzlepals.interfaces.PixelService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PixelAPI {

    private static final String PIXEL_API_KEY = "563492ad6f917000010000015a7a31ec1416491781d6c82a4b85b462";
    private static final String PIXEL_URL = "https://api.pexels.com/v1/";

    private static PixelService pixelService;

    public static PixelService getPixelService() {
        if (pixelService == null) {
            OkHttpClient.Builder okHttpChannelBuilder = new OkHttpClient.Builder();
            okHttpChannelBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Request.Builder newRequest = request.newBuilder().header("Authorization", PIXEL_API_KEY);

                    return chain.proceed(newRequest.build());
                }
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(PIXEL_URL)
                    .client(okHttpChannelBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            pixelService = retrofit.create(PixelService.class);
        }

        return pixelService;
    }

}
