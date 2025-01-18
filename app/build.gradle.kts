import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    id("kotlin-kapt") // The kapt plugin is applied here
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}

android {
    namespace = "com.enigma.georocks"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.enigma.georocks"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // The MAPS_API_KEY is read from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }
        val mapsApiKey = localProperties.getProperty("MAPS_API_KEY") ?: ""
        resValue("string", "google_maps_key", mapsApiKey)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Minification is disabled for the release build type
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        // Java 17 compatibility is configured
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        // The JVM target is set to 17 for Kotlin
        jvmTarget = "17"
    }

    buildFeatures {
        // View binding is enabled
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation (libs.androidx.fragment.ktx)
    // Retrofit and Gson are used for networking and JSON conversion
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Logging interceptor for HTTP requests
    implementation(libs.logging.interceptor)

    // Glide and Picasso are used for image loading
    implementation(libs.glide.v4151)
    implementation(libs.picasso)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Room database dependencies
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler) // Annotation processing with kapt for Room
    implementation(libs.androidx.room.ktx)

    // Lifecycle-aware components
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Rounded image views
    implementation(libs.roundedimageview)

    // Google Maps API
    implementation(libs.play.services.maps)

    // Firebase Authentication
    implementation(platform(libs.firebase.bom.v3223)) // Firebase BOM is used
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.common.ktx)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core.v351)
}
