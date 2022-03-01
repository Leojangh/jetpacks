//val kotlin_version: String by settings

pluginManagement {

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    resolutionStrategy {

        eachPlugin {
            //hilt
            if (requested.id.namespace == "dagger.hilt.android") {
                useModule("com.google.dagger:hilt-android-gradle-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
    }
}

rootProject.name = "jetpacks"
// Use kebab case formatting for all project names:
// A kebab case formatting is when all letters lowercase,
// words separated with a dash (‘-’) character (e.g.kebab-case-formatting)
include(":app")
include(":share")
include(":webview")
include(":javascript")
include(":native")
include(":vulkan")
include(":app-widgets")
include(":app-plugins")
include(":app-host")
include(":pseudo-android")

include(":javascript-bridge-compiler")
include(":javascript-bridge")
