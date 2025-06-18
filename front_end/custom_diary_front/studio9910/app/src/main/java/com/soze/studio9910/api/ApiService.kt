package com.soze.studio9910.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class SignupRequest(
    val idToken: String,  // Firebase 토큰
    val password: String,
    val terms: List<TermAgreementDTO>,
    val gender: String,  // "M", "F", "O"
    val birthDate: String,  // "YYYY-MM-DD" 형식
    val genreNames: List<String>,
    val artStyleId: String
)

data class TermAgreementDTO(
    val termId: Long,
    val agreed: Boolean
)

data class SignupResponse(
    val firebaseUid: String,
    val email: String,
    val password: String,
    val gender: String,
    val birthDate: String,
    val agreedTermCodes: List<Long>,
    val genreNames: List<String>,
    val artStyleId: String
)

data class LoginRequest(
    val idToken: String
)

data class LoginResponse(
    val firebaseUid: String,
    val email: String,
    val gender: String,
    val password: String,
    val birthDate: String,
    val createdAt: String
)


interface ApiService {
    @POST("api/auth/register")
    suspend fun signup(@Body req: SignupRequest): Response<SignupResponse>

    @POST("api/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>
}

