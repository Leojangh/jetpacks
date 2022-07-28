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
    api ("androidx.lifecycle:lifecycle-common-java8:2.5.1")
    compileOnly ("androidx.appcompat:appcompat:1.4.2")
    compileOnly ("androidx.databinding:databinding-runtime:$AGP")
}