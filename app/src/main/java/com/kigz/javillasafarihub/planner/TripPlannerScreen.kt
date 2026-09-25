package com.kigz.javillasafarihub.planner

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
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Save
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import java.text.SimpleDateFormat
import java.util.*
import com.kigz.javillasafarihub.ui.theme.SavannahGold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.data.model.SavedTrip
import com.kigz.javillasafarihub.data.repository.CloudTripRepository
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlannerScreen(
    generatedBudget: Double = 0.0,
    onBackClick: () -> Unit = {},
    onBudgetClick: () -> Unit = {}
) {
    val language = LocalLanguageManager.current.currentLanguage

    var tripName by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var travelers by remember { mutableStateOf("") }
    var activities by remember { mutableStateOf("") }
    var transport by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDatePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                        startDate = sdf.format(Date(it))
                    }
                    showStartDatePicker = false
                }) { Text(translate("OK", language)) }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text(translate("Cancel", language)) }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDatePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                        endDate = sdf.format(Date(it))
                    }
                    showEndDatePicker = false
                }) { Text(translate("OK", language)) }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text(translate("Cancel", language)) }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }

    var tripSaved by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember(context) { SafariDatabase.getDatabase(context).savedTripDao() }
    val cloudTripRepository = remember { CloudTripRepository() }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(text = translate("Trip Planner", language), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = translate("Plan Your Kenya Safari", language),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = translate("Create a personalized safari itinerary by entering your trip details below.", language)
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = tripName,
                onValueChange = { tripName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(translate("Trip Name", language)) },
                placeholder = { Text(translate("e.g. Maasai Mara Safari", language)) },
                singleLine = true
            )

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(translate("Destination", language)) },
                placeholder = { Text(translate("e.g. Maasai Mara", language)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Explore, contentDescription = "Destination")
                },
                singleLine = true
            )

            Box(modifier = Modifier.fillMaxWidth().clickable { showStartDatePicker = true }) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(translate("Start Date", language)) },
                    placeholder = { Text(translate("Select start date", language), color = SavannahGold.copy(alpha = 0.8f)) },
                    readOnly = true,
                    enabled = false,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Start date")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = SavannahGold,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Box(modifier = Modifier.fillMaxWidth().clickable { showEndDatePicker = true }) {
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(translate("End Date", language)) },
                    placeholder = { Text(translate("Select end date", language), color = SavannahGold.copy(alpha = 0.8f)) },
                    readOnly = true,
                    enabled = false,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "End date")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = SavannahGold,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            OutlinedTextField(
                value = travelers,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        travelers = newValue
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(translate("Number of Travelers", language)) },
                placeholder = { Text(translate("e.g. 2", language)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.People, contentDescription = "Travelers")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = activities,
                onValueChange = { activities = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(translate("Activities", language)) },
                placeholder = { Text(translate("e.g. Game drives, hiking, bird watching", language)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Explore, contentDescription = "Activities")
                },
                minLines = 2
            )

            OutlinedTextField(
                value = transport,
                onValueChange = { transport = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(translate("Transport", language)) },
                placeholder = { Text(translate("e.g. Safari van, rental car", language)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = "Transport")
                },
                singleLine = true
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = translate("Estimated Budget", language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (generatedBudget > 0.0) {
                        Text(
                            text = formatPlannerCurrency(generatedBudget),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = translate("Generated from the Safari Budget Calculator.", language),
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text(
                            text = translate("No budget generated yet.", language),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = translate("Use the Budget Calculator to estimate your trip cost.", language),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(translate("Notes", language)) },
                placeholder = { Text(translate("Add any additional information...", language)) },
                leadingIcon = {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Notes, contentDescription = "Notes")
                },
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = translate("Trip Summary", language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (tripName.isNotBlank()) {
                        Text(text = translate("Trip:", language) + " $tripName")
                    }
                    if (destination.isNotBlank()) {
                        Text(text = translate("Destination:", language) + " $destination")
                    }
                    if (startDate.isNotBlank()) {
                        Text(text = translate("Start:", language) + " $startDate")
                    }
                    if (endDate.isNotBlank()) {
                        Text(text = translate("End:", language) + " $endDate")
                    }
                    if (travelers.isNotBlank()) {
                        Text(text = translate("Travelers:", language) + " $travelers")
                    }
                    if (generatedBudget > 0.0) {
                        Text(
                            text = translate("Budget:", language) + " ${formatPlannerCurrency(generatedBudget)}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val people = travelers.toIntOrNull() ?: 1
                    scope.launch {
                        val trip = SavedTrip(
                            tripName = tripName.trim(),
                            destination = destination.trim(),
                            startDate = startDate.trim(),
                            endDate = endDate.trim(),
                            travelers = people.toString(),
                            activities = activities.trim(),
                            transport = transport.trim(),
                            estimatedBudget = generatedBudget,
                            notes = notes.trim()
                        )
                        dao.insertTrip(trip)
                        cloudTripRepository.saveTrip(trip)
                        tripSaved = true
                        saveMessage = "Trip saved to your account and this device."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = tripName.isNotBlank() && destination.isNotBlank()
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save trip")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = translate("Save Trip", language))
            }

            if (tripSaved) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (saveMessage.isBlank()) translate("Your safari trip has been saved.", language) else translate(saveMessage, language))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

fun formatPlannerCurrency(amount: Double): String {
    return "KSh ${"%,.2f".format(amount)}"
}
