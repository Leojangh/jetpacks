plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.genlz.jetpacks.threadaffinity"
    compileSdk = target_sdk
    defaultConfig {
        minSdk = min_sdk
        targetSdk = target_sdk

        compileOptions {
            isCoreLibraryDesugaringEnabled = true
            targetCompatibility(java_version)
            sourceCompatibility(java_version)
        }

        ndkVersion = ndk_version
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
    }

    buildTypes {

        release {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlinOptions {
        jvmTarget = java_version
        freeCompilerArgs = listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xuse-k2",
            "-Xjvm-default=all",
        )
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {
    implementation(projects.native)
    implementation("androidx.core:core-ktx:$coreKtx")
    implementation("com.github.topjohnwu.libsu:core:$libsu")
    implementation("com.github.topjohnwu.libsu:nio:$libsu")
    implementation("com.github.topjohnwu.libsu:service:$libsu")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.2")
}