package com.kigz.javillasafarihub.reviews

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

 data class TouristReviewsUiState(
    val reviews: List<TouristReview> = emptyList(),
    val selectedCategory: ReviewCategory = ReviewCategory.ALL,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

class TouristReviewsViewModel(
    private val repository: TouristReviewsRepository = TouristReviewsRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(TouristReviewsUiState())
    val uiState: StateFlow<TouristReviewsUiState> = _uiState.asStateFlow()
    private var listener: ValueEventListener? = null

    init { observeReviews() }

    private fun observeReviews() {
        listener = repository.observeReviews(
            onSuccess = { reviews ->
                _uiState.value = _uiState.value.copy(reviews = reviews, isLoading = false, errorMessage = null)
            },
            onError = { message ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
            }
        )
    }

    fun selectCategory(category: ReviewCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun addReview(review: TouristReview, onComplete: (Boolean, String?) -> Unit) {
        _uiState.value = _uiState.value.copy(isSubmitting = true)
        repository.addReview(
            review,
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

    fun markHelpful(reviewId: String) {
        repository.incrementHelpful(reviewId) { message ->
            _uiState.value = _uiState.value.copy(errorMessage = message)
        }
    }

    override fun onCleared() {
        listener?.let(repository::removeListener)
        super.onCleared()
    }
}
