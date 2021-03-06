plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = target_sdk

    defaultConfig {
        minSdk = min_sdk
        targetSdk = targetSdk

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
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    api(projects.share)
}