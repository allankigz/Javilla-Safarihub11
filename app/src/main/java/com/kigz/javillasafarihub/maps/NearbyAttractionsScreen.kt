@file:OptIn(ExperimentalMaterial3Api::class)

package com.kigz.javillasafarihub.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.net.toUri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme
import kotlin.math.*


data class NearbyAttraction(
    val name: String,
    val location: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

private val attractions = listOf(
    NearbyAttraction("Maasai Mara", "Narok County", "Famous for wildlife viewing and the Great Migration.", -1.4061, 35.0089),
    NearbyAttraction("Amboseli National Park", "Kajiado County", "Known for elephants and spectacular views of Mount Kilimanjaro.", -2.6527, 37.2606),
    NearbyAttraction("Nairobi National Park", "Nairobi", "A wildlife park located close to Kenya's capital city.", -1.3733, 36.8589),
    NearbyAttraction("Diani Beach", "Kwale County", "A popular coastal destination with beautiful beaches and marine activities.", -4.2778, 39.5942),
    NearbyAttraction("Lake Naivasha", "Nakuru County", "A scenic freshwater lake offering boat rides and wildlife experiences.", -0.7172, 36.4310),
    NearbyAttraction("Lake Nakuru National Park", "Nakuru County", "A protected area famous for wildlife and scenic landscapes.", -0.3031, 36.0800)
)

@Composable
fun NearbyAttractionsScreen(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLon by remember { mutableStateOf<Double?>(null) }
    var status by remember { mutableStateOf("Location is required to sort attractions by distance.") }

    fun loadLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            status = "Please allow location access to find attractions near you."
            return
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    userLat = location.latitude
                    userLon = location.longitude
                    status = "Showing attractions sorted by distance from your location."
                } else {
                    status = "Unable to get your current location. Turn on Location and try again."
                }
            }
            .addOnFailureListener { status = "Unable to get your location. Please try again." }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            loadLocation()
        } else status = "Location permission was denied."
    }

    LaunchedEffect(Unit) { loadLocation() }

    val sortedAttractions = remember(userLat, userLon) {
        val lat = userLat
        val lon = userLon
        if (lat != null && lon != null) {
            attractions.map { it to distanceKm(lat, lon, it.latitude, it.longitude) }
                .sortedBy { it.second }
        } else attractions.map { it to null }
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Nearby Attractions") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Explore Nearby Attractions", style = MaterialTheme.typography.headlineSmall)
                Text(
                    status,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
                if (userLat == null) {
                    Button(onClick = {
                        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                    }) { Text("Enable Location") }
                } else {
                    OutlinedButton(onClick = { loadLocation() }) { Text("Refresh Location") }
                }
            }
            items(sortedAttractions, key = { it.first.name }) { (attraction, distance) ->
                NearbyAttractionCard(attraction, distance)
            }
        }
    }
}

@Composable
private fun NearbyAttractionCard(attraction: NearbyAttraction, distanceKm: Double?) {
    val context = LocalContext.current
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Icon(Icons.Default.LocationOn, contentDescription = "Location")
            Text(attraction.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
            Text(attraction.location, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 4.dp))
            Text(attraction.description, style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
            if (distanceKm != null) {
                Text(formatDistance(distanceKm), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            }
            Button(
                onClick = {
                    val uri = "google.navigation:q=${attraction.latitude},${attraction.longitude}".toUri()
                    val mapsIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage("com.google.android.apps.maps")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try {
                        context.startActivity(mapsIntent)
                    } catch (_: Exception) {
                        try {
                            val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${attraction.latitude},${attraction.longitude}")).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(fallback)
                        } catch (_: Exception) {
                            android.widget.Toast.makeText(context, "Unable to open navigation.", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Navigate")
            }
        }
    }
}

private fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
    return earthRadiusKm * 2 * atan2(sqrt(a), sqrt(1 - a))
}

private fun formatDistance(km: Double): String = if (km < 1) "${round(km * 1000).toInt()} m away" else "${"%.1f".format(km)} km away"

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NearbyAttractionsScreenPreview() { JavillaSafariHubTheme { NearbyAttractionsScreen() } }
