plugins {
    id("com.android.library")
    kotlin("android")
    id("org.mozilla.rust-android-gradle.rust-android")
}

cargo {
    module = "src/main/rust"
    libname = "rust"
    targets = listOf("arm64")
    prebuiltToolchains = true
    profile = "release"
}

tasks.whenTaskAdded {
    if ((name == "javaPreCompileDebug" || name == "javaPreCompileRelease")) {
        dependsOn("cargoBuild")
    }
}

android {
    namespace = "com.genlz.jetpacks.libnative"
    compileSdk = target_sdk
    defaultConfig {
        minSdk = min_sdk
        targetSdk = target_sdk

        compileOptions {
            targetCompatibility(java_version)
            sourceCompatibility(java_version)
        }

        ndkVersion = "25.1.8937393"
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
            version = "3.22.1"
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    kotlinOptions {
        jvmTarget = java_version
        freeCompilerArgs = listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xuse-k2"
        )
    }
}

dependencies {
    implementation(kotlin("stdlib"))
}