package com.kigz.javillasafarihub.recommended

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kigz.javillasafarihub.data.repository.RecommendedTripRepository
import com.kigz.javillasafarihub.model.RecommendedTrip
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RecommendedTripsScreen() {

    val trips = RecommendedTripRepository.getRecommendedTrips()

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recommended Trips",
                    )
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
                        text = "Recommended Trips",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = "Explore safari trips recommended for your adventure.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            items(trips) { trip ->

                RecommendedTripCard(
                    trip = trip
                )
            }
        }
    }
}

@Composable
fun RecommendedTripCard(
    trip: RecommendedTrip
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = trip.name,
                style = MaterialTheme.typography.titleLarge
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
                    text = "Location: ${trip.location}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Duration: ${trip.duration}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "Estimated Price: KSh ${trip.estimatedPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Text(
                text = "Activities",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp)
            )

            trip.activities.forEach { activity ->

                Text(
                    text = "• $activity",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
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