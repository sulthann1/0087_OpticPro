package com.opticpro.app.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers("No-Auth: true")
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): ApiResponse<UserDto>

    @Headers("No-Auth: true")
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): ApiResponse<AuthData>

    @Headers("No-Auth: true")
    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): ApiResponse<AuthData>

    @POST("auth/logout")
    suspend fun logout(@Body body: RefreshRequest): ApiResponse<Unit>

    @GET("auth/me")
    suspend fun me(): ApiResponse<UserDto>
}