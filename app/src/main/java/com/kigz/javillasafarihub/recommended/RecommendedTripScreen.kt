package com.kigz.javillasafarihub.recommended

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.data.repository.RecommendedTripRepository
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.model.RecommendedTrip
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedTripsScreen(
    onBackClick: () -> Unit = {},
    onPlanTripClick: (RecommendedTrip) -> Unit = {}
) {
    val language = LocalLanguageManager.current.currentLanguage
    val trips = RecommendedTripRepository.getRecommendedTrips()

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = translate("Recommended Trips", language),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = translate("Recommended Trips", language),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = translate("Explore safari trips recommended for your adventure.", language),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            items(trips) { trip ->
                RecommendedTripCard(
                    trip = trip,
                    onPlanTripClick = { onPlanTripClick(trip) }
                )
            }
        }
    }
}

@Composable
fun RecommendedTripCard(
    trip: RecommendedTrip,
    onPlanTripClick: () -> Unit = {}
) {
    val language = LocalLanguageManager.current.currentLanguage

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = trip.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = trip.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp)
            )

            Column(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(
                    text = translate("Location:", language) + " ${trip.location}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = translate("Duration:", language) + " ${trip.duration}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = translate("Estimated Price: KSh", language) + " ${trip.estimatedPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Text(
                text = translate("Activities", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )

            trip.activities.forEach { activity ->
                Text(
                    text = "• $activity",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPlanTripClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = translate("Plan This Trip", language))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecommendedTripsScreenPreview() {
    JavillaSafariHubTheme {
        RecommendedTripsScreen()
    }
}
