plugins {
    id("com.android.library")
}

description =
    "This subproject(Module) includes some fake android source which helps third-part app using system apis ,and some AIDLs"

android {
    compileSdk = target_sdk

    defaultConfig {
        minSdk = min_sdk
        targetSdk = targetSdk
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
}