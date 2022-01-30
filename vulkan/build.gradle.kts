plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 31

    // When building this project with with SDK build tools of version earlier than 31.0.0, and
    // minSdkVersion 29+, the RenderScript compiler will fail with the following error message:
    //
    //     error: target API level '29' is out of range ('11' - '24')
    //
    // This issue has been fixed in SDK build tools 31.0.0.
    buildToolsVersion = "32.0.0"

    defaultConfig {
        minSdk = 28
        targetSdk = 31
        ndkVersion = "22.0.7026061"
        renderscriptTargetApi = 24

        externalNativeBuild {
            cmake.arguments("-DANDROID_TOOLCHAIN=clang", "-DANDROID_STL=c++_static")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    externalNativeBuild {
        cmake {
            version = "3.18.1"
            path("src/main/cpp/CMakeLists.txt")
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs {
                srcDirs("$android.ndkDirectory/sources/third_party/vulkan/src/build-android/jniLibs")
            }
        }
    }

//    lint {
//        isAbortOnError = false
//    }
}

dependencies {
    implementation("androidx.core:core-ktx:$coreKtx")
    implementation("androidx.appcompat:appcompat:$appcompat")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayout")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
