plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

kotlin {

    android()
    androidNativeArm64()
    sourceSets {

        val androidMain by getting {
            dependencies {
                api("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
                api("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
                api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")

                api("androidx.core:core-ktx:$coreKtx")
                api("androidx.appcompat:appcompat:$appcompat")
                api("com.google.android.material:material:$material")
            }
        }
    }
}

android {
    compileSdk = target_sdk
    defaultConfig {
        minSdk = min_sdk
        targetSdk = target_sdk

        compileOptions {
            targetCompatibility(java_version)
            sourceCompatibility(java_version)
        }

        ndkVersion = "23.1.7779620"
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
        externalNativeBuild {
            cmake {
                cppFlags += listOf("")
            }
        }
    }

    externalNativeBuild {
        cmake {
            version = "3.18.1"
            path = file("src/androidMain/cpp/CMakeLists.txt")
        }
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}