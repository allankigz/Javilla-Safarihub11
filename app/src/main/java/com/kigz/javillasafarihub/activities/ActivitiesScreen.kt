package com.kigz.javillasafarihub.activities

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.ui.theme.SavannahGold
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kigz.javillasafarihub.data.repository.ActivityRepository
import com.kigz.javillasafarihub.model.Activity
import com.kigz.javillasafarihub.model.ActivityCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(onBackClick: () -> Unit = {}) {
    val language = LocalLanguageManager.current.currentLanguage
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ActivityCategory?>(null) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var contentVersion by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        ActivityRepository.observeContentChanges { contentVersion++ }
        onDispose { ActivityRepository.clearContentObserver() }
    }

    val allActivities = ActivityRepository.getActivities()
    contentVersion.hashCode()

    val filteredActivities = allActivities.filter { activity ->
        val matchesSearch = searchText.isBlank() ||
                activity.name.contains(searchText, ignoreCase = true) ||
                activity.location.contains(searchText, ignoreCase = true)

        val matchesCategory = selectedCategory == null || activity.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = translate("Safari Activities", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = translate("Explore Activities", language),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 16.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Text(
                text = translate("Discover exciting things to do during your safari.", language),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 14.sp
            )

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search activities")
                },
                placeholder = {
                    Text(
                        text = translate("Search activities...", language),
                        fontSize = 14.sp,
                        color = SavannahGold.copy(alpha = 0.8f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedPlaceholderColor = SavannahGold,
                    unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f)
                )
            )

            ExposedDropdownMenuBox(
                expanded = categoryMenuExpanded,
                onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name
                        ?.replace("_", " ")
                        ?.lowercase()
                        ?.replaceFirstChar { it.uppercase() }
                        ?: translate("All Activities", language),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth(),
                    label = {
                        Text(translate("Filter by category", language), color = SavannahGold.copy(alpha = 0.8f))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = SavannahGold,
                        unfocusedLabelColor = SavannahGold.copy(alpha = 0.7f)
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = { categoryMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(translate("All Activities", language)) },
                        onClick = {
                            selectedCategory = null
                            categoryMenuExpanded = false
                        }
                    )

                    ActivityCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    category.name
                                        .replace("_", " ")
                                        .lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                )
                            },
                            onClick = {
                                selectedCategory = category
                                categoryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (searchText.isNotBlank() || selectedCategory != null) {
                    Button(
                        onClick = {
                            searchText = ""
                            selectedCategory = null
                        }
                    ) {
                        Text(translate("Clear Filters", language))
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredActivities) { activity ->
                    ActivityRepositoryCard(activity = activity)
                }

                if (filteredActivities.isEmpty()) {
                    item {
                        Text(
                            text = translate("No activities found.", language),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityRepositoryCard(activity: Activity) {
    val language = LocalLanguageManager.current.currentLanguage
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = activity.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = translate("Location:", language) + " ${activity.location}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 13.sp
            )

            Text(
                text = translate("Rating:", language) + " ${activity.rating} ★",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 13.sp
            )

            Text(
                text = translate("Price: KSh", language) + " ${activity.price}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = translate("Best time:", language) + " ${activity.bestTimeToVisit}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val context = LocalContext.current
            OutlinedButton(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(activity.name + " " + activity.location)}")).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        Toast.makeText(context, translate("Unable to open location.", language), Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(translate("View Location on Map", language))
            }
        }
    }
}
