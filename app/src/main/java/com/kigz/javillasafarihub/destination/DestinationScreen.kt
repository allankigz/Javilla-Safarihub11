package com.kigz.javillasafarihub.destination

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.data.repository.DestinationRepository
import com.kigz.javillasafarihub.model.Destination

private fun openDestinationWebsite(context: Context, url: String, language: String) {
    if (url.isBlank()) {
        Toast.makeText(context, translate("No website available for this destination.", language), Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, translate("Unable to open this destination link.", language), Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationScreen(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage
    var searchQuery by remember { mutableStateOf("") }
    var favoriteVersion by remember { mutableIntStateOf(0) }
    val repository = remember { DestinationRepository() }
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }
    var contentVersion by remember { mutableIntStateOf(0) }

    DisposableEffect(repository) {
        repository.observeContentChanges { contentVersion++ }
        onDispose { repository.clearContentObserver(); repository.close() }
    }

    val destinations = repository.getDestinations()
    contentVersion.hashCode()
    val filteredDestinations = if (searchQuery.isBlank()) {
        destinations
    } else {
        repository.searchDestinations(searchQuery)
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(text = translate("Kenya Destinations", language), fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(text = translate("Discover Kenya", language), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = translate("Explore amazing destinations, wildlife, beaches, mountains and cultural experiences across Kenya.", language), style = MaterialTheme.typography.bodyLarge, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search destinations") },
                    placeholder = { Text(text = translate("Search destinations...", language), fontSize = 14.sp) },
                    shape = RoundedCornerShape(14.dp)
                )
            }

            if (filteredDestinations.isEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(55.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = translate("No destinations found", language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = translate("Try searching for another destination.", language), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                items(items = filteredDestinations, key = { it.id }) { destination ->
                    DestinationCard(
                        destination = destination,
                        onFavoriteClick = {
                            repository.toggleFavorite(destination.id)
                            favoriteVersion++
                        },
                        onClick = { selectedDestination = destination },
                        onWebsiteClick = { openDestinationWebsite(context, destination.websiteUrl, language) },
                        onDetailsClick = { selectedDestination = destination }
                    )
                }
            }
        }
    }

    selectedDestination?.let { destination ->
        AlertDialog(
            onDismissRequest = { selectedDestination = null },
            title = { Text(destination.name, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(destination.location, fontWeight = FontWeight.SemiBold)
                    Text(destination.description)
                    Text(translate("Best time:", language) + " ${destination.bestTimeToVisit}")
                    Text(translate("Rating:", language) + " ${destination.rating}")
                    if (destination.entryFee > 0.0) {
                        Text(translate("Entry Fee:", language) + " KES ${destination.entryFee}")
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        try {
                            val mapUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(destination.name + " " + destination.location)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(mapIntent)
                        } catch (_: Exception) {
                            Toast.makeText(context, translate("Unable to open map.", language), Toast.LENGTH_SHORT).show()
                        }
                    }) { Text(translate("View on Map", language)) }

                    if (destination.websiteUrl.isNotBlank()) {
                        TextButton(onClick = {
                            openDestinationWebsite(context, destination.websiteUrl, language)
                            selectedDestination = null
                        }) { Text(translate("Visit Website", language)) }
                    }
                }
            },
            dismissButton = { TextButton(onClick = { selectedDestination = null }) { Text(translate("Close", language)) } }
        )
    }
}

@Composable
fun DestinationCard(
    destination: Destination,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    onWebsiteClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    val language = LocalLanguageManager.current.currentLanguage
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)).background(Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer))), contentAlignment = Alignment.Center) {
                if (destination.imageUrl.isNotBlank()) {
                    AsyncImage(model = destination.imageUrl, contentDescription = destination.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(imageVector = Icons.Default.Terrain, contentDescription = destination.name, modifier = Modifier.size(70.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Box(modifier = Modifier.align(Alignment.TopStart).padding(12.dp).background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), shape = RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Text(text = destination.category.name.replace("_", " "), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onFavoriteClick, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                    Icon(imageVector = if (destination.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = if (destination.isFavorite) "Remove from favorites" else "Add to favorites", tint = if (destination.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = destination.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = destination.location, style = MaterialTheme.typography.bodyMedium, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = destination.description, style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Rating", modifier = Modifier.size(20.dp), tint = Color(0xFFFFB300))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = destination.rating.toString(), fontWeight = FontWeight.Bold)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onDetailsClick) {
                            Text(text = translate("Details", language), color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = onWebsiteClick) {
                            Text(text = translate("Visit Website", language), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
