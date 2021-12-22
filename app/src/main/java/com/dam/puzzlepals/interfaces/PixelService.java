package com.dam.puzzlepals.interfaces;

import com.dam.puzzlepals.models.Image;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PixelService {

    @GET("photos/{id}")
    Observable<Image> findImage(@Path("id") Integer id);

}
