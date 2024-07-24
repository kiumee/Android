package com.kotlin.kiumee.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.kiumee.MainApplication
import com.kotlin.kiumee.core.view.UiState
import com.kotlin.kiumee.data.ServicePool
import com.kotlin.kiumee.data.dto.response.ResponseSessionDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SessionViewModel : ViewModel() {
    private val _getSession = MutableStateFlow<UiState<ResponseSessionDto>>(UiState.Loading)
    val getSession: StateFlow<UiState<ResponseSessionDto>> = _getSession

    init {
        getSession()
    }

    private fun getSession() = viewModelScope.launch {
        runCatching {
            ServicePool.homeApiService.getSession(MainApplication.prefs.getBusinessId())
        }.fold(
            {
                _getSession.value = UiState.Success(it).apply {
                    MainApplication.prefs.setSessionId(it.sessionId)
                }
            },
            { _getSession.value = UiState.Failure(it.message.toString()) }
        )
    }
}
