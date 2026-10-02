package com.kigz.javillasafarihub.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate

private data class MapDestination(
    val name: String,
    val position: LatLng,
    val description: String
)

private val kenyaDestinations = listOf(
    MapDestination("Nairobi", LatLng(-1.2921, 36.8219), "Kenya's capital city"),
    MapDestination("Maasai Mara", LatLng(-1.4937, 35.1439), "Wildlife and Great Migration"),
    MapDestination("Amboseli", LatLng(-2.6527, 37.2606), "Elephants and Kilimanjaro views"),
    MapDestination("Lake Nakuru", LatLng(-0.3031, 36.0800), "Rhinos, flamingos and wildlife"),
    MapDestination("Diani Beach", LatLng(-4.2840, 39.5742), "White-sand beach and water activities"),
    MapDestination("Mount Kenya", LatLng(-0.1521, 37.3084), "Hiking and mountain scenery"),
    MapDestination("Lamu Old Town", LatLng(-2.2717, 40.9020), "Swahili culture and heritage"),
    MapDestination("Hell's Gate", LatLng(-0.9060, 36.3060), "Hiking, cycling and cliffs"),
    MapDestination("Lake Naivasha", LatLng(-0.7667, 36.3667), "Boat rides and bird watching")
)

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleMapsScreen(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage
    val nairobi = LatLng(-1.2921, 36.8219)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(nairobi, 6f)
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            LocationServices.getFusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        userLocation = LatLng(location.latitude, location.longitude)
                    }
                }
        }
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(translate("Safari Map", language)) },
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
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(myLocationButtonEnabled = hasLocationPermission, zoomControlsEnabled = true)
            ) {
                kenyaDestinations.forEach { destination ->
                    Marker(
                        state = MarkerState(position = destination.position),
                        title = translate(destination.name, language),
                        snippet = translate(destination.description, language)
                    )
                }

                userLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = translate("Your location", language),
                        snippet = translate("Last known device location", language)
                    )
                }
            }

            if (!hasLocationPermission) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    tonalElevation = 4.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(translate("Allow location to show your position on the map.", language))
                        Button(onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }) {
                            Text(translate("Allow Location", language))
                        }
                    }
                }
            }
        }
    }
}
