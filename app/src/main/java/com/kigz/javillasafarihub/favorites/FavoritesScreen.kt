package com.kigz.javillasafarihub.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.model.Destination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favorites: List<Destination>,
    onDestinationClick: (Destination) -> Unit = {},
    onRemoveFavorite: (Destination) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val language = LocalLanguageManager.current.currentLanguage
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(text = translate("My Favorites", language), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            EmptyFavorites(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites) { destination ->
                    FavoriteDestinationCard(
                        destination = destination,
                        onClick = { onDestinationClick(destination) },
                        onRemove = { onRemoveFavorite(destination) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteDestinationCard(
    destination: Destination,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val language = LocalLanguageManager.current.currentLanguage
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = destination.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = destination.location,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = translate("Rating:", language) + " ${destination.rating}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyFavorites(
    modifier: Modifier = Modifier
) {
    val language = LocalLanguageManager.current.currentLanguage
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = translate("No favorite destinations", language),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = translate("Save destinations you would like to visit.", language),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
