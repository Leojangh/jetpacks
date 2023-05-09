plugins {
    id("com.android.library")
    kotlin("android")
    id("org.mozilla.rust-android-gradle.rust-android")
}
//Specifying paths to sub-commands (Python, Cargo, and Rustc) in local.properties
//https://github.com/mozilla/rust-android-gradle#specifying-paths-to-sub-commands-python-cargo-and-rustc
cargo {
    module = "src/main/rust"
    libname = "rust"
    targets = listOf("arm64")
    prebuiltToolchains = true
    profile = "release"
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
        //test构建仍然需要这个属性
        targetSdk = target_sdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
    testImplementation(kotlin("test"))
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}