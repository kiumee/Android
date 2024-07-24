package com.kotlin.kiumee.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.kiumee.MainApplication
import com.kotlin.kiumee.core.view.UiState
import com.kotlin.kiumee.data.ServicePool
import com.kotlin.kiumee.data.dto.request.RequestLoginDto
import com.kotlin.kiumee.data.dto.response.ResponseLoginDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {
    private val _postLogin = MutableSharedFlow<UiState<ResponseLoginDto>>()
    val postLogin: SharedFlow<UiState<ResponseLoginDto>> = _postLogin

    fun postLogin(username: String, password: String) = viewModelScope.launch {
        _postLogin.emit(UiState.Loading)
        runCatching {
            ServicePool.loginApiService.postLogin(RequestLoginDto(username, password))
        }.fold(
            {
                _postLogin.emit(UiState.Success(it))
                    .apply { MainApplication.prefs.setAccessToken(it.token.accessToken) }
            },
            { _postLogin.emit(UiState.Failure(it.message.toString())) }
        )
    }
}
