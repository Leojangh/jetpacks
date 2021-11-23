plugins {
    id("com.android.application") version AGP apply false
    id("com.android.library") version AGP apply false
    kotlin("android") version KOTLIN apply false
    kotlin("kapt") version KOTLIN apply false
    kotlin("multiplatform") version KOTLIN apply false
    kotlin("js") version KOTLIN apply false
    id("dagger.hilt.android.plugin") version HILT apply false
    id("androidx.navigation.safeargs.kotlin") version NAVIGATION apply false
}