package com.kigz.javillasafarihub.safety

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SafetyIncidentsUiState(
    val incidents: List<SafetyIncident> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

class SafetyIncidentsViewModel(
    private val repository: SafetyIncidentsRepository = SafetyIncidentsRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(SafetyIncidentsUiState())
    val uiState: StateFlow<SafetyIncidentsUiState> = _uiState.asStateFlow()
    private var listener: ValueEventListener? = null

    init {
        listener = repository.observeIncidents(
            onSuccess = { _uiState.value = _uiState.value.copy(incidents = it, isLoading = false, errorMessage = null) },
            onError = { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it) }
        )
    }

    fun submit(category: IncidentCategory, location: String, description: String, serviceId: String, latitude: Double?, longitude: Double?, onComplete: (Boolean, String?) -> Unit) {
        _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)
        repository.addIncident(category, location, description, serviceId, latitude, longitude,
            onSuccess = { _uiState.value = _uiState.value.copy(isSubmitting = false); onComplete(true, null) },
            onError = { _uiState.value = _uiState.value.copy(isSubmitting = false, errorMessage = it); onComplete(false, it) }
        )
    }

    fun confirm(id: String) {
        repository.confirmIncident(id) { _uiState.value = _uiState.value.copy(errorMessage = it) }
    }

    override fun onCleared() {
        listener?.let(repository::removeListener)
        super.onCleared()
    }
}
