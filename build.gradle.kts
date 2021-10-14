buildscript {

    val kotlin_version: String by project
    val hilt_version: String by project
    val nav_version: String by project

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}