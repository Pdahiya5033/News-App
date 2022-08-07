package com.example.newsapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface newsApi {
    @GET("everything")
    Call<Object> getNewsData(@Query("q") String q1,
                             @Query("apiKey") String apikey);
}
