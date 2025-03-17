import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
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
        buildConfigField(
            type = "String",//::class.simpleName!!,
            name = "FIREBASE_WEB_CLIENT_ID",
            value = Properties()
                .apply { load(rootProject.file("local.properties").inputStream()) }
                .getProperty("FIREBASE_WEB_CLIENT_ID")
        )
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
    //--------------------------------------------------------------------------------------firebase
    implementation(libs.firebase.auth)
    //----------------------------------------------------------------------------compose-navigation
    implementation(libs.androidx.navigation.compose)
    //------------------------------------------------------------------------------------------gson
    implementation(libs.gson)
    //-----------------------------------------------------------------------------constraint-layout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
}