plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.dokka")
}

android {
    compileSdk = target_sdk
    buildToolsVersion = "32.0.0"

    defaultConfig {
        applicationId = "com.genlz.jetpacks"
        minSdk = min_sdk //vulkan requisites
        targetSdk = target_sdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }

    sourceSets {
        val dir = project(":javascript-bridge").projectDir
        getByName("main") {
            kotlin.srcDir("$dir/src/main/kotlin")
        }
    }

    val device = "device"
    val region = "region"
    flavorDimensions += listOf(region, device)

    productFlavors {

        create("cn") {
            dimension = region
            buildConfigField("boolean", "IS_GLOBAL", "false")
        }

        create("global") {
            dimension = region
            buildConfigField("boolean", "IS_GLOBAL", "true")
        }

        create("phone") {
            dimension = device //Optional if there is only one dimension.
            buildConfigField("String", "DEVICE", """"$name"""")
        }

        create("pad") {
            dimension = device //Optional if there is only one dimension.
            buildConfigField("String", "DEVICE", """"$name"""")
        }
    }

    androidComponents {
        beforeVariants {
            // To check for a certain build type, use variantBuilder.buildType == "<buildType>"

//            //No globalAlpha
//            if (it.productFlavors.containsAll(listOf("version" to "alpha", "region" to "global"))) {
//                // Gradle ignores any variants that satisfy the conditions above.
//                it.enabled = false
//            }
        }
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
        release {

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles += getDefaultProguardFile("proguard-android-optimize.txt")
            proguardFiles("proguard-rules.pro")
        }

        debug {
            applicationIdSuffix = ".$name"
            // I can't create a new build type for follow configurations
//            signingConfig = signingConfigs["platform"]
            manifestPlaceholders["sharedUserId"] =
                if (signingConfig?.name == "platform") "android.uid.system" else ""
        }

        // It has same signature with system.
        create("platformDebug") {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs["platform"]
            manifestPlaceholders["sharedUserId"] = "android.uid.system"
        }

        create("macrobenchmark") {
            signingConfig = signingConfigs["debug"]
            isDebuggable = false
        }
    }


    tasks.assemble.configure {
        dependsOn += ":javascript:copyCompiledJs2app"
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
        viewBinding = true
        dataBinding = true
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

    implementation(projects.share)
    implementation(projects.webview)
    implementation(projects.native)
    implementation(projects.appWidgets)
    compileOnly(projects.pseudoAndroid)//escape non-public API restriction

    implementation("androidx.profileinstaller:profileinstaller:1.2.0-beta03")
    implementation("androidx.tracing:tracing-ktx:1.0.0")

    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
    implementation("org.ow2.asm:asm:$asm")
    // The core module is used by all other components
    implementation("com.github.topjohnwu.libsu:core:$libsu")

    implementation("androidx.browser:browser:$browser")

    implementation("androidx.palette:palette:$palette")

    implementation("com.github.chrisbanes:PhotoView:$photoView")

    implementation("androidx.paging:paging-runtime-ktx:$paging")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:$swiperRefreshLayout")

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

    implementation("androidx.activity:activity-ktx:$activity")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayout")
    implementation("androidx.navigation:navigation-fragment-ktx:$NAVIGATION")
    implementation("androidx.navigation:navigation-ui-ktx:$NAVIGATION")
    implementation(fileTree("libs") {
        include("*.aar", "*.jar")
    })
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
}