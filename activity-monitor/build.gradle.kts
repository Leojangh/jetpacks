plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
}

android {
    compileSdk = target_sdk
    buildToolsVersion = "31.0.0"

    defaultConfig {
        applicationId = "com.genlz.activitymonitor"
        minSdk = min_sdk
        targetSdk = target_sdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        register("platform") {
            keyAlias = "platform_key"
            keyPassword = "android"
            storeFile =
                file("$rootDir/platform.debug.keystore")
            storePassword = "android"
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".$name"
            // I can't create a new build type for follow configurations
            signingConfig = signingConfigs["platform"]
            manifestPlaceholders["sharedUserId"] =
                if (signingConfig?.name == "platform") "android.uid.system" else ""
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility(java_version)
        targetCompatibility(java_version)
    }

    kotlinOptions {
        jvmTarget = java_version
    }

    buildFeatures {
        viewBinding = true
    }

    lint {
        // if true, only report errors.
        isIgnoreWarnings = true
        isCheckDependencies = true
    }
}

dependencies {
    implementation(project(":share"))
    implementation(project(":pseudo-android"))
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.3.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines")
}
