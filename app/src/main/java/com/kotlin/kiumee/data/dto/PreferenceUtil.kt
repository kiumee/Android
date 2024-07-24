package com.kotlin.kiumee.data.dto

import android.content.Context
import android.content.SharedPreferences
import com.kotlin.kiumee.core.util.KeyStorage.ACCESS_TOKEN
import com.kotlin.kiumee.core.util.KeyStorage.BUSINESS_ID
import com.kotlin.kiumee.core.util.KeyStorage.SESSION_ID

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getAccessToken(): String {
        return prefs.getString(ACCESS_TOKEN, null).toString()
    }

    fun setAccessToken(accessToken: String) {
        prefs.edit().putString(ACCESS_TOKEN, accessToken).apply()
    }

    fun getBusinessId(): Int {
        return prefs.getInt(BUSINESS_ID, -1)
    }

    fun setBusinessId(businessId: Int) {
        prefs.edit().putInt(BUSINESS_ID, businessId).apply()
    }

    fun getSessionId(): String {
        return prefs.getString(SESSION_ID, null).toString()
    }

    fun setSessionId(sessionId: String) {
        prefs.edit().putString(SESSION_ID, sessionId).apply()
    }
}
