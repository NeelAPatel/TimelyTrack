plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.timelytrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.timelytrack"
        minSdk = 34
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
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    //    implementation(libs.androidx.ui.desktop)
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.compose.bom.v20230800)
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom.v20240902))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    debugImplementation(libs.ui.tooling)
//    implementation(libs.androidx.activity.compose.v182)
//    implementation(libs.androidx.activity.compose.v192)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.compose.bom.v20241100)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.compose.ui.ui.graphics)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.ui.tooling.preview2)
    implementation(libs.androidx.compose.ui.ui2)
    implementation(libs.androidx.constraintlayout.compose.android)
//    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx.v270)
//    implementation(libs.androidx.lifecycle.runtime.ktx.v286)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material) // Added Material dependency for completeness
    implementation(libs.androidx.material3.v120alpha03)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.material3)
    implementation(libs.navigation.compose)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.ui)
    implementation(platform(libs.androidx.compose.bom.v20240902))
    implementation(platform(libs.androidx.compose.bom))
    kapt(libs.androidx.room.compiler)
    testImplementation(libs.junit)

}