import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
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

        // Leer MAPS_API_KEY desde local.properties
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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Retrofit y Gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Interceptor
    implementation(libs.logging.interceptor)

    // Glide y Picasso
    implementation(libs.glide.v4151)
    annotationProcessor(libs.compiler)
    implementation(libs.glide)
    implementation(libs.picasso)

    // Corutinas con alcance lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Imágenes con bordes redondeados
    implementation(libs.roundedimageview)

    // Google Maps
    implementation(libs.play.services.maps)

    // Firebase Authentication
    implementation(platform(libs.firebase.bom.v3223)) // Replace with the desired version
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.common.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core.v351)
}
