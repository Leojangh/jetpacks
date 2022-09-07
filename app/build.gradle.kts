plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.dokka")
    id("com.google.gms.google-services")
}

android {
    compileSdk = target_sdk

    defaultConfig {
        applicationId = "com.genlz.jetpacks"
        minSdk = min_sdk //vulkan requisites
        targetSdk = target_sdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true

        manifestPlaceholders["sharedUserId"] = ""
    }

    val device = "device"
    val region = "region"
    flavorDimensions += listOf(region, device)

    productFlavors {

        create("cn") {
            dimension = region
        }

        create("global") {
            dimension = region
        }

        create("phone") {
            dimension = device //Optional if there is only one dimension.
        }

        create("pad") {
            dimension = device //Optional if there is only one dimension.
        }
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

            proguardFiles += getDefaultProguardFile("proguard-android-optimize.txt")
            proguardFiles("proguard-rules.pro")
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
//            "-Xuse-k2"
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = COMPOSE_COMPILER
    }

    packagingOptions {
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
        // if true, only report errors.
        ignoreWarnings = true
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

dependencies {
//    implementation(projects.fcmAndroid)
//    implementation(projects.viewBinding)
    implementation(projects.androidRpc)
    implementation(projects.share)
    implementation(projects.webview)
    implementation(projects.native)
    implementation(projects.appWidgets)
    compileOnly(projects.pseudoAndroid)//escape non-public API restriction
    implementation(platform("com.google.firebase:firebase-bom:$firebase"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("androidx.preference:preference-ktx:1.2.0")

    implementation("com.google.guava:guava:$guava")
    implementation("androidx.profileinstaller:profileinstaller:1.2.0")
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

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    implementation("androidx.constraintlayout:constraintlayout:$constraintLayout")
    implementation("androidx.navigation:navigation-fragment-ktx:$NAVIGATION")
    implementation("androidx.navigation:navigation-ui-ktx:$NAVIGATION")
    implementation(fileTree("libs") {
        include("*.aar", "*.jar")
    })
    //Compose
    implementation("androidx.compose.ui:ui:$COMPOSE")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.26.0-alpha")
    implementation("com.google.accompanist:accompanist-webview:0.26.0-alpha")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling-preview:$COMPOSE")
    // When using a MDC theme
    implementation("com.google.android.material:compose-theme-adapter:1.1.15")
    implementation("androidx.compose.foundation:foundation:$COMPOSE")
    // Material Design
    implementation("androidx.compose.material:material:$COMPOSE")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:$COMPOSE")
    implementation("androidx.compose.material:material-icons-extended:$COMPOSE")
    // Integration with activities
    implementation("androidx.activity:activity-compose:1.5.1")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:$COMPOSE")
    implementation("androidx.compose.runtime:runtime-rxjava2:$COMPOSE")
    // Animations
    implementation("androidx.compose.animation:animation:$COMPOSE")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$COMPOSE")
    debugImplementation("androidx.compose.ui:ui-tooling:$COMPOSE")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$COMPOSE")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
}