package com.kigz.javillasafarihub.offline

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme

data class OfflineGuideItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val details: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineTravelScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var downloaded by remember { mutableStateOf(true) }
    var selectedItem by remember { mutableStateOf<OfflineGuideItem?>(null) }

    val guideItems = listOf(
        OfflineGuideItem("Emergency Essentials", "Important numbers and actions when you have no internet.", Icons.Default.LocalHospital, listOf("Emergency numbers: 999 or 112", "Keep your accommodation address available offline", "Share your location when mobile data is available", "For urgent danger, move to a safe public place and contact authorities")),
        OfflineGuideItem("Safety Checklist", "Practical reminders for safer travel around Kenya.", Icons.Default.Security, listOf("Keep valuables secure and avoid displaying large amounts of cash", "Confirm prices before accepting a service", "Use verified service providers where possible", "Avoid isolated areas after dark unless accompanied by a trusted guide")),
        OfflineGuideItem("Kenya Travel Basics", "Quick reference information for first-time visitors.", Icons.Default.LocationOn, listOf("Capital: Nairobi", "Major tourism regions: Nairobi, Maasai Mara, Coast, Amboseli, Tsavo, Rift Valley and Western Kenya", "Currency: Kenyan Shilling (KES)", "Carry a backup payment method and keep important booking details offline")),
        OfflineGuideItem("Useful Swahili", "Common phrases for everyday travel conversations.", Icons.Default.Language, listOf("Jambo — Hello", "Asante — Thank you", "Tafadhali — Please", "Samahani — Excuse me / Sorry", "Bei gani? — What price?", "Nisaidie — Help me"))
    )

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Offline Travel Guide") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { padding ->
        selectedItem?.let { item ->
            OfflineDetailContent(item, padding) { selectedItem = null }
        } ?: run {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WifiOff, "Offline")
                                Spacer(Modifier.padding(6.dp))
                                Text("Travel without internet", style = MaterialTheme.typography.titleLarge)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Keep essential Kenya travel information available when network coverage is weak or unavailable.")
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {
                                downloaded = true
                                Toast.makeText(context, "Offline Kenya guide is ready on this device", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.CloudDownload, "Download")
                                Spacer(Modifier.padding(4.dp))
                                Text(if (downloaded) "Guide Ready" else "Save Offline Guide")
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Offline essentials", style = MaterialTheme.typography.titleLarge)
                }
                items(guideItems) { item ->
                    Card(onClick = { selectedItem = item }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(item.icon, item.title)
                            Spacer(Modifier.padding(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(item.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(item.description, style = MaterialTheme.typography.bodyMedium)
                            }
                            Icon(Icons.Default.CheckCircle, "Available offline")
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    Text("Production note: this module is the local-first foundation. Future versions can download destination packs, maps, verified services and emergency resources for selected regions.", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun OfflineDetailContent(item: OfflineGuideItem, padding: androidx.compose.foundation.layout.PaddingValues, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            Text(item.title, style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                item.details.forEachIndexed { index, detail ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
                        Text("•", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.padding(5.dp))
                        Text(detail, style = MaterialTheme.typography.bodyLarge)
                    }
                    if (index < item.details.lastIndex) HorizontalDivider()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OfflineTravelScreenPreview() {
    JavillaSafariHubTheme {
        OfflineTravelScreen(onBackClick = {})
    }
}
