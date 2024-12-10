@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id ("kotlin-kapt")

    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.monitoring.heartrate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.monitoring.heartrate"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility =JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    dependencies {
        // Compose BOM
        implementation(platform(libs.compose.bom))
        implementation("androidx.compose.material3:material3")
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-test-junit4")
        implementation("androidx.compose.ui:ui-graphics")
        implementation("androidx.compose.ui:ui-tooling-preview")
        debugImplementation("androidx.compose.ui:ui-tooling")
        androidTestImplementation("androidx.compose.ui:ui-test-junit4")

        implementation("androidx.compose.material:material-icons-core:material3")
        implementation("androidx.compose.material:material-icons-extended:material3")

        // Firebase BOM
        implementation(platform(libs.firebase.bom))
        implementation("com.google.firebase:firebase-analytics-ktx")
        implementation("com.google.firebase:firebase-auth-ktx")
        implementation("com.google.firebase:firebase-firestore-ktx")
        implementation(libs.firebase.storage.ktx)

        // Core AndroidX
        implementation(libs.core.ktx)
        implementation(libs.lifecycle.runtime.ktx)
        implementation(libs.activity.compose)

        // Retrofit and OkHttp
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

        // Dagger Hilt
        implementation("com.google.dagger:hilt-android:2.48.1")
        kapt("com.google.dagger:hilt-android-compiler:2.48.1")

        // Room
        val room_version = "2.6.0"
        implementation("androidx.room:room-runtime:$room_version")
        kapt("androidx.room:room-compiler:$room_version")
        implementation("androidx.room:room-ktx:$room_version")

        // Navigation
        implementation(libs.navigation.compose)
        implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

        // Lottie
        implementation(libs.lottie)

        // Chart
        implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")


        // Coil
        implementation(libs.coil.compose)

        // Testing
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.test.ext.junit)
        androidTestImplementation(libs.espresso.core)
    }

}

// Allow references to generated code
kapt {
    correctErrorTypes = true
    javacOptions {
        option("-Xmaxerrs", 500)
    }

}
