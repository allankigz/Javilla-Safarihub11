package com.kigz.javillasafarihub.planner

import com.kigz.javillasafarihub.data.model.SavedTrip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import kotlinx.coroutines.launch
import com.kigz.javillasafarihub.data.repository.CloudTripRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedTripsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val database = remember { SafariDatabase.getDatabase(context) }
    val repository = remember { TripRepository(database.savedTripDao()) }
    val cloudRepository = remember { CloudTripRepository() }
    val scope = rememberCoroutineScope()
    val trips by cloudRepository.getTrips().collectAsState(initial = emptyList())

    SavedTripsContent(
        trips = trips,
        onBackClick = onBackClick,
        onDeleteTrip = { trip ->
            scope.launch {
                repository.deleteTrip(trip)
                cloudRepository.deleteTrip(trip)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedTripsContent(
    trips: List<SavedTrip>,
    onBackClick: () -> Unit,
    onDeleteTrip: (SavedTrip) -> Unit
) {
    val language = LocalLanguageManager.current.currentLanguage
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(text = translate("Saved Trips", language), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (trips.isEmpty()) {
            EmptySavedTripsContent(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = translate("Your Safari Trips", language),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = translate("Trips you have saved in Javilla Safari Hub.", language))

                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(items = trips) { trip ->
                    SavedTripCard(
                        trip = trip,
                        onDelete = { onDeleteTrip(trip) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySavedTripsContent(
    modifier: Modifier = Modifier
) {
    val language = LocalLanguageManager.current.currentLanguage
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "No saved trips",
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = translate("No Saved Trips", language),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = translate("Create a trip using the Trip Planner and save it here.", language)
        )
    }
}

@Composable
fun SavedTripCard(
    trip: SavedTrip,
    onDelete: () -> Unit
) {
    val language = LocalLanguageManager.current.currentLanguage
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.tripName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete trip")
                }
            }

            Row {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Destination")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = trip.destination)
            }

            Row {
                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Dates")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${trip.startDate} - ${trip.endDate}")
            }

            Row {
                Icon(imageVector = Icons.Default.People, contentDescription = "Travelers")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${trip.travelers} " + translate("traveler(s)", language))
            }

            Text(
                text = translate("Budget:", language) + " ${formatPlannerCurrency(trip.estimatedBudget)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (trip.activities.isNotBlank()) {
                Text(text = translate("Activities:", language) + " ${trip.activities}")
            }

            if (trip.transport.isNotBlank()) {
                Text(text = translate("Transport:", language) + " ${trip.transport}")
            }

            if (trip.notes.isNotBlank()) {
                Text(text = translate("Notes:", language) + " ${trip.notes}")
            }
        }
    }
}
