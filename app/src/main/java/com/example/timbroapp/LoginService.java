package com.example.timbroapp;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoginService {

    @FormUrlEncoded
    @POST("login")
    Call<Result> login (
        @Field("username") String username,
        @Field("password") String password
    );

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("list-stampings")
    Call<ResultStampings> list_stampings (
        @Query("id_user") String id_user,
        @Header("Authorization") String jwt_token
    );

}
