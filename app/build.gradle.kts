plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.dokka")
}

android {
    namespace = "com.genlz.jetpacks"
    compileSdk = target_sdk

    defaultConfig {
        applicationId = namespace
        minSdk = min_sdk
        targetSdk = target_sdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk.abiFilters += listOf("arm64-v8a")

        vectorDrawables.useSupportLibrary = true

        manifestPlaceholders["sharedUserId"] = ""
    }

    signingConfigs {
        register("miui_platform") {
            keyAlias = "platform_key"
            keyPassword = "android"
            storeFile =
                file("$rootDir/platform.debug.keystore")
            storePassword = "android"
        }

        register("aosp_platform") {
            keyAlias = "android"
            keyPassword = "android"
            storeFile = file("$rootDir/aosp.jks")
            storePassword = "android"
        }
    }

    buildTypes {

        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
//            applicationIdSuffix = ".$name"
            signingConfig = signingConfigs["aosp_platform"]
        }

        create("macrobenchmark") {
            signingConfig = signingConfigs["debug"]
            isDebuggable = false
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility(java_version)
        targetCompatibility(java_version)
    }

    kotlinOptions {
        jvmTarget = java_version
        freeCompilerArgs = listOf(
            "-opt-in=kotlin.RequiresOptIn",
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
//        kotlinCompilerExtensionVersion = COMPOSE_COMPILER
        kotlinCompilerExtensionVersion = "1.5.0-dev-k1.9.0-6a60475e07f"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    /**
     * The lint closure is changed in AGP 7.1.0.
     * See the diff to get differences.
     * @see com.android.build.api.dsl.Lint
     */
    lint {
        checkDependencies = true
    }

    testFixtures {
        enable = true
    }
}

ksp {
    // For Room schema export
    arg("room.schemaLocation", "$projectDir/schemas")
}

kapt {
    useBuildCache = false
    correctErrorTypes = true
}

kotlin {
    jvmToolchain(java_version.toInt())
}

dependencies {
    implementation(projects.androidThreadAffinity)
    implementation(projects.androidRpc)
    implementation(projects.share)
    implementation(projects.webview)
    implementation(projects.native)
    implementation(projects.appWidgets)
    compileOnly(projects.pseudoAndroid)//escape non-public API restriction
    implementation("androidx.preference:preference-ktx:1.2.0")

    implementation("com.google.guava:guava:$guava")
    implementation("androidx.profileinstaller:profileinstaller:1.3.0")
    implementation("androidx.tracing:tracing-ktx:1.1.0")

    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
    implementation("org.ow2.asm:asm:$asm")
    implementation("org.ow2.asm:asm-util:$asm")
    // The core module is used by all other components
    implementation("com.github.topjohnwu.libsu:core:$libsu")

    implementation("androidx.palette:palette:$palette")

    implementation("androidx.paging:paging-runtime-ktx:$paging")

    implementation("androidx.window:window:$window")
    androidTestImplementation("androidx.window:window-testing:$window")

    implementation("androidx.core:core-splashscreen:$splash")

    implementation("io.coil-kt:coil:$coil")

    // Typed DataStore (Typed API surface, such as Proto)
    implementation("androidx.datastore:datastore:$datastore")
    // Preferences DataStore (SharedPreferences like APIs)
    implementation("androidx.datastore:datastore-preferences:$datastore")
    implementation("androidx.startup:startup-runtime:$startup")

    implementation("androidx.room:room-ktx:$room")
    ksp("androidx.room:room-compiler:$room")

    implementation("androidx.work:work-runtime-ktx:$work")

    //retrofit
    implementation("com.squareup.retrofit2:converter-gson:$retrofit")
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.google.code.gson:gson:$gson")

    //okhttp
    implementation(platform("com.squareup.okhttp3:okhttp-bom:$okhttp"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:$okhttp")

    implementation("com.google.dagger:hilt-android:$HILT")
    kapt("com.google.dagger:hilt-android-compiler:$HILT")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    implementation("androidx.constraintlayout:constraintlayout:$constraintLayout")
    implementation("androidx.navigation:navigation-fragment-ktx:$NAVIGATION")
    implementation("androidx.navigation:navigation-ui-ktx:$NAVIGATION")
    implementation(fileTree("libs") {
        include("*.aar", "*.jar")
    })
    //Compose

    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.27.0")
    implementation("com.google.accompanist:accompanist-webview:0.27.0")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling-preview")
    // When using a MDC theme
    implementation("com.google.android.material:compose-theme-adapter:1.2.1")
    implementation("androidx.compose.foundation:foundation")
    // Material Design
    implementation("androidx.compose.material:material")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    // Integration with activities
    implementation("androidx.activity:activity-compose:$activity")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.runtime:runtime-rxjava2")
    // Animations
    implementation("androidx.compose.animation:animation")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$COMPOSE")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
}