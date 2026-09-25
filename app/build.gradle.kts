import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.ksp)
}

import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

val mapsApiKey = localProperties.getProperty("MAPS_API_KEY", "")

android {
    namespace = "com.kigz.javillasafarihub"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.kigz.javillasafarihub"
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

        minSdk = 26
        targetSdk = 37

        versionCode = 3
        versionName = "1.2"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        allWarningsAsErrors.set(true)
    }
}

dependencies {

    // AndroidX
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("androidx.activity:activity-compose:1.10.1")

    implementation(libs.maps.compose)
    // Image loading (destination photos + safari gallery images)
    implementation(libs.coil.compose)
    // Compose
    implementation(platform("androidx.compose:compose-bom:2026.02.01"))
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.unit)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.3")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth:24.0.1")
    implementation("com.google.firebase:firebase-database")

    // Testing
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation(
        "androidx.test.ext:junit:1.2.1"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.7.0"
    )

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    debugImplementation("com.google.firebase:firebase-appcheck-debug")

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

}