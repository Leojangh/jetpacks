//val kotlin_version: String by settings

pluginManagement {

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    resolutionStrategy {

        eachPlugin {
            //agp
            if (requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
            //hilt
            if (requested.id.id == "dagger.hilt.android.plugin") {
                useModule("com.google.dagger:hilt-android-gradle-plugin:${requested.version}")
            }
            //navigation
            if (requested.id.id == "androidx.navigation.safeargs.kotlin") {
                useModule("androidx.navigation:navigation-safe-args-gradle-plugin:${requested.version}")
            }
        }
    }
}

rootProject.name = "jetpacks"
// Use kebab case formatting for all project names:
// A kebab case formatting is when all letters lowercase,
// words separated with a dash (‘-’) character (e.g.kebab-case-formatting)
include(":app")
include(":share")
include(":javascript")
include(":native")

include(":javascript-bridge-compiler")
include(":javascript-bridge")
