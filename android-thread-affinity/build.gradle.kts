plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.genlz.jetpacks.threadaffinity"
    compileSdk = target_sdk
    defaultConfig {
        minSdk = min_sdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        compileOptions {
            targetCompatibility(java_version)
            sourceCompatibility(java_version)
        }
    }

    buildTypes {

        release {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlinOptions {
        jvmTarget = java_version
        freeCompilerArgs = listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all",
        )
    }
}

dependencies {
    implementation(projects.native)
    implementation(projects.share)
    implementation("com.github.topjohnwu.libsu:core:$libsu")
    implementation("com.github.topjohnwu.libsu:nio:$libsu")
    implementation("com.github.topjohnwu.libsu:service:$libsu")

    testImplementation(kotlin("test"))
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}