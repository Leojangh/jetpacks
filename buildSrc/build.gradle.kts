plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain {
        with(this as JavaToolchainSpec) {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }
    tasks.compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
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
    implementation("com.android.tools.build:gradle-api:7.0.4")
    gradleApi()
    gradleKotlinDsl()
}