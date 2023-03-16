plugins {
    id("com.android.library")
}

description =
    "This subproject(Module) includes some fake android source which helps third-part app using system apis ,and some AIDLs"

android {
    compileSdk = target_sdk
    namespace = "pseudo.android"

    defaultConfig {
        minSdk = min_sdk
        targetSdk = target_sdk
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

    buildFeatures {
        aidl = true
    }

    compileOptions {
        sourceCompatibility(java_version)
        targetCompatibility(java_version)
    }
}