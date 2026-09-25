@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.kigz.javillasafarihub.safety

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import kotlin.math.*

@Composable
fun TouristSafetyScreen(
    onBackClick: () -> Unit,
    viewModel: SafetyIncidentsViewModel = viewModel()
) {
    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage
    val uiState by viewModel.uiState.collectAsState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLon by remember { mutableStateOf<Double?>(null) }
    var locationStatus by remember { mutableStateOf("Enable location to see verified safety alerts near you.") }
    var showIncidentDialog by remember { mutableStateOf(false) }

    fun loadLocation() {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) { locationStatus = "Location permission is not enabled."; return }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    userLat = location.latitude; userLon = location.longitude
                    locationStatus = "Safety alerts are filtered to about 25 km around your location."
                } else locationStatus = "Could not get your location. Turn on Location and try again."
            }
            .addOnFailureListener { locationStatus = "Unable to get your location. Please try again." }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) loadLocation()
        else locationStatus = "Location permission was denied."
    }
    LaunchedEffect(Unit) { loadLocation() }

    val nearbyAlerts = remember(uiState.incidents, userLat, userLon) {
        val uLat = userLat
        val uLon = userLon
        if (uLat == null || uLon == null) emptyList()
        else uiState.incidents.mapNotNull { incident ->
            val lat = incident.latitude ?: return@mapNotNull null
            val lon = incident.longitude ?: return@mapNotNull null
            if (incident.moderationStatus != "approved") return@mapNotNull null
            val distance = distanceKm(uLat, uLon, lat, lon)
            if (distance <= 25.0) incident to distance else null
        }.sortedBy { it.second }
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,topBar = {
        TopAppBar(
            title = { Text(translate("Tourist Safety & Assistance", language), fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, "Emergency")
                            Spacer(Modifier.width(8.dp))
                            Text(translate("Emergency assistance", language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Text(translate("If you are in immediate danger, contact the appropriate local emergency service directly. This app does not replace emergency services.", language))
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(translate("Nearby safety alerts", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(translate(locationStatus, language))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (userLat == null) Button(onClick = { permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) }) { Text(translate("Enable Location", language)) }
                            else OutlinedButton(onClick = { loadLocation() }) { Text(translate("Refresh Location", language)) }
                        }
                        Text(translate("Only reports approved by moderation are shown as alerts. Reports under review are not presented as confirmed incidents.", language), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            if (uiState.errorMessage != null) item { Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error) }
            if (userLat != null) {
                item { Text(translate("Verified alerts within 25 km", language) + " (${nearbyAlerts.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                if (nearbyAlerts.isEmpty()) item { Text(translate("No approved location-aware safety alerts were found nearby.", language)) }
                else items(nearbyAlerts, key = { it.first.id }) { (incident, distance) ->
                    SafetyAlertCard(incident, distance, onConfirm = { viewModel.confirm(incident.id) })
                }
            }
            item { Text(translate("Safety Services", language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            item { SafetyCard(translate("Police Assistance", language), translate("Contact police when you experience theft, assault, threats or other criminal incidents.", language), Icons.Default.Security) }
            item { SafetyCard(translate("Medical Assistance", language), translate("Seek professional medical help for illness, injury or medical emergencies.", language), Icons.Default.LocalHospital) }
            item { SafetyCard(translate("Transport Safety", language), translate("Use reputable transport providers and confirm prices before beginning a journey.", language), Icons.Default.DirectionsCar) }
            item { SafetyCard(translate("Scam Awareness", language), translate("Be cautious of unsolicited offers, unclear prices and requests for unusual payments.", language), Icons.Default.Warning) }
            item { SafetyCard(translate("Wildlife Safety", language), translate("Follow your guide's instructions and maintain safe distances from wildlife.", language), Icons.Default.Pets) }
            item {
                Button(onClick = { showIncidentDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Report, null); Spacer(Modifier.width(8.dp)); Text(translate("Report a Safety Incident", language))
                }
            }
        }
    }

    if (showIncidentDialog) {
        ReportIncidentDialog(
            isSubmitting = uiState.isSubmitting,
            initialLat = userLat,
            initialLon = userLon,
            onDismiss = { if (!uiState.isSubmitting) showIncidentDialog = false },
            onSubmit = { category, location, description, serviceId, lat, lon ->
                if (FirebaseAuth.getInstance().currentUser == null) {
                    Toast.makeText(context, translate("Please sign in before reporting an incident.", language), Toast.LENGTH_LONG).show()
                } else {
                    viewModel.submit(category, location, description, serviceId, lat, lon) { success, message ->
                        Toast.makeText(context, if (success) translate("Incident submitted for moderation", language) else (message ?: translate("Could not submit incident", language)), Toast.LENGTH_LONG).show()
                        if (success) showIncidentDialog = false
                    }
                }
            }
        )
    }
}

@Composable
private fun SafetyAlertCard(incident: SafetyIncident, distance: Double, onConfirm: () -> Unit) {
    val language = LocalLanguageManager.current.currentLanguage
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, null)
                Spacer(Modifier.width(8.dp))
                Text(incident.category.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text("${formatDistance(distance)} " + translate("away • ", language) + incident.location, style = MaterialTheme.typography.labelMedium)
            Text(incident.description)
            Text(translate("Confirmed by travellers: ", language) + "${incident.confirmedCount}", style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = onConfirm) { Icon(Icons.Default.ThumbUp, null); Spacer(Modifier.width(6.dp)); Text(translate("I experienced this too", language)) }
        }
    }
}

@Composable
private fun SafetyCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, null); Spacer(Modifier.width(12.dp))
            Column { Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(4.dp)); Text(description) }
        }
    }
}

@Composable
private fun ReportIncidentDialog(
    isSubmitting: Boolean,
    initialLat: Double?,
    initialLon: Double?,
    onDismiss: () -> Unit,
    onSubmit: (IncidentCategory, String, String, String, Double?, Double?) -> Unit
) {
    val language = LocalLanguageManager.current.currentLanguage
    var selectedCategory by remember { mutableStateOf(IncidentCategory.OTHER) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var serviceId by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(translate("Report a Safety Incident", language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(translate("Reports are reviewed before they can become nearby safety alerts.", language))
                IncidentCategory.entries.forEach { category ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedCategory == category, onClick = { selectedCategory = category }, enabled = !isSubmitting)
                        Text(category.label)
                    }
                }
                OutlinedTextField(location, { location = it }, Modifier.fillMaxWidth(), singleLine = true, enabled = !isSubmitting, label = { Text(translate("Location / landmark", language)) })
                OutlinedTextField(serviceId, { serviceId = it }, Modifier.fillMaxWidth(), singleLine = true, enabled = !isSubmitting, label = { Text(translate("Verified service ID (optional)", language)) })
                OutlinedTextField(description, { description = it }, Modifier.fillMaxWidth(), minLines = 3, enabled = !isSubmitting, label = { Text(translate("Describe what happened", language)) })
                if (initialLat != null && initialLon != null) Text(translate("Your current coordinates will be attached to this report to support location-aware alerts.", language), style = MaterialTheme.typography.bodySmall)
                else Text(translate("Enable location before submitting if you want this report to support nearby alerts.", language), style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(enabled = !isSubmitting && location.isNotBlank() && description.trim().length >= 10, onClick = { onSubmit(selectedCategory, location.trim(), description.trim(), serviceId.trim(), initialLat, initialLon) }) {
                if (isSubmitting) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text(translate("Submit", language))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !isSubmitting) { Text(translate("Cancel", language)) } }
    )
}

private fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
    return earthRadiusKm * 2 * atan2(sqrt(a), sqrt(1 - a))
}

private fun formatDistance(km: Double): String = if (km < 1.0) "${(km * 1000).roundToInt()} m" else "${"%.1f".format(km)} km"
