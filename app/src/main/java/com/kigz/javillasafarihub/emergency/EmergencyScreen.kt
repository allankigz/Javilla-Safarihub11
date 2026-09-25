package com.kigz.javillasafarihub.emergency

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.core.content.ContextCompat
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

private data class EmergencyContact(
    val title: String,
    val number: String,
    val description: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage

    val contacts = remember {
        listOf(
            EmergencyContact(
                title = "National Emergency Services",
                number = "999",
                description = "Police, ambulance and urgent emergency assistance"
            ),
            EmergencyContact(
                title = "Emergency Services",
                number = "112",
                description = "European-style emergency number supported in Kenya"
            )
        )
    }

    var locationMessage by remember {
        mutableStateOf("Share your current location with a trusted person.")
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted =
                (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                    (permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true)

            if (granted) {
                shareCurrentLocation(context) { message ->
                    locationMessage = message
                }
            } else {
                locationMessage = "Location permission was not granted."
            }
        }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = translate("Emergency & Safety", language),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = translate("Back", language)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                modifier = Modifier.size(34.dp)
                            )

                            Spacer(modifier = Modifier.size(12.dp))

                            Text(
                                text = translate("Need urgent help?", language),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = translate("Use the emergency numbers below or share your location with someone you trust.", language)
                        )
                    }
                }
            }

            item {
                Text(
                    text = translate("Emergency Contacts", language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(contacts) { contact ->
                EmergencyContactCard(
                    contact = contact,
                    language = language
                ) {
                    dialNumber(context, contact.number, language)
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            Text(
                                text = translate("Share My Location", language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(text = translate(locationMessage, language))

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(translate("Get & Share Current Location", language))
                        }
                    }
                }
            }

            item {
                Text(
                    text = translate("Safety Tools", language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SafetyInfoCard(
                    icon = Icons.Default.LocalHospital,
                    title = translate("Medical Assistance", language),
                    description = translate("For serious medical emergencies, call 999 or 112 and state your exact location.", language)
                )
            }

            item {
                SafetyInfoCard(
                    icon = Icons.Default.Security,
                    title = translate("Personal Safety", language),
                    description = translate("Move to a safe public place, avoid confrontation and contact emergency services when necessary.", language)
                )
            }

            item {
                SafetyInfoCard(
                    icon = Icons.Default.Share,
                    title = translate("Trusted Contact", language),
                    description = translate("Use the location-sharing function to send your current position to a friend, family member or travel companion.", language)
                )
            }
        }
    }
}

@Composable
private fun EmergencyContactCard(
    contact: EmergencyContact,
    language: String,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = translate(contact.title, language),
                    fontWeight = FontWeight.Bold
                )
                Text(text = contact.number)
                Text(
                    text = translate(contact.description, language),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(onClick = onCall) {
                Text(translate("Call", language))
            }
        }
    }
}

@Composable
private fun SafetyInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description)
            }
        }
    }
}

private fun dialNumber(
    context: Context,
    number: String,
    language: String
) {
    val intent = Intent(
        Intent.ACTION_DIAL,
        "tel:$number".toUri()
    )
    try {
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            android.widget.Toast.makeText(context, translate("No phone app is available on this device.", language), android.widget.Toast.LENGTH_SHORT).show()
        }
    } catch (_: Exception) {
        android.widget.Toast.makeText(context, translate("Unable to open the phone app.", language), android.widget.Toast.LENGTH_SHORT).show()
    }
}

@android.annotation.SuppressLint("MissingPermission")
private fun shareCurrentLocation(
    context: Context,
    onResult: (String) -> Unit
) {
    val hasPermission =
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED

    if (!hasPermission) {
        onResult("Location permission was not granted.")
        return
    }

    val client = LocationServices.getFusedLocationProviderClient(context)

    client.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        null
    ).addOnSuccessListener { location ->
        if (location == null) {
            onResult("Unable to obtain your current location. Try again outdoors or with GPS enabled.")
            return@addOnSuccessListener
        }

        val latitude = location.latitude
        val longitude = location.longitude
        val mapsUri = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude".toUri()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "My current location in Kenya: $mapsUri"
            )
        }

        try {
            val chooser = Intent.createChooser(shareIntent, "Share my location")
            if (chooser.resolveActivity(context.packageManager) != null) {
                context.startActivity(chooser)
                onResult("Location found. Choose a messaging or sharing app to send it.")
            } else {
                onResult("No sharing app is available on this device.")
            }
        } catch (_: Exception) {
            onResult("Unable to open the sharing menu.")
        }
    }.addOnFailureListener {
        onResult("Could not obtain your location. Check that GPS/location services are enabled.")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmergencyScreenPreview() {
    JavillaSafariHubTheme {
        EmergencyScreen()
    }
}
