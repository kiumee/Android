package com.kotlin.kiumee.presentation.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.kiumee.MainApplication
import com.kotlin.kiumee.core.view.UiState
import com.kotlin.kiumee.data.ServicePool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoreViewModel : ViewModel() {
    private val _getStore = MutableStateFlow<UiState<List<StoreEntity>>>(UiState.Empty)
    val getStore: StateFlow<UiState<List<StoreEntity>>> = _getStore

    init {
        getStore()
    }

    private fun getStore() = viewModelScope.launch {
        _getStore.value = UiState.Loading
        runCatching {
            ServicePool.storeApiService.getBusiness().data.map { it.toStoreEntity() }
        }.fold(
            { _getStore.value = UiState.Success(it) },
            { _getStore.value = UiState.Failure(it.message.toString()) }
        )
    }

    fun getBusinessId(businessId: Int) =
        viewModelScope.launch {
            MainApplication.prefs.setBusinessId(businessId)
        }
}
