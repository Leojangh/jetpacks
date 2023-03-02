plugins {
    id("com.android.library")
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

        ndkVersion = "25.2.9519653"
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

}