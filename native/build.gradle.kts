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
    pythonCommand = "python3"
}

tasks.whenTaskAdded {
    if ((name == "mergeDebugJniLibFolders" || name == "mergeReleaseJniLibFolders")) {
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

        ndkVersion = ndk_version
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
        externalNativeBuild {
            cmake {
                cppFlags += listOf("")
            }
        }
    }

    kotlinOptions {
        jvmTarget = java_version
        freeCompilerArgs = listOf(
            "-opt-in=kotlin.RequiresOptIn",
        )
    }

    externalNativeBuild {
        cmake {
            version = "3.22.1"
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

}

dependencies {
    implementation("androidx.annotation:annotation:1.6.0")
}