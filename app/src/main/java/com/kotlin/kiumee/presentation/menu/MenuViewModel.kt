package com.kotlin.kiumee.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.kiumee.MainApplication
import com.kotlin.kiumee.core.view.UiState
import com.kotlin.kiumee.data.ServicePool
import com.kotlin.kiumee.data.dto.request.RequestBillingDto
import com.kotlin.kiumee.data.dto.request.RequestPromptDto
import com.kotlin.kiumee.data.dto.response.ResponseBillingDto
import com.kotlin.kiumee.presentation.menu.chat.ChatEntity
import com.kotlin.kiumee.presentation.menu.chat.guidebtn.GuideBtnEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val _getMenu = MutableStateFlow<UiState<List<CategoryEntity>>>(UiState.Loading)
    val getMenu: StateFlow<UiState<List<CategoryEntity>>> = _getMenu

    private val _postPrompt = MutableSharedFlow<UiState<ChatEntity>>()
    val postPrompt: SharedFlow<UiState<ChatEntity>> = _postPrompt

    // 더미용
    private val _postCasePrompt = MutableSharedFlow<UiState<ChatEntity>>()
    val postCasePrompt: SharedFlow<UiState<ChatEntity>> = _postCasePrompt

    private val _getPrompts = MutableStateFlow<UiState<List<GuideBtnEntity>>>(UiState.Loading)
    val getPrompts: StateFlow<UiState<List<GuideBtnEntity>>> = _getPrompts

    private val _putBilling = MutableStateFlow<UiState<ResponseBillingDto>>(UiState.Empty)
    val putBilling: StateFlow<UiState<ResponseBillingDto>> = _putBilling

    init {
        getCategory()
        getPrompts()
    }

    private fun getCategory() = viewModelScope.launch {
        runCatching {
            ServicePool.menuApiService.getItems(MainApplication.prefs.getBusinessId()).data.map { it.toCategoryEntity() }
        }.fold(
            { _getMenu.value = UiState.Success(it) },
            { _getMenu.value = UiState.Failure(it.message.toString()) }
        )
    }

    fun postPrompt(userChatData: RequestPromptDto) = viewModelScope.launch {
        _postPrompt.emit(UiState.Loading)
        runCatching {
            ServicePool.menuApiService.postPrompt(
                MainApplication.prefs.getBusinessId(),
                MainApplication.prefs.getSessionId(),
                userChatData
            ).toChatEntity()
        }.fold(
            { _postPrompt.emit(UiState.Success(it)) },
            { _postPrompt.emit(UiState.Failure(it.message.toString())) }
        )
    }

    // 더미용
    fun postCasePrompt(case: Int, userChatData: RequestPromptDto) = viewModelScope.launch {
        _postPrompt.emit(UiState.Loading)
        runCatching {
            ServicePool.menuApiService.postCasePrompt(
                MainApplication.prefs.getBusinessId(),
                MainApplication.prefs.getSessionId(),
                case,
                userChatData
            ).toChatEntity()
        }.fold(
            { _postPrompt.emit(UiState.Success(it)) },
            { _postPrompt.emit(UiState.Failure(it.message.toString())) }
        )
    }

    private fun getPrompts() = viewModelScope.launch {
        runCatching { ServicePool.menuApiService.getPrompts(MainApplication.prefs.getBusinessId()).data.map { it.toGuideBtnEntity() } }.fold(
            { _getPrompts.value = UiState.Success(it) },
            { _getPrompts.value = UiState.Failure(it.message.toString()) }
        )
    }

    fun putBilling(billingData: List<Int>) = viewModelScope.launch {
        runCatching {
            ServicePool.menuApiService.putBilling(
                MainApplication.prefs.getBusinessId(),
                MainApplication.prefs.getSessionId(),
                RequestBillingDto(billingData)
            )
        }.fold(
            { _putBilling.value = UiState.Success(it) },
            { _putBilling.value = UiState.Failure(it.message.toString()) }
        )
    }
}
