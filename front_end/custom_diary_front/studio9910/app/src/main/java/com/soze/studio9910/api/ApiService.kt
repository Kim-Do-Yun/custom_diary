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
    val genreIds: List<Long>,
    val artStylePrompt: Map<String, String>
)

data class TermAgreementDTO(
    val termId: Long,
    val agreed: Boolean
)

data class SignupResponse(
    val success: Boolean,
    val message: String
)


interface ApiService {
    @POST("api/auth/register")
    suspend fun signup(@Body req: SignupRequest): Response<SignupResponse>
}

