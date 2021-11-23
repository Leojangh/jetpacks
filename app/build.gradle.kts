plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = target_sdk
    buildToolsVersion = "32.0.0-rc1"

    defaultConfig {
        applicationId = "com.genlz.jetpacks"
        minSdk = min_sdk //vulkan requisites
        targetSdk = target_sdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += "room.schemaLocation" to "$projectDir/schemas"
            }
        }

        vectorDrawables.useSupportLibrary = true

        ndkVersion = "23.1.7779620"
        ndk {
            abiFilters += listOf("arm64-v8a", "arm64-v8a", "x86_64")
        }
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
    }

    externalNativeBuild {
        cmake {
            version = "3.18.1"
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    sourceSets {

    }

    flavorDimensions += "env"

    productFlavors {

        create("alpha") {
            isDefault = true
            versionNameSuffix = "-$name"
            buildConfigField("String", "ENV", """"$name"""")
        }
        create("beta") {
//            dimension = "env" //only one dimension,optional
            versionNameSuffix = "-$name"
            buildConfigField("String", "ENV", """"$name"""")
        }

        create("rc") {
            buildConfigField("String", "ENV", """"$name"""")
            versionNameSuffix = "-$name"
        }

        create("stable") {
            buildConfigField("String", "ENV", """"$name"""")
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
            applicationIdSuffix = ".debug"
        }
    }

    androidComponents {

    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility(java_version)
        targetCompatibility(java_version)
    }

    kotlinOptions {
        jvmTarget = java_version
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    ndkVersion = "23.1.7779620"

    lint {
        // if true, only report errors.
        isIgnoreWarnings = true
    }
}

dependencies {

    implementation(project(":share"))
//    implementation(project(":vulkan"))

    implementation("androidx.browser:browser:$browser")

    implementation("androidx.palette:palette:$palette")

    implementation("com.github.chrisbanes:PhotoView:$photoView")

    implementation("androidx.paging:paging-runtime-ktx:$paging")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:$swiperRefreshLayout")

    implementation("androidx.window:window:$window")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    androidTestImplementation("androidx.window:window-testing:$window")

    implementation("androidx.core:core-splashscreen:$splash")

    implementation("io.coil-kt:coil:$coil")

    implementation("androidx.startup:startup-runtime:$startup")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")

    implementation("androidx.room:room-ktx:$room")
    kapt("androidx.room:room-compiler:$room")

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
    implementation(kotlin("reflect"))

    implementation("androidx.core:core-ktx:$coreKtx")
    implementation("androidx.appcompat:appcompat:$appcompat")
    implementation("androidx.activity:activity-ktx:$activity")
    implementation("com.google.android.material:material:$material")
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

configurations.all {

    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")

    resolutionStrategy {
        cacheDynamicVersionsFor(10, "minutes")
        cacheChangingModulesFor(4, "hours")
    }
}

//TODO
task("generateJavascriptBridge") {

}

//TODO
task("copyTheBridge2javascriptSourceSet") {

}

tasks.build.configure {
    dependsOn += ":javascript:copyCompiledJs2app"
}