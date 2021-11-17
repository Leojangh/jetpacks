plugins {
    id("com.android.library") /*version AGP*/
    kotlin("android") /*version KOTLIN*/
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

repositories {
    google()
    mavenCentral()
}

dependencies {

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")

    implementation("androidx.core:core-ktx:$coreKtx")
    implementation("androidx.appcompat:appcompat:$appcompat")
    implementation("com.google.android.material:material:$material")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}