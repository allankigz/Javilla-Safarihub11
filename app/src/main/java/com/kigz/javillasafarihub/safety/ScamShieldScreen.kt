package com.kigz.javillasafarihub.safety

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.ui.theme.SavannahGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScamShieldScreen(
    onBackClick: () -> Unit = {},
    viewModel: ScamReportsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ScamShieldContent(
        state = state,
        onBackClick = onBackClick,
        onRefresh = viewModel::refresh,
        onSubmitReport = viewModel::submitReport,
        onConfirmReport = { id, _ -> viewModel.confirmReport(id) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScamShieldContent(
    state: ScamReportsUiState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit = {},
    onSubmitReport: (String, String, String, (Boolean, String?) -> Unit) -> Unit,
    onConfirmReport: (String, (String?) -> Unit) -> Unit
) {
    val language = LocalLanguageManager.current.currentLanguage
    var reportText by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Overcharging") }
    var showSubmittedDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(translate("Scam Shield & Reports", language), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = translate("Live updates", language))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = SavannahGold)
                            Spacer(Modifier.width(8.dp))
                            Text(translate("Travel with confidence", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SavannahGold)
                        }
                        Text(translate("Community reports help travellers identify possible scams, overcharging and harassment. Reports are shared only after submission and can be moderated.", language), color = SavannahGold)
                    }
                }
            }
            item { Text(translate("Before You Pay", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            item { SafetyTip(translate("Check the total price and what is included before accepting a service.", language)) }
            item { SafetyTip(translate("Prefer verified accommodation, guides, transport providers and businesses.", language)) }
            item { SafetyTip(translate("Keep receipts, booking confirmations and payment references.", language)) }
            item { SafetyTip(translate("If something feels unsafe, leave the situation and use Emergency & Safety.", language)) }

            item { Text(translate("Community Safety Reports", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontSize = 18.sp) }

            if (state.isLoading) {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { CircularProgressIndicator() }
                }
            } else if (state.reports.isEmpty()) {
                item { Text(translate("No community reports yet. Be the first to share a useful warning.", language)) }
            } else {
                items(state.reports, key = { it.id }) { report ->
                    ScamReportCard(report = report, onConfirm = { onConfirmReport(report.id) {} })
                }
            }

            state.errorMessage?.let { message ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(message)
                        }
                    }
                }
            }

            item { Text(translate("Report a Problem", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(translate("Select a category", language), fontSize = 14.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Overcharging", "Scam", "Harassment").forEach { category ->
                            if (selectedCategory == category) Button(onClick = { selectedCategory = category }) { Text(translate(category, language), fontSize = 12.sp) }
                            else OutlinedButton(onClick = { selectedCategory = category }) { Text(translate(category, language), fontSize = 12.sp) }
                        }
                    }
                    OutlinedTextField(
                        value = locationText,
                        onValueChange = { locationText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text(translate("Where did it happen?", language)) },
                        placeholder = { Text(translate("e.g. Diani Beach, Nairobi CBD, Maasai Mara", language), color = SavannahGold.copy(alpha = 0.8f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedPlaceholderColor = SavannahGold,
                            unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f)
                        )
                    )
                    OutlinedTextField(
                        value = reportText,
                        onValueChange = { reportText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        label = { Text(translate("Describe what happened", language)) },
                        placeholder = { Text(translate("Give factual details. Do not share passwords, card details or other secrets.", language), color = SavannahGold.copy(alpha = 0.8f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedPlaceholderColor = SavannahGold,
                            unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f)
                        )
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSubmitting && locationText.trim().length >= 2 && reportText.trim().length >= 10,
                        onClick = {
                            onSubmitReport(selectedCategory, locationText, reportText) { success, _ ->
                                if (success) {
                                    reportText = ""
                                    locationText = ""
                                    showSubmittedDialog = true
                                }
                            }
                        }
                    ) {
                        if (state.isSubmitting) CircularProgressIndicator(strokeWidth = 2.dp)
                        else {
                            Icon(Icons.Default.Flag, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(translate("Submit Report", language))
                        }
                    }
                }
            }

            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(translate("Reports are stored in Firebase Realtime Database and marked pending for moderation. This module keeps allegations separate from verified reviews and does not present a report as proven fact.", language))
                    }
                }
            }
        }
    }

    if (showSubmittedDialog) {
        AlertDialog(
            onDismissRequest = { showSubmittedDialog = false },
            title = { Text(translate("Report submitted", language)) },
            text = { Text(translate("Thank you. Your report has been shared with the community as pending moderation. Please use Emergency & Safety if you are in immediate danger.", language)) },
            confirmButton = { TextButton(onClick = { showSubmittedDialog = false }) { Text(translate("OK", language)) } }
        )
    }
}

@Composable
private fun ScamReportCard(report: ScamReport, onConfirm: () -> Unit) {
    val language = LocalLanguageManager.current.currentLanguage
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ReportProblem, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(translate(report.category, language), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Text(report.location, style = MaterialTheme.typography.labelMedium, color = SavannahGold, fontSize = 12.sp)
            Text(report.description, fontSize = 14.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)
            Text(translate("Reported by ", language) + "${report.reporterName} • ${if (report.moderationStatus == "approved") translate("Community verified", language) else translate("Pending moderation", language)}", style = MaterialTheme.typography.labelSmall, fontSize = 11.sp)
            OutlinedButton(onClick = onConfirm, enabled = report.moderationStatus == "approved", modifier = Modifier.height(36.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (report.moderationStatus == "approved") translate("I experienced this too (", language) + "${report.confirmedCount})" else translate("Pending moderation", language), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SafetyTip(text: String) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp), tint = SavannahGold)
            Spacer(Modifier.width(10.dp))
            Text(text, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}
