plugins {
    kotlin("multiplatform")
    id("android-native")
}

val defBaseName = "native"

kotlin {

    androidNativeArm64("native").apply {
        binaries {
            sharedLib {
                baseName = defBaseName
            }
        }
    }
    sourceSets {
        val nativeMain by getting {

        }
        val nativeTest by getting {

        }
    }
}
