plugins {
    `kotlin-dsl`
}
/**
 * [getRootProject] is buildSrc,instead of jetpacks.
 * */

kotlin {
    tasks.compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
            sourceCompatibility = "11"
            targetCompatibility = "11"
        }
    }
}

repositories {
    google()
    mavenCentral()
}

gradlePlugin {
//    plugins.register("AndroidNativePlugin") {
//        id = "android-native"
//        implementationClass = "plugin.AndroidNativePlugin"
//    }
}

dependencies {
//    implementation("com.android.tools.build:gradle-api:7.2.2")
    gradleApi()
    gradleKotlinDsl()
}