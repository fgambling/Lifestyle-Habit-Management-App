package com.comp90018.a2.location;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodeAPI {
    @GET("/reverse")
    Observable<GeocodeResponse> getAddress(@Query("lat") double lat, @Query("lon") double lon);
}
