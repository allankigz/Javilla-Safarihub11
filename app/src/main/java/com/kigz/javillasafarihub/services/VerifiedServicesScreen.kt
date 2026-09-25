package com.kigz.javillasafarihub.services

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.core.ServiceCommunitySummary
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme
import com.kigz.javillasafarihub.ui.theme.SavannahGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifiedServicesScreen(
    onBackClick: () -> Unit = {},
    viewModel: VerifiedServicesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isInspectionMode = LocalInspectionMode.current
    val isUserLoggedIn = remember {
        if (isInspectionMode) false else try {
            FirebaseAuth.getInstance().currentUser != null
        } catch (_: Exception) {
            false
        }
    }

    VerifiedServicesContent(
        uiState = uiState,
        isUserLoggedIn = isUserLoggedIn,
        onBackClick = onBackClick,
        onReportService = viewModel::reportService
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifiedServicesContent(
    uiState: VerifiedServicesUiState,
    isUserLoggedIn: Boolean,
    onBackClick: () -> Unit,
    onReportService: (TouristService, String, String, (Boolean, String?) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage
    var search by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All") }
    var selected by remember { mutableStateOf<TouristService?>(null) }
    var reportService by remember { mutableStateOf<TouristService?>(null) }
    val categories = listOf("All", "Accommodation", "Tour Operator", "Tour Guide", "Transport", "Restaurant", "Experience")
    val filtered = uiState.services.filter { service ->
        val matchesCategory = category == "All" || service.category == category
        val q = search.trim()
        val matchesSearch = q.isBlank() || service.name.contains(q, true) || service.location.contains(q, true) || service.category.contains(q, true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,topBar = {
        TopAppBar(
            title = { Text(translate("Verified Tourist Services", language), fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, null)
                            Spacer(Modifier.width(8.dp))
                            Text(translate("Trusted tourism directory", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Text(translate("You can verify trusted tourism operators and agencies in Kenya through official directories like the Tourism Regulatory Authority.", language), fontSize = 14.sp)
                }
            }
        }
        item {
            OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth(), singleLine = true,
                label = { Text(translate("Search services", language), fontSize = 14.sp) }, leadingIcon = { Icon(Icons.Default.Search, null) },
                placeholder = { Text(translate("Name, category or location", language), fontSize = 14.sp, color = SavannahGold.copy(alpha = 0.8f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedPlaceholderColor = SavannahGold,
                    unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f)
                ))
        }
        item { LazyCategoryRow(categories, category) { category = it } }
        uiState.errorMessage?.let { message ->
            item { Text(message, color = MaterialTheme.colorScheme.error, fontSize = 14.sp) }
        }
        item { Text(translate("Available services (", language) + "${filtered.size})", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            if (uiState.isLoading) {
                item { Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            } else if (filtered.isEmpty()) {
                item { Text(translate("No services match your search.", language)) }
            } else {
                items(filtered, key = { it.id }) { service ->
                    ServiceCard(service, onView = { selected = service }, onReport = { reportService = service })
                }
            }
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, null)
                        Spacer(Modifier.width(8.dp))
                        Text(translate("Verification is not an endorsement.", language))
                    }
                }
            }
        }
    }

    selected?.let { service ->
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(service.name) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (service.verified) Icons.Default.CheckCircle else Icons.Default.Info, null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (service.verified) translate("Verified listing", language) else translate("Verification pending", language), fontWeight = FontWeight.Bold)
                    }
                    Text(translate("Category: ", language) + service.category)
                    Text(translate("Location: ", language) + service.location)
                    Text(translate("Pricing: ", language) + service.priceGuide)
                    Text(service.description)
                    if (service.verificationNote.isNotBlank()) Text(service.verificationNote, style = MaterialTheme.typography.bodySmall)
                    if (service.phone.isNotBlank()) Text(translate("Phone: ", language) + service.phone)
                    if (service.website.isNotBlank()) Text(translate("Website: ", language) + service.website)
                    Text(translate("Safety reminder: confirm the final price, inclusions, cancellation terms and provider identity before payment.", language), style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    ServiceCommunitySummary(serviceId = service.id, serviceName = service.name)
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (service.phone.isNotBlank()) {
                        OutlinedButton(onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${service.phone}"))
                                if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
                                else android.widget.Toast.makeText(context, translate("No phone app is available.", language), android.widget.Toast.LENGTH_SHORT).show()
                            } catch (_: Exception) {
                                android.widget.Toast.makeText(context, translate("Unable to open the phone app.", language), android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.Phone, null); Spacer(Modifier.width(4.dp)); Text(translate("Call", language))
                        }
                    }
                    Button(onClick = { selected = null }) { Text(translate("Close", language)) }
                }
            }
        )
    }

    reportService?.let { service ->
        var reason by remember(service.id) { mutableStateOf("Information is inaccurate") }
        var details by remember(service.id) { mutableStateOf("") }
        val reasons = listOf("Information is inaccurate", "Provider is no longer operating", "Verification appears questionable", "Other")
        AlertDialog(
            onDismissRequest = { if (!uiState.isSubmittingReport) reportService = null },
            title = { Text(translate("Report ", language) + service.name) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(translate("Reports are sent to the moderation queue and are kept separate from reviews and scam allegations.", language))
                    Text(translate("Reason", language), fontWeight = FontWeight.Bold)
                    reasons.forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = reason == option, onClick = { reason = option }, enabled = !uiState.isSubmittingReport)
                            Text(translate(option, language))
                        }
                    }
                    OutlinedTextField(details, { details = it }, Modifier.fillMaxWidth(), minLines = 2, enabled = !uiState.isSubmittingReport,
                        label = { Text(translate("Additional details (optional)", language)) })
                }
            },
            confirmButton = {
                Button(
                    enabled = !uiState.isSubmittingReport && isUserLoggedIn,
                    onClick = {
                        onReportService(service, reason, details.trim()) { success, message ->
                            if (success) {
                                Toast.makeText(context, translate("Service report submitted for review", language), Toast.LENGTH_SHORT).show()
                                reportService = null
                            } else Toast.makeText(context, message ?: translate("Could not submit report", language), Toast.LENGTH_LONG).show()
                        }
                    }
                ) {
                    if (uiState.isSubmittingReport) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text(translate("Submit report", language))
                }
            },
            dismissButton = { TextButton(onClick = { reportService = null }, enabled = !uiState.isSubmittingReport) { Text(translate("Cancel", language)) } }
        )
    }
}

