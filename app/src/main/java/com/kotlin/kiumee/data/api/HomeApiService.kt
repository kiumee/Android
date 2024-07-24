package com.kotlin.kiumee.data.api

import com.kotlin.kiumee.core.util.KeyStorage.BUSINESS_ID
import com.kotlin.kiumee.core.util.KeyStorage.NEW_SESSION
import com.kotlin.kiumee.core.util.KeyStorage.ORDERS
import com.kotlin.kiumee.core.util.KeyStorage.PROMPT
import com.kotlin.kiumee.core.util.KeyStorage.V1
import com.kotlin.kiumee.data.dto.response.ResponseSessionDto
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeApiService {
    @GET("$V1/$ORDERS/{$BUSINESS_ID}/$PROMPT/$NEW_SESSION")
    suspend fun getSession(
        @Path(value = BUSINESS_ID) businessId: Int
    ): ResponseSessionDto
}
