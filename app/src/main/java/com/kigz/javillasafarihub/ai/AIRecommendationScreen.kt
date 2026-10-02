package com.kigz.javillasafarihub.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIRecommendationsScreen(
    generatedBudget: Double = 0.0,
    onBackClick: () -> Unit = {},
    onPlanTrip: (String) -> Unit = {}
) {

    var budget by remember {
        mutableStateOf(
            if (generatedBudget > 0) {
                generatedBudget.toString()
            } else {
                ""
            }
        )
    }

    var days by remember {
        mutableStateOf("")
    }

    var travelers by remember {
        mutableStateOf("1")
    }

    var selectedInterest by remember {
        mutableStateOf("Wildlife")
    }

    var selectedTravelStyle by remember {
        mutableStateOf("Balanced")
    }

    var showInterestMenu by remember {
        mutableStateOf(false)
    }

    var showStyleMenu by remember {
        mutableStateOf(false)
    }

    var recommendations by remember {
        mutableStateOf(
            emptyList<SafariRecommendation>()
        )
    }

    fun generate() {

        recommendations =
            RecommendationEngine.generateRecommendations(

                RecommendationPreferences(

                    budget =
                        budget.toDoubleOrNull()
                            ?: 0.0,

                    days =
                        days.toIntOrNull()
                            ?: 0,

                    travelers =
                        travelers.toIntOrNull()
                            ?: 1,

                    interest =
                        selectedInterest,

                    travelStyle =
                        selectedTravelStyle
                )
            )
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "AI Recommendations",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription =
                                "Back"
                        )
                    }
                },

                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }

    ) { paddingValues ->

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // =================================================
            // HEADER
            // =================================================

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(20.dp)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.AutoAwesome,
                            contentDescription =
                                "AI",
                            modifier =
                                Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text =
                                "Smart Safari Recommendations",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Tell us what you want from your trip " +
                                    "and we will recommend destinations " +
                                    "that best match your preferences."
                    )
                }
            }

            // =================================================
            // BUDGET
            // =================================================

            OutlinedTextField(

                value = budget,

                onValueChange = { value ->

                    if (
                        value.isEmpty() ||
                        value.matches(
                            Regex("^\\d*\\.?\\d*$")
                        )
                    ) {
                        budget = value
                    }
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Budget (KSh)")
                },

                placeholder = {
                    Text("e.g. 50000")
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Decimal
                    ),

                singleLine = true
            )

            // =================================================
            // DAYS
            // =================================================

            OutlinedTextField(

                value = days,

                onValueChange = { value ->

                    if (
                        value.isEmpty() ||
                        value.all {
                            it.isDigit()
                        }
                    ) {
                        days = value
                    }
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Trip Duration (Days)")
                },

                placeholder = {
                    Text("e.g. 5")
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default.CalendarMonth,
                        contentDescription =
                            "Days"
                    )
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ),

                singleLine = true
            )

            // =================================================
            // TRAVELERS
            // =================================================

            OutlinedTextField(

                value = travelers,

                onValueChange = { value ->

                    if (
                        value.isEmpty() ||
                        value.all {
                            it.isDigit()
                        }
                    ) {
                        travelers = value
                    }
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Number of Travelers")
                },

                placeholder = {
                    Text("e.g. 2")
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default.People,
                        contentDescription =
                            "Travelers"
                    )
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ),

                singleLine = true
            )

            // =================================================
            // INTEREST
            // =================================================

            Column {

                Button(
                    onClick = {
                        showInterestMenu =
                            !showInterestMenu
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Explore,
                        contentDescription =
                            "Interest"
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            "Interest: $selectedInterest"
                    )
                }

                DropdownMenu(

                    expanded =
                        showInterestMenu,

                    onDismissRequest = {
                        showInterestMenu = false
                    }
                ) {

                    listOf(
                        "Wildlife",
                        "Beach",
                        "Adventure",
                        "Culture",
                        "Photography"
                    ).forEach { interest ->

                        DropdownMenuItem(

                            text = {
                                Text(interest)
                            },

                            onClick = {

                                selectedInterest =
                                    interest

                                showInterestMenu =
                                    false
                            }
                        )
                    }
                }
            }

            // =================================================
            // TRAVEL STYLE
            // =================================================

            Column {

                Button(
                    onClick = {
                        showStyleMenu =
                            !showStyleMenu
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.DirectionsCar,
                        contentDescription =
                            "Travel style"
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            "Style: $selectedTravelStyle"
                    )
                }

                DropdownMenu(

                    expanded =
                        showStyleMenu,

                    onDismissRequest = {
                        showStyleMenu = false
                    }
                ) {

                    listOf(
                        "Budget",
                        "Balanced",
                        "Luxury",
                        "Adventure"
                    ).forEach { style ->

                        DropdownMenuItem(

                            text = {
                                Text(style)
                            },

                            onClick = {

                                selectedTravelStyle =
                                    style

                                showStyleMenu =
                                    false
                            }
                        )
                    }
                }
            }

            // =================================================
            // GENERATE BUTTON
            // =================================================

            Button(

                onClick = {
                    generate()
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector =
                        Icons.Default.AutoAwesome,
                    contentDescription =
                        "Generate"
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        "Generate Recommendations"
                )
            }

            // =================================================
            // RESET
            // =================================================

            if (recommendations.isNotEmpty()) {

                Button(

                    onClick = {
                        recommendations =
                            emptyList()
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Refresh,
                        contentDescription =
                            "Reset"
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text = "Clear Recommendations"
                    )
                }
            }

            // =================================================
            // RESULTS
            // =================================================

            if (recommendations.isNotEmpty()) {

                Text(

                    text =
                        "Recommended for You",

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,

                    fontWeight =
                        FontWeight.Bold
                )

                recommendations.forEach { recommendation ->

                    RecommendationCard(

                        recommendation =
                            recommendation,

                        onPlanTrip = {

                            onPlanTrip(
                                recommendation.destination
                            )
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun RecommendationCard(
    recommendation: SafariRecommendation,
    onPlanTrip: () -> Unit
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
    ) {

        Column(

            modifier =
                Modifier.padding(18.dp),

            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(

                    text =
                        recommendation.destination,

                    modifier =
                        Modifier.weight(1f),

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(

                    text =
                        "${recommendation.matchScore}% Match",

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )
            }

            Text(
                text =
                    recommendation.location
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Star,
                    contentDescription =
                        "Rating"
                )

                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )

                Text(
                    text =
                        "${recommendation.rating} / 5.0"
                )
            }

            Text(
                text =
                    recommendation.reason
            )

            Text(
                text =
                    "Activities: ${
                        recommendation.activities.joinToString(
                            ", "
                        )
                    }"
            )

            Text(

                text =
                    "Estimated Cost: ${
                        formatRecommendationCurrency(
                            recommendation.estimatedCost
                        )
                    }",

                fontWeight =
                    FontWeight.Bold
            )

            Button(

                onClick =
                    onPlanTrip,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Plan This Trip"
                )
            }
        }
    }
}
fun formatRecommendationCurrency(
    amount: Double
): String {

    return "KSh ${"%,.0f".format(amount)}"
}
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AIRecommendationScreenPreview() {

    JavillaSafariHubTheme {

        AIRecommendationsScreen(
            onBackClick = {},
            onPlanTrip = {}
        )
    }

}
@Preview(
    showBackground = true
)
@Composable
fun RecommendationCardPreview() {

    JavillaSafariHubTheme {

        RecommendationCard(

            recommendation =
                SafariRecommendation(

                    destination =
                        "Maasai Mara",

                    location =
                        "Narok County",

                    reason =
                        "Excellent wildlife viewing and cultural experiences.",

                    activities =
                        listOf(
                            "Game Drives",
                            "Wildlife Photography",
                            "Maasai Village Visit"
                        ),

                    estimatedCost =
                        65000.0,

                    rating =
                        4.8,

                    matchScore =
                        95
                ),
            onPlanTrip = {}
        )
    }
}