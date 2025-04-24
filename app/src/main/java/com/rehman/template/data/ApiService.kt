package com.rehman.template.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // Auth
    @FormUrlEncoded
    @POST("auth/business/login")
    suspend fun authLoginUser(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("auth/forgot-password")
    suspend fun forgetLoginUser(
        @Field("email") email: String,
    ): Response<ResponseBody>

    @GET("auth/logout")
    suspend fun logoutUser(): Response<ResponseBody>



}