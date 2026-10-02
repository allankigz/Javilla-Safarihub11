package com.kigz.javillasafarihub.home

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.LocalThemeManager
import com.kigz.javillasafarihub.localization.translate
import com.kigz.javillasafarihub.activities.ActivitiesScreen
import com.kigz.javillasafarihub.ai.AIRecommendationsScreen
import com.kigz.javillasafarihub.budget.BudgetCalculatorScreen
import com.kigz.javillasafarihub.core.BookingScreen
import com.kigz.javillasafarihub.core.BudgetTrackerScreen
import com.kigz.javillasafarihub.core.EnhancedEmergencyScreen
import com.kigz.javillasafarihub.core.NotificationsInfoScreen
import com.kigz.javillasafarihub.core.OfflineGuideScreen
import com.kigz.javillasafarihub.core.TransportScreen
import com.kigz.javillasafarihub.destination.DestinationScreen
import com.kigz.javillasafarihub.favorites.FavoritesScreen
import com.kigz.javillasafarihub.data.repository.DestinationRepository
import com.kigz.javillasafarihub.data.repository.ActivityRepository
import com.kigz.javillasafarihub.emergency.EmergencyScreen
import com.kigz.javillasafarihub.maps.GoogleMapsScreen
import com.kigz.javillasafarihub.planner.SavedTripsScreen
import com.kigz.javillasafarihub.planner.TripPlannerScreen
import com.kigz.javillasafarihub.profile.ProfileScreen
import com.kigz.javillasafarihub.recommended.RecommendedTripsScreen
import com.kigz.javillasafarihub.reviews.TouristReviewsScreen
import com.kigz.javillasafarihub.safety.ScamShieldScreen
import com.kigz.javillasafarihub.services.VerifiedServicesScreen
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme
import com.kigz.javillasafarihub.ui.theme.SavannahGold
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalInspectionMode

data class HomeFeature(
    val title: String,
    val description: String,
    val icon: ImageVector
)

data class DrawerItem(
    val title: String,
    val icon: ImageVector
)

data class SafariGalleryItem(
    val caption: String,
    val imageUrl: String
)

private val safariGalleryItems = listOf(
    SafariGalleryItem(caption = "Zebra Stripes", imageUrl = "https://images.pexels.com/photos/7376480/pexels-photo-7376480.jpeg?auto=compress&cs=tinysrgb&w=800"),
    SafariGalleryItem(caption = "Giraffe Pattern", imageUrl = "https://images.pexels.com/photos/6796848/pexels-photo-6796848.jpeg?auto=compress&cs=tinysrgb&w=800"),
    SafariGalleryItem(caption = "Leopard Spots", imageUrl = "https://images.pexels.com/photos/26045671/pexels-photo-26045671.jpeg?auto=compress&cs=tinysrgb&w=800"),
    SafariGalleryItem(caption = "Lion's Mane", imageUrl = "https://images.pexels.com/photos/13022080/pexels-photo-13022080.jpeg?auto=compress&cs=tinysrgb&w=800"),
    SafariGalleryItem(caption = "Elephant Skin", imageUrl = "https://images.pexels.com/photos/9775197/pexels-photo-9775197.jpeg?auto=compress&cs=tinysrgb&w=800")
)

