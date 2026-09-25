package com.kigz.javillasafarihub.budget

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.ui.theme.SavannahGold
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetCalculatorScreen(
    onBackClick: () -> Unit = {},
    onBudgetCalculated: (Double) -> Unit = {}
) {
    val language = LocalLanguageManager.current.currentLanguage
    var travelers by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var accommodationCost by remember { mutableStateOf("") }
    var foodCost by remember { mutableStateOf("") }
    var transportCost by remember { mutableStateOf("") }
    var activitiesCost by remember { mutableStateOf("") }
    var otherExpenses by remember { mutableStateOf("") }

    val numberOfTravelers = travelers.toDoubleOrNull() ?: 0.0
    val numberOfDays = days.toDoubleOrNull() ?: 0.0
    val accommodationPerDay = accommodationCost.toDoubleOrNull() ?: 0.0
    val foodPerDay = foodCost.toDoubleOrNull() ?: 0.0
    val transport = transportCost.toDoubleOrNull() ?: 0.0
    val activities = activitiesCost.toDoubleOrNull() ?: 0.0
    val other = otherExpenses.toDoubleOrNull() ?: 0.0

    val totalAccommodation = numberOfTravelers * numberOfDays * accommodationPerDay
    val totalFood = numberOfTravelers * numberOfDays * foodPerDay
    val totalTripCost = totalAccommodation + totalFood + transport + activities + other
    val costPerTraveler = if (numberOfTravelers > 0) totalTripCost / numberOfTravelers else 0.0

    fun resetBudget() {
        travelers = ""; days = ""; accommodationCost = ""; foodCost = ""; transportCost = ""; activitiesCost = ""; otherExpenses = ""
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(text = translate("Safari Budget Calculator", language), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = translate("Plan Your Kenya Safari Budget", language), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = translate("Enter your estimated travel expenses below to calculate the total cost of your trip in Kenyan Shillings (KSh).", language), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            BudgetInputField(value = travelers, onValueChange = { travelers = it }, label = translate("Number of Travelers", language), placeholder = translate("e.g. 2", language), icon = { Icon(imageVector = Icons.Default.People, contentDescription = "Travelers") })
            BudgetInputField(value = days, onValueChange = { days = it }, label = translate("Number of Days", language), placeholder = translate("e.g. 5", language), icon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Days") })
            BudgetInputField(value = accommodationCost, onValueChange = { accommodationCost = it }, label = translate("Accommodation Cost per Person per Day", language), placeholder = translate("e.g. 5000", language), icon = { Icon(imageVector = Icons.Default.Hotel, contentDescription = "Accommodation") })
            BudgetInputField(value = foodCost, onValueChange = { foodCost = it }, label = translate("Food Cost per Person per Day", language), placeholder = translate("e.g. 2000", language), icon = { Icon(imageVector = Icons.Default.Restaurant, contentDescription = "Food") })
            BudgetInputField(value = transportCost, onValueChange = { transportCost = it }, label = translate("Transport Cost", language), placeholder = translate("e.g. 15000", language), icon = { Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = "Transport") })
            BudgetInputField(value = activitiesCost, onValueChange = { activitiesCost = it }, label = translate("Activities Cost", language), placeholder = translate("e.g. 10000", language), icon = { Icon(imageVector = Icons.Default.Explore, contentDescription = "Activities") })
            BudgetInputField(value = otherExpenses, onValueChange = { otherExpenses = it }, label = translate("Other Expenses", language), placeholder = translate("e.g. 5000", language), icon = { Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = "Other expenses") })

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onBudgetCalculated(totalTripCost) }, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Default.Calculate, contentDescription = "Calculate")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = translate("Calculate Budget", language))
            }
            Button(onClick = { resetBudget() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = translate("Reset Budget", language))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = translate("Estimated Trip Cost", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    BudgetResultRow(label = translate("Accommodation", language), amount = totalAccommodation)
                    BudgetResultRow(label = translate("Food", language), amount = totalFood)
                    BudgetResultRow(label = translate("Transport", language), amount = transport)
                    BudgetResultRow(label = translate("Activities", language), amount = activities)
                    BudgetResultRow(label = translate("Other Expenses", language), amount = other)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = translate("TOTAL", language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = formatCurrency(totalTripCost), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Text(text = translate("Estimated cost per traveler: ", language) + formatCurrency(costPerTraveler), style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BudgetInputField(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String, icon: @Composable () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue -> if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) onValueChange(newValue) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label, fontSize = 14.sp) },
        placeholder = { Text(text = placeholder, color = SavannahGold.copy(alpha = 0.7f), fontSize = 14.sp) },
        leadingIcon = icon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(focusedLabelColor = SavannahGold, unfocusedLabelColor = SavannahGold.copy(alpha = 0.8f), focusedPlaceholderColor = SavannahGold, unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.6f))
    )
}

@Composable
fun BudgetResultRow(label: String, amount: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label)
        Text(text = formatCurrency(amount), fontWeight = FontWeight.Medium)
    }
}

fun formatCurrency(amount: Double): String {
    return "KSh ${"%,.2f".format(amount)}"
}
