package com.kotlin.kiumee.data.api

import com.kotlin.kiumee.core.util.KeyStorage.LOGIN
import com.kotlin.kiumee.core.util.KeyStorage.V1
import com.kotlin.kiumee.data.dto.request.RequestLoginDto
import com.kotlin.kiumee.data.dto.response.ResponseLoginDto
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("$V1/$LOGIN")
    suspend fun postLogin(
        @Body requestLoginDto: RequestLoginDto
    ): ResponseLoginDto
}
