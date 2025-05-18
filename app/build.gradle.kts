import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.1.20"
}

android {
    namespace = "easter.egg.passmark"
    compileSdk = 35

    defaultConfig {
        applicationId = "easter.egg.passmark"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    defaultConfig {
        val propertiesFle = Properties()
            .apply { load(rootProject.file("local.properties").inputStream()) }

        fun addStringFields(name: String) {
            buildConfigField(
                type = String::class.simpleName!!,
                name = name,
                value = propertiesFle.getProperty(name)
            )
        }

        addStringFields(name = "FIREBASE_WEB_CLIENT_ID")
        addStringFields(name = "SUPABASE_URL")
        addStringFields(name = "SUPABASE_KEY")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //-----------------------------------------------------------------------------------credentials
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    //-------------------------------------------------------------------------------------google-id
    implementation(libs.googleid)
    //----------------------------------------------------------------------------compose-navigation
    implementation(libs.androidx.navigation.compose)
    //------------------------------------------------------------------------------------------gson
    implementation(libs.gson)
    //--------------------------------------------------------------------------------material-icons
    implementation(libs.androidx.material.icons.extended)
    //--------------------------------------------------------------------------------------supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.supabase.auth.kt)
    implementation(libs.supabase.realtime.kt)
    //------------------------------------------------------------------------------------------hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    //------------------------------------------------------------------------------------------ktor
    implementation(libs.ktor.client.android)
    //---------------------------------------------------------------------------------serialization
    implementation(libs.kotlinx.serialization.json)
    //-------------------------------------------------------------------------------------datastore
    implementation(libs.androidx.datastore.preferences)
    //-----------------------------------------------------------------------------constraint-layout
    implementation(libs.androidx.constraintlayout.compose)
    //----------------------------------------------------------------------------------------splash
    implementation(libs.androidx.core.splashscreen)
    //------------------------------------------------------------------------------------------coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    //------------------------------------------------------------------------------------biometrics
    implementation(libs.androidx.biometric)
    //------------------------------------------------------------------------------------------room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}