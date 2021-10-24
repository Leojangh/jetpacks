// Auto properties delegate from gradle.properties,
// same as settings and init script,just us 'by settings' or 'by gradle'.
// this delegate declaration can be omitted if use groovy.
val java_version: String by project
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
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    lint {
        // if true, only report errors.
        isIgnoreWarnings = true
    }
}

dependencies {

    implementation(project(":share"))

    implementation("androidx.palette:palette:${rootProject.extra["palette"]}")

    implementation("com.github.chrisbanes:PhotoView:${rootProject.extra["photoView"]}")

    implementation("androidx.paging:paging-runtime-ktx:${rootProject.extra["paging"]}")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:${rootProject.extra["swiperRefreshLayout"]}")

    val window: String by rootProject.extra
    implementation("androidx.window:window:$window")
    androidTestImplementation("androidx.window:window-testing:$window")

    implementation("androidx.core:core-splashscreen:${rootProject.extra["splash"]}")

    implementation("io.coil-kt:coil:${rootProject.extra["coil"]}")

    implementation("androidx.startup:startup-runtime:${rootProject.extra["startup"]}")

    val lifecycle: String by rootProject.extra
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")

    val room: String by rootProject.extra
    implementation("androidx.room:room-ktx:$room")
    kapt("androidx.room:room-compiler:$room")

    implementation("androidx.work:work-runtime-ktx:${rootProject.extra["work"]}")

    //retrofit
    val retrofit: String by rootProject.extra
    implementation("com.squareup.retrofit2:converter-gson:$retrofit")
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.google.code.gson:gson:${rootProject.extra["gson"]}")

    //okhttp
    val okhttp: String by rootProject.extra
    implementation(platform("com.squareup.okhttp3:okhttp-bom:$okhttp"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:$okhttp")

    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-android-compiler:$hilt_version")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation(kotlin("reflect"))
    implementation("androidx.core:core-ktx:${rootProject.extra["coreKtx"]}")
    implementation("androidx.appcompat:appcompat:${rootProject.extra["appcompat"]}")
    implementation("com.google.android.material:material:${rootProject.extra["material"]}")
    implementation("androidx.constraintlayout:constraintlayout:${rootProject.extra["constraintLayout"]}")
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation(fileTree("libs") {
        include("*.aar", "*.jar")
    })
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
}

configurations.all {

    resolutionStrategy {
        cacheDynamicVersionsFor(10, "minutes")
        cacheChangingModulesFor(4, "hours")
    }
}