package com.kigz.javillasafarihub

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Production Firebase bootstrap.
 *
 * App Check is installed before the app starts using Firebase services.
 * Debug builds use the debug provider so local development/emulators can be
 * registered safely. Release builds use Play Integrity.
 */
class JavillaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        runCatching {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                if (BuildConfig.DEBUG) {
                    DebugAppCheckProviderFactory.getInstance()
                } else {
                    PlayIntegrityAppCheckProviderFactory.getInstance()
                }
            )
        }

        runCatching {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }

        runCatching {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
            crashlytics.setCustomKey("application_id", BuildConfig.APPLICATION_ID)
        }
    }
}
