plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

gradlePlugin {
    plugins.register("AndroidNativePlugin") {
        id = "android-native"
        implementationClass = "plugin.AndroidNativePlugin"
    }
}

dependencies {
    implementation("com.android.tools.build:gradle-api:7.0.3")
    gradleApi()
}

tasks.compileKotlin {
    targetCompatibility = "11"
    sourceCompatibility = "11"
}