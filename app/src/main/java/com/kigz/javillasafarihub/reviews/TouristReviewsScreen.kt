package com.kigz.javillasafarihub.reviews

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristReviewsScreen(
    onBackClick: () -> Unit,
    viewModel: TouristReviewsViewModel = viewModel()
) {
    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage
    val uiState by viewModel.uiState.collectAsState()
    var showWriteDialog by remember { mutableStateOf(false) }
    val filters = ReviewCategory.entries
    val visibleReviews = if (uiState.selectedCategory == ReviewCategory.ALL) uiState.reviews
        else uiState.reviews.filter { it.category == uiState.selectedCategory }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(translate("Tourist Reviews & Ratings", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(translate("Community reviews stored securely online", language), style = MaterialTheme.typography.bodyMedium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Spacer(Modifier.height(8.dp))
            Button(onClick = { showWriteDialog = true }, modifier = Modifier.fillMaxWidth(), enabled = try { FirebaseAuth.getInstance().currentUser != null } catch(_:Exception){ false }) {
                Text(if (try { FirebaseAuth.getInstance().currentUser == null } catch(_:Exception){ true }) translate("Sign in to Write a Review", language) else translate("Write a Review", language))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                filters.forEach { filter ->
                    FilterChip(selected = uiState.selectedCategory == filter, onClick = { viewModel.selectCategory(filter) }, label = { Text(translate(filter.label, language)) })
                }
            }
            Spacer(Modifier.height(12.dp))
            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (visibleReviews.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(translate("No reviews available yet.", language)) }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(visibleReviews, key = { it.id }) { review ->
                        ReviewCard(review = review, onHelpful = { viewModel.markHelpful(review.id) })
                    }
                }
            }
        }
    }

    if (showWriteDialog) {
        WriteReviewDialog(
            isSubmitting = uiState.isSubmitting,
            onDismiss = { if (!uiState.isSubmitting) showWriteDialog = false },
            onSubmit = { place, serviceId, rating, text ->
                val currentUser = try { FirebaseAuth.getInstance().currentUser } catch (_: Exception) { null }
                val newReview = TouristReview(
                    id = "pending",
                    authorName = currentUser?.displayName ?: "Traveller",
                    title = "Traveller Review",
                    comment = text,
                    rating = rating,
                    category = ReviewCategory.SERVICE,
                    location = place,
                    serviceId = serviceId,
                    verified = false,
                    helpfulCount = 0,
                    createdAtLabel = "Recently"
                )
                viewModel.addReview(newReview) { success, message ->
                    if (success) {
                        showWriteDialog = false
                        Toast.makeText(context, translate("Review submitted for moderation", language), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, message ?: translate("Could not submit review", language), Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}

@Composable
private fun ReviewCard(review: TouristReview, onHelpful: () -> Unit) {
    val language = LocalLanguageManager.current.currentLanguage
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.location, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (review.verified) Icon(Icons.Default.Verified, translate("Verified", language), tint = MaterialTheme.colorScheme.primary)
            }
            Text("${review.authorName} • ${translate(review.category.label, language)}", style = MaterialTheme.typography.bodySmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index -> Icon(Icons.Default.Star, translate("Rating", language), tint = if (index < review.rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline) }
                Text("  ${review.rating}/5")
            }
            Spacer(Modifier.height(4.dp))
            Text(review.comment)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onHelpful) { Icon(Icons.Default.ThumbUp, translate("Helpful", language)) }
                Text("${review.helpfulCount} " + translate(" travellers found this helpful", language), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun WriteReviewDialog(isSubmitting: Boolean, onDismiss: () -> Unit, onSubmit: (String, String, Int, String) -> Unit) {
    val language = LocalLanguageManager.current.currentLanguage
    var place by remember { mutableStateOf("") }
    var serviceId by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(translate("Write a Review", language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = place, onValueChange = { place = it }, label = { Text(translate("Place or service", language)) }, singleLine = true, enabled = !isSubmitting)
                OutlinedTextField(value = serviceId, onValueChange = { serviceId = it }, label = { Text(translate("Verified service ID (optional)", language)) }, singleLine = true, enabled = !isSubmitting)
                Text(translate("Rating: ", language) + "$rating / 5")
                Row {
                    (1..5).forEach { value -> IconButton(onClick = { rating = value }, enabled = !isSubmitting) { Icon(Icons.Default.Star, "$value " + translate(" stars", language), tint = if (value <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline) } }
                }
                OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text(translate("Your experience", language)) }, minLines = 3, enabled = !isSubmitting)
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(place.trim(), serviceId.trim(), rating, text.trim()) }, enabled = !isSubmitting && place.isNotBlank() && text.trim().length >= 10) {
                if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp) else Text(translate("Submit", language))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !isSubmitting) { Text(translate("Cancel", language)) } }
    )
}
