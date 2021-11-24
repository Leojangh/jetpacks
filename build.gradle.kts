plugins {
    // Define all plugins required by subprojects,specifying the versions but
    // applying false explicitly,then subproject just apply them without
    // versions.The purpose is for loading plugin only once.
    id("com.android.application") version AGP apply false
    id("com.android.library") version AGP apply false
    kotlin("android") version KOTLIN apply false
    kotlin("kapt") version KOTLIN apply false
    kotlin("multiplatform") version KOTLIN apply false
    kotlin("js") version KOTLIN apply false
    id("dagger.hilt.android.plugin") version HILT apply false
    id("androidx.navigation.safeargs.kotlin") version NAVIGATION apply false
    id("com.google.devtools.ksp") version KSP apply false
}

//Enable kapt cache
pluginManager.withPlugin("kotlin-kapt") {
    configure<org.jetbrains.kotlin.gradle.plugin.KaptExtension> { useBuildCache = true }
}