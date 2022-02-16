plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.dokka")
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    api(kotlin("reflect"))

    api("androidx.webkit:webkit:$webkit")

    api("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
    api("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines")

    api("androidx.core:core-ktx:$coreKtx")
    api("androidx.appcompat:appcompat:$appcompat")
    api("com.google.android.material:material:$material")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}