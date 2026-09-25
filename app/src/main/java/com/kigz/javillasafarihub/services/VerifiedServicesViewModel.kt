package com.kigz.javillasafarihub.services

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class VerifiedServicesUiState(
    val services: List<TouristService> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmittingReport: Boolean = false,
    val errorMessage: String? = null
)

class VerifiedServicesViewModel(
    private val repository: VerifiedServicesRepository = VerifiedServicesRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(VerifiedServicesUiState())
    val uiState: StateFlow<VerifiedServicesUiState> = _uiState.asStateFlow()
    private var listener: ValueEventListener? = null

    init { observeServices() }

    private fun observeServices() {
        listener = repository.observeServices(
            onSuccess = { services ->
                _uiState.value = _uiState.value.copy(services = services, isLoading = false, errorMessage = null)
            },
            onError = { message ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
            }
        )
    }

    fun reportService(service: TouristService, reason: String, details: String, onComplete: (Boolean, String?) -> Unit) {
        _uiState.value = _uiState.value.copy(isSubmittingReport = true, errorMessage = null)
        repository.reportService(service, reason, details,
            onSuccess = {
                _uiState.value = _uiState.value.copy(isSubmittingReport = false)
                onComplete(true, null)
            },
            onError = { message ->
                _uiState.value = _uiState.value.copy(isSubmittingReport = false, errorMessage = message)
                onComplete(false, message)
            }
        )
    }

    override fun onCleared() {
        listener?.let(repository::removeListener)
        super.onCleared()
    }
}
