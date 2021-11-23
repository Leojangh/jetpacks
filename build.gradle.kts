buildscript {

    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:$AGP")
        classpath(kotlin("gradle-plugin", KOTLIN))
        classpath("com.google.dagger:hilt-android-gradle-plugin:$HILT")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$NAVIGATION")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}