val java_version: String by project

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles += getDefaultProguardFile("proguard-android-optimize.txt")
            proguardFiles("proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility(java_version)
        targetCompatibility(java_version)
    }
    kotlinOptions {
        jvmTarget = java_version
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    val lifecycle: String by rootProject.extra
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")

    implementation("androidx.core:core-ktx:${rootProject.extra["coreKtx"]}")
    implementation("androidx.appcompat:appcompat:${rootProject.extra["appcompat"]}")
    implementation("com.google.android.material:material:${rootProject.extra["material"]}")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}