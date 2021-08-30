// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    val java_version = JavaVersion.VERSION_11
    val kotlin_version: String by project
    val hilt_version: String by project
    val nav_version: String by project

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}