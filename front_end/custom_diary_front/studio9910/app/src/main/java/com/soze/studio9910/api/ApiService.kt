package com.soze.studio9910.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class SignupRequest(
    val email: String,
    val password: String,
    val genres: List<String>,
    val artStyle: String
)

data class SignupResponse(
    val success: Boolean,
    val message: String
)


interface ApiService {
    @POST("api/auth/register")
    suspend fun signup(@Body req: SignupRequest): Response<SignupResponse>
}

