package com.kigz.javillasafarihub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kigz.javillasafarihub.navigation.AppNavigation
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme

import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

val LocalThemeManager = compositionLocalOf { ThemeManager() }
val LocalLanguageManager = compositionLocalOf { LanguageManager() }

class ThemeManager {
    var isDarkTheme by mutableStateOf<Boolean?>(null)
    fun toggleTheme(isDark: Boolean?) { isDarkTheme = isDark }
}

class LanguageManager(private val context: android.content.Context? = null) {
    private val prefs = context?.getSharedPreferences("javilla_preferences", android.content.Context.MODE_PRIVATE)
    var currentLanguage by mutableStateOf(prefs?.getString("language", "en") ?: "en")
        private set

    fun changeLanguage(lang: String) {
        currentLanguage = lang
        prefs?.edit()?.putString("language", lang)?.apply()
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeManager = remember { ThemeManager() }
            val context = LocalContext.current
            val languageManager = remember { LanguageManager(context.applicationContext) }
            
            // Key to force recomposition when language changes
            key(languageManager.currentLanguage) {
                val isDark = themeManager.isDarkTheme ?: isSystemInDarkTheme()
                
                // Apply language/locale
                val context = LocalContext.current
                val locale = Locale.forLanguageTag(languageManager.currentLanguage)
                Locale.setDefault(locale)
                val configuration = android.content.res.Configuration(context.resources.configuration)
                configuration.setLocale(locale)
                val localizedContext = context.createConfigurationContext(configuration)

                CompositionLocalProvider(
                    LocalContext provides localizedContext,
                    androidx.activity.compose.LocalActivityResultRegistryOwner provides this@MainActivity,
                    LocalThemeManager provides themeManager,
                    LocalLanguageManager provides languageManager
                ) {
                    JavillaSafariHubTheme(darkTheme = isDark) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}