@Composable
fun WildBeautyGallery() {
    val language = LocalLanguageManager.current.currentLanguage
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = translate("Wild Beauty of the Safari", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = translate("Fur patterns, paws and textures up close", language), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(safariGalleryItems) { item ->
                Column(modifier = Modifier.width(140.dp)) {
                    Box(modifier = Modifier.size(140.dp).clip(RoundedCornerShape(16.dp))) {
                        AsyncImage(model = item.imageUrl, contentDescription = item.caption, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = item.caption, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    var backPressedTime by remember { mutableStateOf(0L) }
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    var generatedBudget by remember { mutableDoubleStateOf(0.0) }
    var selectedDrawerItem by remember { mutableStateOf("Home") }
    val destinationRepository = remember { DestinationRepository() }
    var favoriteDestinations by remember { mutableStateOf(emptyList<com.kigz.javillasafarihub.model.Destination>()) }
    DisposableEffect(Unit) {
        val listener = destinationRepository.observeFavorites(
            onChanged = { favoriteDestinations = it }
        )
        onDispose { destinationRepository.removeFavoritesListener(listener) }
    }
    val language = LocalLanguageManager.current.currentLanguage

    // Navigation logic for back button
    BackHandler {
        if (selectedTab != 0) {
            selectedTab = 0
            selectedDrawerItem = "Home"
        } else {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                (context as? android.app.Activity)?.finish()
            } else {
                Toast.makeText(context, translate("Press back again to exit", language), Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        DrawerItem("Home", Icons.Default.Home),
        DrawerItem("Destinations", Icons.Default.LocationOn),
        DrawerItem("Activities", Icons.Default.Terrain),
        DrawerItem("Recommended Trips", Icons.Default.Explore),
        DrawerItem("Transport", Icons.Default.TwoWheeler),
        DrawerItem("Favorites", Icons.Default.Favorite),
        DrawerItem("Profile", Icons.Default.Person),
        DrawerItem("Settings", Icons.Default.Settings),
        DrawerItem("AI Recommendations", Icons.Default.AutoAwesome),
        DrawerItem("Emergency & Safety", Icons.Default.Security),
        DrawerItem("Scam Shield & Reports", Icons.Default.ReportProblem),
        DrawerItem("Verified Tourist Services", Icons.Default.Verified),
        DrawerItem("Offline Travel Guide", Icons.Default.WifiOff),
        DrawerItem("Tourist Reviews & Ratings", Icons.Default.Star),
        DrawerItem("Admin Dashboard", Icons.Default.Security),
        DrawerItem("Bookings", Icons.Default.CalendarMonth),
        DrawerItem("Emergency SOS", Icons.Default.Security),
        DrawerItem("Notifications & Safety Alerts", Icons.Default.Notifications),
        DrawerItem("Budget & Expenses", Icons.Default.AccountBalanceWallet)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = translate("Javilla Safari Hub", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = translate("Explore. Discover. Experience.", language), style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { scope.launch { drawerState.close() } }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close drawer")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    drawerItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(text = translate(item.title, language), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                            selected = selectedDrawerItem == item.title,
                            onClick = {
                                selectedDrawerItem = item.title
                                scope.launch { drawerState.close() }
                                when (item.title) {
                                    "Home" -> selectedTab = 0
                                    "Destinations" -> selectedTab = 4
                                    "Activities" -> selectedTab = 5
                                    "Recommended Trips" -> selectedTab = 8
                                    "Transport" -> selectedTab = 6
                                    "Favorites" -> selectedTab = 2
                                    "Profile" -> selectedTab = 3
                                    "Settings" -> selectedTab = 7
                                    "AI Recommendations" -> selectedTab = 10
                                    "Emergency & Safety" -> selectedTab = 11
                                    "Scam Shield & Reports" -> selectedTab = 12
                                    "Verified Tourist Services" -> selectedTab = 13
                                    "Offline Travel Guide" -> selectedTab = 14
                                    "Tourist Reviews & Ratings" -> selectedTab = 15
                                    "Admin Dashboard" -> selectedTab = 16
                                    "Bookings" -> selectedTab = 17
                                    "Emergency SOS" -> selectedTab = 18
                                    "Notifications & Safety Alerts" -> selectedTab = 19
                                    "Budget & Expenses" -> selectedTab = 20
                                }
                            },
                            icon = { Icon(imageVector = item.icon, contentDescription = item.title, modifier = Modifier.size(20.dp)) },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    val isInspectionMode = LocalInspectionMode.current
                    val currentUser = if (isInspectionMode) null else try { FirebaseAuth.getInstance().currentUser } catch (_: Exception) { null }
                    NavigationDrawerItem(
                        label = { Text(if (currentUser != null) translate("Logout", language) else translate("Login", language), fontSize = 14.sp) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentUser != null) {
                                try { FirebaseAuth.getInstance().signOut() } catch (_: Exception) {}
                                onLogout()
                            } else {
                                onLogout()
                            }
                        },
                        icon = { Icon(imageVector = if (currentUser != null) Icons.AutoMirrored.Filled.Logout else Icons.AutoMirrored.Filled.Login, contentDescription = "Auth") },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val safariPattern = when (selectedTab) {
                4 -> com.kigz.javillasafarihub.ui.SafariPattern.GIRAFFE
                5 -> com.kigz.javillasafarihub.ui.SafariPattern.ZEBRA
                6 -> com.kigz.javillasafarihub.ui.SafariPattern.ELEPHANT
                2, 3 -> com.kigz.javillasafarihub.ui.SafariPattern.LEOPARD
                else -> com.kigz.javillasafarihub.ui.SafariPattern.SAVANNAH
            }
            com.kigz.javillasafarihub.ui.SafariPatternBackground(safariPattern)

            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier.height(80.dp),
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), 
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0; selectedDrawerItem = "Home" },
                            icon = { Icon(imageVector = Icons.Default.Explore, contentDescription = "Explore") },
                            label = { Text(translate("Explore", language), fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer, selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer, indicatorColor = MaterialTheme.colorScheme.primaryContainer, unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1; selectedDrawerItem = "Home" },
                            icon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Planner") },
                            label = { Text(translate("Planner", language), fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer, selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer, indicatorColor = MaterialTheme.colorScheme.primaryContainer, unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        )
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2; selectedDrawerItem = "Favorites" },
                            icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorites") },
                            label = { Text(translate("Favorites", language), fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer, selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer, indicatorColor = MaterialTheme.colorScheme.primaryContainer, unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        )
                        NavigationBarItem(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3; selectedDrawerItem = "Profile" },
                            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text(translate("Profile", language), fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer, selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer, indicatorColor = MaterialTheme.colorScheme.primaryContainer, unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        )
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    when (selectedTab) {
                        0 -> ExploreScreenContent(
                            paddingValues = PaddingValues(0.dp),
                            searchText = searchText,
                            onSearchTextChange = { searchText = it },
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onActivitiesClick = { selectedTab = 5; selectedDrawerItem = "Activities" },
                            onDestinationsClick = { selectedTab = 4; selectedDrawerItem = "Destinations" },
                            onTransportClick = { selectedTab = 6; selectedDrawerItem = "Transport" },
                            onMapClick = { selectedTab = 21; selectedDrawerItem = "Home" },
                            onFavoritesClick = { selectedTab = 2; selectedDrawerItem = "Favorites" },
                            onPlannerClick = { selectedTab = 1; selectedDrawerItem = "Home" },
                            onAIRecommendationsClick = { selectedTab = 10; selectedDrawerItem = "AI Recommendations" },
                            onEmergencyClick = { selectedTab = 11; selectedDrawerItem = "Emergency & Safety" },
                            onScamShieldClick = { selectedTab = 12; selectedDrawerItem = "Scam Shield & Reports" },
                            onVerifiedServicesClick = { selectedTab = 13; selectedDrawerItem = "Verified Tourist Services" },
                            onOfflineTravelClick = { selectedTab = 14; selectedDrawerItem = "Offline Travel Guide" },
                            onTouristReviewsClick = { selectedTab = 15; selectedDrawerItem = "Tourist Reviews & Ratings" },
                            onEmergencySosClick = { selectedTab = 18; selectedDrawerItem = "Emergency SOS" },
                            onNotificationsClick = { selectedTab = 19; selectedDrawerItem = "Notifications & Safety Alerts" },
                            onProfileClick = { selectedTab = 3; selectedDrawerItem = "Profile" },
                        destinationRepository = destinationRepository
                    )
                        1 -> TripPlannerScreen(generatedBudget = generatedBudget, onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" }, onBudgetClick = { selectedTab = 9 })
                        2 -> FavoritesScreen(
                            favorites = favoriteDestinations,
                            onDestinationClick = { selectedTab = 4; selectedDrawerItem = "Destinations" },
                            onRemoveFavorite = { try { destinationRepository.toggleFavorite(it.id) } catch(_:Exception){} },
                            onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" }
                        )
                        3 -> ProfileScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" }, onFavoritesClick = { selectedTab = 2; selectedDrawerItem = "Favorites" }, onTripsClick = { selectedTab = 22; selectedDrawerItem = "Profile" }, onSettingsClick = { selectedTab = 7; selectedDrawerItem = "Settings" }, onLogoutClick = { try { FirebaseAuth.getInstance().signOut() } catch(_:Exception){}; onLogout() })
                        4 -> DestinationScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        5 -> ActivitiesScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        6 -> TransportScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        7 -> SettingsContent(paddingValues = PaddingValues(0.dp), onLoginClick = onLogout)
                        8 -> RecommendedTripsScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" }, onPlanTripClick = { selectedTab = 1; selectedDrawerItem = "Home" })
                        9 -> BudgetCalculatorScreen(onBackClick = { selectedTab = 1 }, onBudgetCalculated = { budget -> generatedBudget = budget; selectedTab = 1 })
                        10 -> AIRecommendationsScreen(generatedBudget = generatedBudget, onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" }, onPlanTrip = { selectedTab = 1; selectedDrawerItem = "Home" })
                        16 -> com.kigz.javillasafarihub.admin.AdminDashboardScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        11 -> EmergencyScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        12 -> ScamShieldScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        13 -> VerifiedServicesScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        14 -> OfflineGuideScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        15 -> TouristReviewsScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        21 -> GoogleMapsScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        22 -> SavedTripsScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        17 -> BookingScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        18 -> EnhancedEmergencyScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        19 -> NotificationsInfoScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                        20 -> BudgetTrackerScreen(onBackClick = { selectedTab = 0; selectedDrawerItem = "Home" })
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreScreenContent(
    paddingValues: PaddingValues,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onMenuClick: () -> Unit,
    onActivitiesClick: () -> Unit,
    onDestinationsClick: () -> Unit,
    onTransportClick: () -> Unit,
    onMapClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onPlannerClick: () -> Unit,
    onAIRecommendationsClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onScamShieldClick: () -> Unit,
    onVerifiedServicesClick: () -> Unit,
    onOfflineTravelClick: () -> Unit,
    onTouristReviewsClick: () -> Unit,
    onEmergencySosClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    destinationRepository: DestinationRepository
) {
    val language = LocalLanguageManager.current.currentLanguage

    val features = listOf(
        HomeFeature("Destinations", "Discover amazing places", Icons.Default.Explore),
        HomeFeature("Activities", "Find things to do", Icons.Default.Terrain),
        HomeFeature("Transport", "Plan your transportation", Icons.Default.TwoWheeler),
        HomeFeature("Travel Planner", "Plan your trip and calculate budget", Icons.Default.CalendarMonth),
        HomeFeature("Map", "Explore locations", Icons.Default.Map),
        HomeFeature("Favorites", "Your saved places", Icons.Default.Favorite),
        HomeFeature("AI Recommendations", "Get personalized safari ideas", Icons.Default.AutoAwesome),
        HomeFeature("Emergency & Safety", "Get urgent travel safety assistance", Icons.Default.Security),
        HomeFeature("Emergency SOS", "Quick access to emergency assistance", Icons.Default.Call),
        HomeFeature("Notifications & Safety Alerts", "Receive important safety alerts", Icons.Default.Notifications),
        HomeFeature("Scam Shield & Reports", "Check reports and report tourist problems", Icons.Default.ReportProblem),
        HomeFeature("Verified Tourist Services", "Find trusted tourism businesses", Icons.Default.Verified),
        HomeFeature("Offline Travel Guide", "Essential Kenya travel info without internet", Icons.Default.WifiOff),
        HomeFeature("Tourist Reviews & Ratings", "Share experiences and discover trusted feedback", Icons.Default.Star)
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onMenuClick) { Icon(imageVector = Icons.Default.Menu, contentDescription = "Open menu") }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(text = translate("Javilla Safari Hub", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = translate("Explore. Discover. Experience.", language), style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(onClick = onProfileClick) { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") }
        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = searchText, 
            onValueChange = onSearchTextChange, 
            modifier = Modifier.fillMaxWidth(), 
            singleLine = true, 
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") }, 
            placeholder = { Text(text = translate("Search destinations, activities...", language), color = SavannahGold.copy(alpha = 0.8f), fontSize = 14.sp) }, 
            colors = OutlinedTextFieldDefaults.colors(
                focusedPlaceholderColor = SavannahGold,
                unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(14.dp)
        )

        if (searchText.isNotBlank()) {
            val destinationMatches = destinationRepository.searchDestinations(searchText.trim()).take(5)
            val activityMatches = ActivityRepository.searchActivities(searchText.trim()).take(5)
            Card(Modifier.fillMaxWidth().padding(top = 10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(translate("Search results", language), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    destinationMatches.forEach { destination ->
                        Row(Modifier.fillMaxWidth().clickable { onDestinationsClick() }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(destination.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(destination.location, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    activityMatches.forEach { activity ->
                        Row(Modifier.fillMaxWidth().clickable { onActivitiesClick() }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Terrain, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(activity.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(activity.location, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    if (destinationMatches.isEmpty() && activityMatches.isEmpty()) {
                        Text(translate("No matching destinations or activities found.", language), style = MaterialTheme.typography.bodyMedium)
                    }
                    if (destinationMatches.isNotEmpty()) {
                        Text(translate("Tap a destination result to see all destinations.", language), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = translate("Start your adventure", language), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = translate("Discover destinations, activities, transport options and useful travel tools.", language), color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = translate("Explore Javilla Safari Hub", language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        WildBeautyGallery()
        Spacer(modifier = Modifier.height(20.dp))
        // Keep the feature cards inside the Home scroll container. This avoids a nested
        // lazy grid with a fixed height and ensures every card remains reachable.
        features.chunked(2).forEach { rowFeatures ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowFeatures.forEach { feature ->
                    FeatureCard(feature = feature, modifier = Modifier.weight(1f), onClick = {
                        when (feature.title) {
                            "Destinations" -> onDestinationsClick()
                            "Activities" -> onActivitiesClick()
                            "Transport" -> onTransportClick()
                            "Travel Planner" -> onPlannerClick()
                            "Map" -> onMapClick()
                            "Favorites" -> onFavoritesClick()
                            "AI Recommendations" -> onAIRecommendationsClick()
                            "Emergency & Safety" -> onEmergencyClick()
                            "Emergency SOS" -> onEmergencySosClick()
                            "Notifications & Safety Alerts" -> onNotificationsClick()
                            "Scam Shield & Reports" -> onScamShieldClick()
                            "Verified Tourist Services" -> onVerifiedServicesClick()
                            "Offline Travel Guide" -> onOfflineTravelClick()
                            "Tourist Reviews & Ratings" -> onTouristReviewsClick()
                        }
                    })
                }
                if (rowFeatures.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(paddingValues: PaddingValues, onLoginClick: () -> Unit = {}) {
    val context = LocalContext.current
    val themeManager = LocalThemeManager.current
    val languageManager = LocalLanguageManager.current
    val language = languageManager.currentLanguage
    val languages = listOf(
        "en" to "English", "sw" to "Swahili", "es" to "Spanish", "fr" to "French", "de" to "German", 
        "zh" to "Chinese", "ar" to "Arabic", "hi" to "Hindi", "pt" to "Portuguese", "ko" to "Korean", "ja" to "Japanese"
    )
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(translate("Settings", language), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(translate("Appearance", language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
                val isDarkActive = themeManager.isDarkTheme ?: isSystemDark
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(translate("Dark Mode", language), Modifier.weight(1f), fontSize = 14.sp)
                    androidx.compose.material3.Switch(checked = isDarkActive, onCheckedChange = { themeManager.toggleTheme(it) })
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(translate("Language", language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.height(350.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), userScrollEnabled = false) {
                    items(languages) { (code, name) ->
                        Button(onClick = { languageManager.changeLanguage(code) }, colors = if (languageManager.currentLanguage == code) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(), modifier = Modifier.fillMaxWidth()) { Text(name, fontSize = 12.sp, maxLines = 1) }
                    }
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(translate("Account", language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                val isInspectionMode = LocalInspectionMode.current
                val currentUser = if (isInspectionMode) null else try { FirebaseAuth.getInstance().currentUser } catch (_: Exception) { null }
                if (currentUser != null) {
                    Text(translate("Logged in as", language) + " ${currentUser.email}", fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { try { FirebaseAuth.getInstance().signOut() } catch (_: Exception) {} }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.AutoMirrored.Filled.Logout, null); Spacer(Modifier.width(8.dp)); Text(translate("Logout", language)) }
                } else {
                    Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) { Icon(Icons.AutoMirrored.Filled.Login, null); Spacer(Modifier.width(8.dp)); Text(translate("Login", language)) }
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(translate("App Links", language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                SettingsLinkItem("Notifications Settings", Icons.Default.Notifications) {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try { context.startActivity(intent) } catch (_: Exception) {}
                }
                SettingsLinkItem("Application Info", Icons.Default.Settings) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try { context.startActivity(intent) } catch (_: Exception) {}
                }
                SettingsLinkItem("Terms & Privacy", Icons.Default.Security) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kws.go.ke/terms-and-conditions")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try { context.startActivity(intent) } catch (_: Exception) { Toast.makeText(context, translate("Unable to open the link.", language), Toast.LENGTH_SHORT).show() }
                }
            }
        }
    }
}

@Composable
fun SettingsLinkItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    val language = LocalLanguageManager.current.currentLanguage
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = SavannahGold, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(16.dp))
        Text(translate(title, language), Modifier.weight(1f), fontSize = 14.sp)
        Icon(Icons.Default.Explore, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun FeatureCard(feature: HomeFeature, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val language = LocalLanguageManager.current.currentLanguage
    Card(modifier = modifier.fillMaxWidth().height(140.dp).clickable { onClick() }, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
            Box(modifier = Modifier.size(40.dp).background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(imageVector = feature.icon, contentDescription = feature.title, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = translate(feature.title, language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = translate(feature.description, language), style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    JavillaSafariHubTheme { HomeScreen(onLogout = {}) }
}
