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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout.compose.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom.v20240902))
    debugImplementation(libs.ui.test.manifest)
    debugImplementation(libs.ui.tooling)
    implementation(libs.androidx.activity.compose.v192)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.material) // Added Material dependency for completeness
    implementation(libs.ui.graphics)
    implementation(libs.androidx.compose.ui.ui.tooling.preview2)
    implementation(libs.androidx.compose.ui.ui2)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx.v286)
    implementation(libs.navigation.compose) // Navigation Compose dependency
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ui.tooling.preview)
    implementation(platform(libs.androidx.compose.bom.v20240902))
    kapt(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    implementation(libs.androidx.material.icons.core)

    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.compose.ui.ui2)
    implementation(libs.androidx.compose.ui.ui.tooling.preview2)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.animation)
    implementation(libs.navigation.compose)

    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3:1.2.0-alpha03")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

}

/**
 *   implementation("androidx.core:core-ktx:1.12.0")
 *     implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
 *     implementation("androidx.activity:activity-compose:1.8.2")
 *     implementation(platform("androidx.compose:compose-bom:2023.08.00"))
 *     implementation("androidx.compose.ui:ui")
 *     implementation("androidx.compose.ui:ui-graphics")
 *     implementation("androidx.compose.ui:ui-tooling-preview")
 *     implementation("androidx.compose.material3:material3")
 *     implementation("androidx.compose.material3:material3:1.2.0-alpha03")
 *     testImplementation("junit:junit:4.13.2")
 *     androidTestImplementation("androidx.test.ext:junit:1.1.5")
 *     androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
 *     androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
 *     androidTestImplementation("androidx.compose.ui:ui-test-junit4")
 *     debugImplementation("androidx.compose.ui:ui-tooling")
 *     debugImplementation("androidx.compose.ui:ui-test-manifest")
 *     implementation("androidx.room:room-runtime:2.6.1")
 *     implementation("androidx.room:room-compiler:2.6.1")
 *     implementation("androidx.room:room-ktx:2.6.1")
 */