val java_version: String by project
val kotlin_version: String by project
val hilt_version: String by project
val nav_version: String by project

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.genlz.jetpacks"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += "room.schemaLocation" to "$projectDir/schemas"
            }
        }

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {

            isMinifyEnabled = true

            proguardFiles += getDefaultProguardFile("proguard-android-optimize.txt")
            proguardFiles("proguard-rules.pro")
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
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.paging:paging-runtime-ktx:3.0.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    val window_version = "1.0.0-beta+"
    implementation("androidx.window:window:$window_version")
    androidTestImplementation("androidx.window:window-testing:$window_version")

    implementation("androidx.core:core-splashscreen:1.0.0-alpha01")

    implementation("io.coil-kt:coil:1.3.2")

    implementation("androidx.startup:startup-runtime:1.1.0")

    val lifecycle_version = "2.4.0-alpha03"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

    val room_version = "2.3.0"
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    implementation("androidx.work:work-runtime-ktx:2.7.0-beta01")//for targeting S+

    val retrofit_version = "2.9.0"
    //retrofit
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.google.code.gson:gson:2.8.7")

    //okhttp
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.1"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")

    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-android-compiler:$hilt_version")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation(kotlin("reflect"))
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation(fileTree("libs") {
        include("*.aar", "*.jar")
    })
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

configurations.all {

    resolutionStrategy {
        cacheDynamicVersionsFor(10, "minutes")
        cacheChangingModulesFor(4, "hours")
    }
}