package com.kigz.javillasafarihub.safety

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ScamReportsUiState(
    val reports: List<ScamReport> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

class ScamReportsViewModel(
    private val repository: ScamReportsRepository = ScamReportsRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScamReportsUiState())
    val uiState: StateFlow<ScamReportsUiState> = _uiState.asStateFlow()
    private var listener: ValueEventListener? = null

    init { observeReports() }

    private fun observeReports() {
        listener = repository.observeReports(
            onSuccess = { reports ->
                _uiState.value = _uiState.value.copy(reports = reports, isLoading = false, errorMessage = null)
            },
            onError = { message ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
            }
        )
    }

    fun submitReport(category: String, location: String, description: String, onComplete: (Boolean, String?) -> Unit) {
        _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)
        repository.addReport(category, location, description,
            onSuccess = {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
                onComplete(true, null)
            },
            onError = { message ->
                _uiState.value = _uiState.value.copy(isSubmitting = false, errorMessage = message)
                onComplete(false, message)
            }
        )
    }

    fun confirmReport(reportId: String) {
        repository.confirmReport(reportId) { message ->
            _uiState.value = _uiState.value.copy(errorMessage = message)
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }

    fun refresh() {
        listener?.let(repository::removeListener)
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        observeReports()
    }

    override fun onCleared() {
        listener?.let(repository::removeListener)
        super.onCleared()
    }
}
