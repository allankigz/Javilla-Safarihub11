package com.kigz.javillasafarihub.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun CurrentLocationScreen(onBackClick: () -> Unit = {}) {

    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage

    var locationText by remember {
        mutableStateOf(translate("Location not available", language))
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            hasLocationPermission =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }

    LaunchedEffect(hasLocationPermission) {

        if (hasLocationPermission) {

            val fusedLocationClient =
                com.google.android.gms.location.LocationServices
                    .getFusedLocationProviderClient(context)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->

                    if (location != null) {

                        locationText =
                            "Latitude: ${location.latitude}\n" +
                                    "Longitude: ${location.longitude}"

                    } else {

                        locationText =
                            translate("Current location is unavailable.", language)
                    }
                }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(translate("Current Location", language), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = translate("Current Location", language),
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = locationText,
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 24.dp
                )
            )

            if (!hasLocationPermission) {

                Button(
                    onClick = {

                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                ) {

                    Text(translate("Allow Location", language))
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CurrentLocationScreenPreview() {

    JavillaSafariHubTheme {
        CurrentLocationScreen()
    }
}
