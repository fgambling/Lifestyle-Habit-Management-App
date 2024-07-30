package com.comp90018.a2.location;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeocodeService {
    private final String baseUrl = "https://geocode.maps.co";
    private GeocodeAPI geocodeAPI;
    public GeocodeService() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        geocodeAPI = retrofit.create(GeocodeAPI.class);
    }

    public Observable<String> getAddress(LatLong latLong) {
        return geocodeAPI.getAddress(latLong.latitude, latLong.longitude)
                .map(GeocodeResponse::getDisplay_name);
    }
}
