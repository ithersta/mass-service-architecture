plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
    kotlin("plugin.serialization") version "1.7.10"
}

android {
    namespace = "ru.spbstu.architecture"
    compileSdk = 33

    defaultConfig {
        applicationId = "ru.spbstu.architecture"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        kotlinCompilerExtensionVersion = "1.3.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

dependencies {
    val composeVersion = "1.2.1"
    val d2vVersion = "0.9.1"
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material3:material3:1.0.0-rc01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation("io.github.pt2121:collage:0.1.0-SNAPSHOT")
    implementation("io.github.raamcosta.compose-destinations:core:1.6.20-beta")
    implementation("io.data2viz.d2v:d2v-axis:$d2vVersion")
    implementation("io.data2viz.d2v:d2v-chord:$d2vVersion")
    implementation("io.data2viz.d2v:d2v-force:$d2vVersion")
    implementation("io.data2viz.d2v:d2v-hexbin:$d2vVersion")
    implementation("io.data2viz.d2v:d2v-random:$d2vVersion")
    implementation("io.data2viz.d2v:d2v-scale:$d2vVersion")
    implementation("io.data2viz.d2v:d2v-shape:$d2vVersion")
    implementation("io.data2viz.d2v:d2v-viz:$d2vVersion")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.6.20-beta")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
}