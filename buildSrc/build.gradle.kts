plugins {
    `kotlin-dsl`
}
/**
 * [getRootProject] is buildSrc,instead of jetpacks.
 * */

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