@Composable
private fun LazyCategoryRow(categories: List<String>, selected: String, onSelected: (String) -> Unit) {
    val language = LocalLanguageManager.current.currentLanguage
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories.size) { index ->
            val item = categories[index]
            FilterChip(selected == item, { onSelected(item) }, label = { Text(translate(item, language)) })
        }
    }
}

@Composable
private fun ServiceCard(service: TouristService, onView: () -> Unit, onReport: () -> Unit) {
    val language = LocalLanguageManager.current.currentLanguage
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (service.verified) Icon(Icons.Default.Verified, translate("Verified", language), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Text("${translate(service.category, language)} • ${service.location}", style = MaterialTheme.typography.labelMedium, fontSize = 12.sp)
            Text(service.description, fontSize = 14.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Text(translate("Pricing: ", language) + service.priceGuide, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
            AssistChip(onClick = {}, enabled = false, label = { Text(if (service.verified) translate("Verified", language) else translate("Pending verification", language), fontSize = 11.sp) })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onView, modifier = Modifier.height(36.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)) { Text(translate("View details", language), fontSize = 12.sp) }
                OutlinedButton(onClick = onReport, modifier = Modifier.height(36.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)) { Icon(Icons.Default.Flag, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text(translate("Report", language), fontSize = 12.sp) }
            }
        }
    }
}
