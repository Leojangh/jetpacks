plugins {
    // Define all plugins required by subprojects,specifying the versions but
    // applying false explicitly,then subproject just apply them without
    // versions.The purpose is for loading plugin only once.
    id("com.android.application") version AGP apply false
    id("com.android.library") version AGP apply false
    id("com.android.test") version AGP apply false
    kotlin("android") version KOTLIN apply false
    kotlin("kapt") version KOTLIN apply false
    kotlin("multiplatform") version KOTLIN apply false
    kotlin("js") version KOTLIN apply false
    id("com.google.dagger.hilt.android") version HILT apply false
    id("androidx.navigation.safeargs.kotlin") version NAVIGATION apply false
    id("com.google.devtools.ksp") version KSP apply false
    id("org.jetbrains.dokka") version DOKKA apply false
    id("androidx.benchmark") version BENCHMARK apply false
    id("org.mozilla.rust-android-gradle.rust-android") version "0.9.3" apply false
    id("com.jfrog.artifactory") version "4.29.3" apply false
}

//Enable kapt cache
pluginManager.withPlugin("kotlin-kapt") {
    configure<org.jetbrains.kotlin.gradle.plugin.KaptExtension> { useBuildCache = true }
}

plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().apply {
        // The default v14.17.0 not support Apple silicon.The support becomes
        // available from v16.x but the lasted v17.x is incompatible.
        nodeVersion = "16.13.0"
    }
}

allprojects {
    configurations.all {
        resolutionStrategy {
            cacheDynamicVersionsFor(10, "minutes")
            cacheChangingModulesFor(4, "hours")
        }
    }
}