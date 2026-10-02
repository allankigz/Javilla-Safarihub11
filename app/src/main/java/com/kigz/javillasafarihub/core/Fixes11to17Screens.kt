package com.kigz.javillasafarihub.core

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.messaging.FirebaseMessaging
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.kigz.javillasafarihub.ui.theme.SavannahGold
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import kotlinx.coroutines.launch
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

private fun openMaps(context: android.content.Context, query: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        android.widget.Toast.makeText(context, "Unable to open Maps.", android.widget.Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(onBackClick: () -> Unit = {}) {
    val user = try { FirebaseAuth.getInstance().currentUser } catch (_: Exception) { null }
    val db = try { FirebaseDatabase.getInstance().reference.child("bookings") } catch (_: Exception) { null }
    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage
    var service by remember { mutableStateOf("") }
    var serviceId by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var guests by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    var bookings by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                        date = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text(translate("OK", language)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(translate("Cancel", language)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    DisposableEffect(user?.uid) {
        if (user == null || db == null) return@DisposableEffect onDispose { }
        val ref = db.orderByChild("userId").equalTo(user.uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                bookings = s.children.mapNotNull { c ->
                    mapOf("service" to (c.child("service").getValue(String::class.java) ?: ""),
                        "date" to (c.child("date").getValue(String::class.java) ?: ""),
                        "status" to (c.child("status").getValue(String::class.java) ?: "pending"))
                }
            }
            override fun onCancelled(e: DatabaseError) { message = e.message }
        }
        ref.addValueEventListener(listener)
        onDispose { ref.removeEventListener(listener) }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text(translate("Bookings", language), fontWeight = FontWeight.Bold, fontSize = 20.sp) }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { p ->
        LazyColumn(Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text(translate("Request a booking", language), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 22.sp) }
            item { Text(translate("Send a booking request to a verified tourism provider. Payment is not taken by this prototype.", language), fontSize = 14.sp) }
            item { OutlinedTextField(service, { service = it }, Modifier.fillMaxWidth(), label = { Text(translate("Service / experience", language), fontSize = 14.sp) }, placeholder = { Text(translate("e.g. Maasai Mara safari tour", language), fontSize = 14.sp) }, singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedPlaceholderColor = SavannahGold, unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f))) }
            item { OutlinedTextField(serviceId, { serviceId = it }, Modifier.fillMaxWidth(), label = { Text(translate("Verified service ID (optional)", language), fontSize = 14.sp) }, singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedPlaceholderColor = SavannahGold, unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f))) }
            item { 
                Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
                    OutlinedTextField(
                        value = date, 
                        onValueChange = { }, 
                        modifier = Modifier.fillMaxWidth(), 
                        label = { Text(translate("Preferred date", language), fontSize = 14.sp) }, 
                        placeholder = { Text(translate("Select date", language), fontSize = 14.sp) }, 
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = SavannahGold,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedPlaceholderColor = SavannahGold, 
                            unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f)
                        )
                    )
                }
            }
            item { OutlinedTextField(guests, { if (it.all(Char::isDigit)) guests = it }, Modifier.fillMaxWidth(), label = { Text(translate("Guests", language), fontSize = 14.sp) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedPlaceholderColor = SavannahGold, unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f))) }
            item { OutlinedTextField(notes, { notes = it }, Modifier.fillMaxWidth(), label = { Text(translate("Special requests", language), fontSize = 14.sp) }, minLines = 3, colors = OutlinedTextFieldDefaults.colors(focusedPlaceholderColor = SavannahGold, unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f))) }
            message?.let { item { Text(translate(it, language), color = if (it.startsWith("Booking")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, fontSize = 14.sp) } }
            item { Button(onClick = {
                val u = user ?: run { message = "Please sign in before making a booking."; return@Button }
                if (db == null) { message = "Database is unavailable."; return@Button }
                if (service.isBlank() || date.isBlank()) { message = "Service and preferred date are required."; return@Button }
                saving = true
                val id = db.push().key!!
                val data = mapOf("id" to id, "userId" to u.uid, "userName" to (u.displayName ?: "Traveller"), "service" to service.trim(), "serviceId" to serviceId.trim(), "date" to date.trim(), "guests" to (guests.toIntOrNull()?.coerceAtLeast(1) ?: 1), "notes" to notes.trim(), "status" to "pending", "createdAt" to ServerValue.TIMESTAMP)
                db.child(id).setValue(data).addOnSuccessListener { saving = false; message = "Booking request submitted."; service = ""; notes = "" }.addOnFailureListener { saving = false; message = it.localizedMessage ?: "Booking could not be submitted." }
            }, enabled = !saving, modifier = Modifier.fillMaxWidth()) { if (saving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text(translate("Request booking", language), fontSize = 16.sp) } }
            item { Text(translate("My booking requests", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            if (bookings.isEmpty()) item { Text(translate("No booking requests yet.", language), fontSize = 14.sp) }
            items(bookings) { b -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(b["service"].orEmpty(), fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(translate("Date: ", language) + "${b["date"]}", fontSize = 14.sp); Text(translate("Status: ", language) + translate(b["status"].orEmpty(), language), fontSize = 14.sp) } } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedEmergencyScreen(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage
    var location by remember { mutableStateOf("Location not retrieved yet") }
    val client = remember { LocationServices.getFusedLocationProviderClient(context) }
    fun hasLocationPermission(): Boolean {
        val fine = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val coarse = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }
    fun fetchLocation() {
        if (!hasLocationPermission()) { location = "Location permission was not granted."; return }
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { l -> location = if (l == null) "Unable to obtain current location." else "Current coordinates: ${"%.6f".format(l.latitude)}, ${"%.6f".format(l.longitude)}" }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) fetchLocation() else location = "Location permission was not granted."
    }
    fun call(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            android.widget.Toast.makeText(context, translate("Unable to open the phone app.", language), android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold(
        containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text(translate("Emergency SOS", language), fontWeight = FontWeight.Bold, fontSize = 20.sp) }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { p ->
        LazyColumn(Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(translate("Emergency SOS", language), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 22.sp); Text(translate("If you are in immediate danger, move to a safe place and contact emergency services.", language), fontSize = 14.sp); Button(onClick = { call("112") }, Modifier.fillMaxWidth()) { Icon(Icons.Default.Call, null); Spacer(Modifier.width(8.dp)); Text(translate("CALL 112 NOW", language), fontSize = 16.sp) } } } }
            item { OutlinedButton(onClick = { call("999") }, Modifier.fillMaxWidth()) { Text(translate("Call 999", language), fontSize = 16.sp) } }
            item { Text(translate("Location assistance", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            item { Text(translate(location, language)) }
            item { Button(onClick = { permissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)) }, Modifier.fillMaxWidth()) { Icon(Icons.Default.MyLocation, null); Spacer(Modifier.width(8.dp)); Text(translate("Get Current Location", language), fontSize = 16.sp) } }
            item { Text(translate("For non-urgent assistance, use the Safety and Scam Shield modules. Do not treat community reports as confirmed emergencies unless moderated.", language), fontSize = 13.sp, lineHeight = 18.sp) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineGuideScreen(onBackClick: () -> Unit = {}) {
    val language = LocalLanguageManager.current.currentLanguage
    val sections = listOf(
        "Emergency" to "Kenya emergency numbers: 112 and 999. Keep your accommodation address available.",
        "Transport" to "Use reputable licensed providers, agree the fare before departure and verify vehicle/driver details.",
        "Money" to "Keep small KSh notes, protect cards and avoid displaying large amounts of cash in public.",
        "Health" to "Carry essential medication, drink safe water and seek professional medical care for serious symptoms.",
        "Culture" to "Respect local customs, ask before photographing people and protect wildlife by following park rules.",
        "Connectivity" to "Download maps and booking confirmations before travelling into areas with weak network coverage."
    )
    Scaffold(
        containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text(translate("Offline Travel Guide", language), fontWeight = FontWeight.Bold, fontSize = 20.sp) }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { p ->
        LazyColumn(Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text(translate("Essential information available without internet", language), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 22.sp) }
            items(sections) { (title, text) -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { Text(translate(title, language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontSize = 16.sp); Text(translate(text, language), fontSize = 14.sp) } } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsInfoScreen(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage
    var subscribed by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    Scaffold(
        containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text(translate("Notifications & Safety Alerts", language), fontWeight = FontWeight.Bold, fontSize = 20.sp) }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(translate("Safety alerts", language), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text(translate("Javilla Safari Hub is prepared for Firebase Cloud Messaging. Approved safety events can be delivered as push notifications when a Firebase backend sends an alert to the app.", language), fontSize = 14.sp)
            Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text(translate("Alert policy", language), fontWeight = FontWeight.Bold, fontSize = 16.sp); Text(translate("Only moderated/approved safety incidents should be promoted as traveller alerts. Pending allegations remain in the moderation workflow.", language), fontSize = 14.sp) } }
            Text(translate("Notification permission is requested on Android 13+ so safety alerts can be displayed.", language), fontSize = 13.sp)
            Button(onClick = {
                try {
                    if (android.os.Build.VERSION.SDK_INT >= 33) permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    val messaging = try { FirebaseMessaging.getInstance() } catch (_: Exception) { null }
                    @Suppress("DEPRECATION")
                    messaging?.token?.addOnSuccessListener { token ->
                        val currentUser = try { FirebaseAuth.getInstance().currentUser } catch (_: Exception) { null }
                        currentUser?.uid?.let { uid ->
                            try { FirebaseDatabase.getInstance().reference.child("users").child(uid).child("fcmTokens").child(token).setValue(true) } catch (_: Exception) {}
                        }
                    }
                    messaging?.subscribeToTopic("safety_alerts")
                        ?.addOnSuccessListener { subscribed = true }
                        ?.addOnFailureListener { subscribed = false }
                } catch (_: Exception) {
                    subscribed = false
                    android.widget.Toast.makeText(context, translate("Safety alerts are temporarily unavailable.", language), android.widget.Toast.LENGTH_SHORT).show()
                }
            }, Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Notifications, null); Spacer(Modifier.width(8.dp)); Text(if (subscribed) translate("Safety alerts enabled", language) else translate("Enable safety alerts", language), fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTrackerScreen(onBackClick: () -> Unit = {}) {
    val user = try { FirebaseAuth.getInstance().currentUser } catch (_: Exception) { null }
    val language = LocalLanguageManager.current.currentLanguage
    val expensesRef = try { FirebaseDatabase.getInstance().reference.child("expenses") } catch (_: Exception) { null }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Transport") }
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    DisposableEffect(user?.uid) {
        if (user == null || expensesRef == null) return@DisposableEffect onDispose { }
        val q = expensesRef.orderByChild("userId").equalTo(user.uid)
        val listener = object : ValueEventListener { override fun onDataChange(s: DataSnapshot) { expenses = s.children.mapNotNull { c -> Expense(c.key.orEmpty(), c.child("description").getValue(String::class.java).orEmpty(), c.child("amount").getValue(Double::class.java) ?: 0.0, c.child("category").getValue(String::class.java).orEmpty()) } }; override fun onCancelled(e: DatabaseError) { message = e.message } }
        q.addValueEventListener(listener); onDispose { q.removeEventListener(listener) }
    }
    val total = expenses.sumOf { it.amount }
    Scaffold(
        containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text(translate("Budget & Expenses", language), fontWeight = FontWeight.Bold, fontSize = 20.sp) }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { p ->
        LazyColumn(Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text(translate("Total spent: KSh ", language) + "${"%.2f".format(total)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 22.sp) }
            item { OutlinedTextField(description, { description = it }, Modifier.fillMaxWidth(), label = { Text(translate("Expense description", language), fontSize = 14.sp) }, singleLine = true) }
            item { OutlinedTextField(amount, { if (it.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) amount = it }, Modifier.fillMaxWidth(), label = { Text(translate("Amount (KSh)", language), fontSize = 14.sp) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true) }
            item { OutlinedTextField(category, { category = it }, Modifier.fillMaxWidth(), label = { Text(translate("Category", language), fontSize = 14.sp) }, singleLine = true) }
            item { Button(onClick = { 
                val u = user ?: run { message = "Please sign in first."; return@Button }
                if (expensesRef == null) { message = "Database is unavailable."; return@Button }
                val a = amount.toDoubleOrNull()
                if (description.isBlank() || a == null || a <= 0) { message = "Enter a description and a positive amount."; return@Button } 
                val id = expensesRef.push().key!!; 
                expensesRef.child(id).setValue(mapOf("id" to id, "userId" to u.uid, "description" to description.trim(), "amount" to a, "category" to category.trim(), "createdAt" to ServerValue.TIMESTAMP)).addOnSuccessListener { description = ""; amount = ""; message = "Expense recorded." }.addOnFailureListener { message = it.localizedMessage } 
            }, Modifier.fillMaxWidth()) { Text(translate("Add expense", language), fontSize = 16.sp) } }
            message?.let { item { Text(translate(it, language), fontSize = 14.sp) } }
            items(expenses) { e -> Card(Modifier.fillMaxWidth()) { Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) { Column { Text(e.description, fontWeight = FontWeight.Bold, fontSize = 16.sp); Text(translate(e.category, language), fontSize = 12.sp) }; Text("KSh ${"%.2f".format(e.amount)}", fontSize = 14.sp) } } }
        }
    }
}
private data class Expense(val id: String, val description: String, val amount: Double, val category: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    val options = listOf("Licensed taxi / ride-hailing" to "Convenient for city transfers; confirm the destination and fare before departure.", "Safari vehicle / tour van" to "Best for national parks and multi-day safari routes; verify operator and inclusions.", "Train" to "Useful for Nairobi–Mombasa travel; reserve seats through official railway channels.", "Matatu / public bus" to "Lower cost but less predictable; use established terminals and keep valuables secure.")
    Scaffold(
        containerColor = Color.Transparent,
        topBar = { TopAppBar(title = { Text(translate("Transport Planner", language), fontWeight = FontWeight.Bold, fontSize = 20.sp) }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { p ->
        LazyColumn(Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { OutlinedTextField(from, { from = it }, Modifier.fillMaxWidth(), label = { Text(translate("From", language), fontSize = 14.sp) }, singleLine = true) }
            item { OutlinedTextField(to, { to = it }, Modifier.fillMaxWidth(), label = { Text(translate("To", language), fontSize = 14.sp) }, singleLine = true) }
            item { Button(onClick = { if (from.isNotBlank() && to.isNotBlank()) openMaps(context, "$from to $to") }, Modifier.fillMaxWidth(), enabled = from.isNotBlank() && to.isNotBlank()) { Icon(Icons.Default.Map, null); Spacer(Modifier.width(8.dp)); Text(translate("Open route in Maps", language), fontSize = 16.sp) } }
            items(options) { (title, text) -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { Text(translate(title, language), fontWeight = FontWeight.Bold, fontSize = 16.sp); Text(translate(text, language), fontSize = 14.sp) } } }
            item { Text(translate("Safety: use licensed/reputable operators, verify vehicle details and agree fares before travel.", language), fontSize = 13.sp, lineHeight = 18.sp) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceCommunitySummary(serviceId: String, serviceName: String) {
    val language = LocalLanguageManager.current.currentLanguage
    var reviews by remember { mutableIntStateOf(0) }
    var average by remember { mutableDoubleStateOf(0.0) }
    var safetyReports by remember { mutableIntStateOf(0) }
    val isInspectionMode = androidx.compose.ui.platform.LocalInspectionMode.current

    DisposableEffect(serviceId) {
        if (isInspectionMode) return@DisposableEffect onDispose { }
        try {
            val root = FirebaseDatabase.getInstance().reference
            val reviewListener = object : ValueEventListener { override fun onDataChange(s: DataSnapshot) { val rows = s.children.filter { it.child("serviceId").getValue(String::class.java) == serviceId && it.child("moderationStatus").getValue(String::class.java) != "rejected" }; reviews = rows.size; average = if (rows.isEmpty()) 0.0 else rows.mapNotNull { it.child("rating").getValue(Int::class.java)?.toDouble() }.average() }; override fun onCancelled(e: DatabaseError) {} }
            val safetyListener = object : ValueEventListener { override fun onDataChange(s: DataSnapshot) { safetyReports = s.children.count { it.child("serviceId").getValue(String::class.java) == serviceId && it.child("moderationStatus").getValue(String::class.java) == "approved" } }; override fun onCancelled(e: DatabaseError) {} }
            root.child("reviews").addListenerForSingleValueEvent(reviewListener)
            root.child("safetyIncidents").addListenerForSingleValueEvent(safetyListener)
        } catch (_: Exception) {
            // Firebase not initialized
        }
        onDispose { }
    }
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) { Text(translate("Community signals", language), fontWeight = FontWeight.Bold); Text(if (reviews > 0) "★ ${"%.1f".format(average)}/5 · $reviews " + translate("traveller reviews", language) else translate("No linked traveller reviews yet", language)); Text(if (safetyReports > 0) "⚠ $safetyReports " + translate("approved safety report(s)", language) else "✓ " + translate("No approved safety reports linked", language)); Text("$serviceName: " + translate("reviews and safety reports are separate from provider verification.", language), style = MaterialTheme.typography.bodySmall) } }
}
