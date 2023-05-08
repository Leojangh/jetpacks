plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = target_sdk

    defaultConfig {
        minSdk = min_sdk

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
        freeCompilerArgs = listOf(
            "-opt-in=kotlin.RequiresOptIn",
        )
    }
    namespace = "com.genlz.android.viewbinding"
}

dependencies {
    implementation ("androidx.lifecycle:lifecycle-common-java8:$lifecycle")
    compileOnly ("androidx.appcompat:appcompat:$appcompat")
    compileOnly ("androidx.databinding:databinding-runtime:$AGP")
}