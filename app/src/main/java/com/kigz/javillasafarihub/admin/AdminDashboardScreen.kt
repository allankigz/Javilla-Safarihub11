package com.kigz.javillasafarihub.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate

private data class ModerationItem(val id:String, val type:String, val title:String, val detail:String, val status:String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(onBackClick: () -> Unit) {
    val language = LocalLanguageManager.current.currentLanguage
    val auth = remember { try { FirebaseAuth.getInstance() } catch(_:Exception){ null } }
    val db = remember { try { FirebaseDatabase.getInstance().reference } catch(_:Exception){ null } }
    var isAdmin by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var items by remember { mutableStateOf(emptyList<ModerationItem>()) }
    var filter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        val uid = auth?.currentUser?.uid
        if (uid == null || db == null) { loading = false; error = if (uid == null) translate("Please sign in.", language) else translate("Database unavailable.", language); return@LaunchedEffect }
        db.child("admins").child(uid).get().addOnSuccessListener { snap ->
            isAdmin = snap.getValue(Boolean::class.java) == true
            loading = false
            if (!isAdmin) error = translate("Admin access is required.", language)
        }.addOnFailureListener { loading = false; error = it.localizedMessage ?: translate("Could not verify admin access.", language) }
    }

    fun refresh() {
        if (!isAdmin || db == null) return
        loading = true
        val nodes = listOf("reviews" to "Review", "scamReports" to "Scam report", "safetyIncidents" to "Safety incident", "serviceReports" to "Service report", "bookings" to "Booking")
        var remaining = nodes.size
        val collected = mutableListOf<ModerationItem>()
        nodes.forEach { (node, type) ->
            db.child(node).get().addOnSuccessListener { snap ->
                snap.children.forEach { c ->
                    val status = c.child("moderationStatus").getValue(String::class.java)
                        ?: c.child("status").getValue(String::class.java) ?: "pending"
                    if (status == "pending") {
                        val title = when (type) {
                            "Review" -> c.child("title").getValue(String::class.java) ?: translate("Tourist review", language)
                            "Scam report" -> c.child("category").getValue(String::class.java) ?: translate("Scam report", language)
                            "Safety incident" -> c.child("category").getValue(String::class.java) ?: translate("Safety incident", language)
                            "Booking" -> c.child("service").getValue(String::class.java) ?: translate("Booking request", language)
                            else -> c.child("serviceName").getValue(String::class.java) ?: translate("Service report", language)
                        }
                        val detail = c.child("description").getValue(String::class.java)
                            ?: c.child("details").getValue(String::class.java) ?: c.child("comment").getValue(String::class.java).orEmpty()
                        collected.add(ModerationItem(c.key.orEmpty(), type, title, detail, status))
                    }
                }
                remaining--
                if (remaining == 0) { items = collected.sortedBy { it.type }; loading = false }
            }.addOnFailureListener { remaining--; if (remaining == 0) { loading = false; error = it.localizedMessage } }
        }
    }

    LaunchedEffect(isAdmin) { if (isAdmin) refresh() }

    fun moderate(item: ModerationItem, approved: Boolean) {
        if (db == null) return
        val status = if (approved) "approved" else "rejected"
        val updates = when (item.type) {
            "Review" -> mapOf("moderationStatus" to status, "verified" to approved)
            "Scam report" -> mapOf("moderationStatus" to status)
            "Safety incident" -> mapOf("moderationStatus" to status)
            "Booking" -> mapOf("status" to if (approved) "confirmed" else "rejected")
            else -> mapOf("status" to if (approved) "reviewed" else "rejected")
        }
        val node = when (item.type) {
            "Review" -> "reviews"; "Scam report" -> "scamReports"; "Safety incident" -> "safetyIncidents"; "Booking" -> "bookings"; else -> "serviceReports"
        }
        db.child(node).child(item.id).updateChildren(updates).addOnSuccessListener {
            items = items.filterNot { it.id == item.id && it.type == item.type }
        }.addOnFailureListener { error = it.localizedMessage ?: translate("Could not update item.", language) }
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(translate("Admin Dashboard", language)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp)) }
            if (!isAdmin && !loading) {
                Text(translate("This area is restricted to authorized Javilla Safari Hub administrators.", language), fontWeight = FontWeight.Medium)
                return@Column
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("All", "Review", "Scam report", "Safety incident", "Service report", "Booking").forEach { f ->
                    FilterChip(selected = filter == f, onClick = { filter = f }, label = { Text(translate(f, language)) })
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(translate("Pending moderation: ", language) + "${items.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items.filter { filter == "All" || it.type == filter }, key = { "${it.type}:${it.id}" }) { item ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) {
                            Text(translate(item.type, language), style = MaterialTheme.typography.labelMedium)
                            Text(item.title, fontWeight = FontWeight.Bold)
                            if (item.detail.isNotBlank()) Text(item.detail, maxLines = 4, modifier = Modifier.padding(vertical = 6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { moderate(item, true) }) { Text(translate("Approve", language)) }
                                OutlinedButton(onClick = { moderate(item, false) }) { Text(translate("Reject", language)) }
                            }
                        }
                    }
                }
                if (items.isEmpty()) item { Text(translate("No pending items.", language), modifier = Modifier.padding(12.dp)) }
            }
        }
    }
}
