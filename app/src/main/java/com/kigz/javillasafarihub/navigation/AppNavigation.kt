package com.kigz.javillasafarihub.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.kigz.javillasafarihub.ui.SafariPattern
import com.kigz.javillasafarihub.ui.SafariPatternBackground

import com.kigz.javillasafarihub.activities.ActivitiesScreen
import com.kigz.javillasafarihub.auth.ForgotPasswordScreen
import com.kigz.javillasafarihub.auth.LoginScreen
import com.kigz.javillasafarihub.auth.RegisterScreen
import com.kigz.javillasafarihub.home.HomeScreen
import com.kigz.javillasafarihub.onboarding.OnboardingScreen
import com.kigz.javillasafarihub.splash.SplashScreen
import com.kigz.javillasafarihub.maps.GoogleMapsScreen
import com.kigz.javillasafarihub.maps.CurrentLocationScreen
import com.kigz.javillasafarihub.maps.NearbyAttractionsScreen
import com.kigz.javillasafarihub.emergency.EmergencyScreen
import com.kigz.javillasafarihub.safety.ScamShieldScreen
import com.kigz.javillasafarihub.safety.TouristSafetyScreen
import com.kigz.javillasafarihub.services.VerifiedServicesScreen
import com.kigz.javillasafarihub.offline.OfflineTravelScreen
import com.kigz.javillasafarihub.reviews.TouristReviewsScreen
import com.kigz.javillasafarihub.admin.AdminDashboardScreen
import com.kigz.javillasafarihub.core.BookingScreen
import com.kigz.javillasafarihub.core.EnhancedEmergencyScreen
import com.kigz.javillasafarihub.core.OfflineGuideScreen
import com.kigz.javillasafarihub.core.NotificationsInfoScreen
import com.kigz.javillasafarihub.core.BudgetTrackerScreen
import com.kigz.javillasafarihub.core.TransportScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val pattern = when (currentRoute) {
        ROUT_ONBOARDING, ROUT_SPLASH -> null
        ROUT_ACTIVITIES -> SafariPattern.ZEBRA
        ROUT_TRANSPORT -> SafariPattern.ELEPHANT
        ROUT_MAPS, ROUT_CURRENT_LOCATION, ROUT_NEARBY_ATTRACTIONS -> SafariPattern.SAVANNAH
        else -> SafariPattern.SAVANNAH
    }

    Box(modifier = Modifier.fillMaxSize()) {
        pattern?.let { SafariPatternBackground(it) }
        NavHost(
            navController = navController,
            startDestination = ROUT_SPLASH
        ) {

        // --------------------------------
        // Splash Screen
        // --------------------------------
        composable(ROUT_SPLASH) {

            SplashScreen {

                val destination = if (FirebaseAuth.getInstance().currentUser != null) {
                    ROUT_HOME
                } else {
                    ROUT_ONBOARDING
                }

                navController.navigate(destination) {
                    popUpTo(ROUT_SPLASH) {
                        inclusive = true
                    }
                }
            }
        }

        // --------------------------------
        // Onboarding Screen
        // --------------------------------
        composable(ROUT_ONBOARDING) {

            OnboardingScreen {

                navController.navigate(ROUT_LOGIN) {
                    popUpTo(ROUT_ONBOARDING) {
                        inclusive = true
                    }
                }
            }
        }

        // --------------------------------
        // Login Screen
        // --------------------------------
        composable(ROUT_LOGIN) {

            LoginScreen(

                onLoginSuccess = {

                    navController.navigate(ROUT_HOME) {
                        popUpTo(ROUT_LOGIN) {
                            inclusive = true
                        }
                    }
                },

                onRegisterClick = {
                    navController.navigate(ROUT_REGISTER)
                },

                onForgotPasswordClick = {
                    navController.navigate(ROUT_FORGOT_PASSWORD)
                }
            )
        }

        // --------------------------------
        // Register Screen
        // --------------------------------
        composable(ROUT_REGISTER) {

            RegisterScreen(

                onRegisterSuccess = {

                    // After successful registration,
                    // go to Home Screen
                    navController.navigate(ROUT_HOME) {

                        popUpTo(ROUT_REGISTER) {
                            inclusive = true
                        }
                    }
                },

                onLoginClick = {

                    navController.popBackStack()
                }
            )
        }

        // --------------------------------
        // Forgot Password Screen
        // --------------------------------
        composable(ROUT_FORGOT_PASSWORD) {

            ForgotPasswordScreen(

                onBackToLogin = {

                    navController.popBackStack()
                }
            )
        }

        composable(ROUT_HOME) {

            HomeScreen(
                onLogout = {
                    navController.navigate(ROUT_LOGIN) {
                        popUpTo(ROUT_HOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // --------------------------------
        // Activities Screen
        // --------------------------------
        composable(ROUT_ACTIVITIES) {

            ActivitiesScreen(onBackClick = { navController.popBackStack() })
        }
        composable(ROUT_MAPS) {

            GoogleMapsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(ROUT_CURRENT_LOCATION) {

            CurrentLocationScreen()
        }
        composable(ROUT_NEARBY_ATTRACTIONS) {

            NearbyAttractionsScreen()
        }

        composable(ROUT_EMERGENCY) {
            EmergencyScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(ROUT_SCAM_SHIELD) {
            ScamShieldScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(ROUT_VERIFIED_SERVICES) {
            VerifiedServicesScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(ROUT_TOURIST_REVIEWS) {
            TouristReviewsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(ROUT_ADMIN_DASHBOARD) { AdminDashboardScreen(onBackClick = { navController.popBackStack() }) }

        composable(ROUT_OFFLINE_TRAVEL) {
            OfflineTravelScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(ROUT_BOOKINGS) { BookingScreen(onBackClick = { navController.popBackStack() }) }
        composable(ROUT_ENHANCED_EMERGENCY) { EnhancedEmergencyScreen(onBackClick = { navController.popBackStack() }) }
        composable(ROUT_OFFLINE_GUIDE) { OfflineGuideScreen(onBackClick = { navController.popBackStack() }) }
        composable(ROUT_NOTIFICATIONS) { NotificationsInfoScreen(onBackClick = { navController.popBackStack() }) }
        composable(ROUT_BUDGET_TRACKER) { BudgetTrackerScreen(onBackClick = { navController.popBackStack() }) }
        composable(ROUT_TRANSPORT) { TransportScreen(onBackClick = { navController.popBackStack() }) }

        composable(ROUT_TOURIST_SAFETY) {
            TouristSafetyScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
    }
}
