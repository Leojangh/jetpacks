plugins {
    kotlin("multiplatform")
}

val defBaseName = "native"

kotlin {
    val nativeTarget = androidNativeArm64("native")

    nativeTarget.apply {
        binaries {
            sharedLib {
                baseName = defBaseName
            }
        }
    }
    sourceSets {
        val nativeMain by getting
        val nativeTest by getting
    }
}

task<Copy>("copyCompiledNative2app") {
    dependsOn += tasks.build
    from(
        File(buildDir, "bin/native/releaseShared/lib$defBaseName.so"),
        File(buildDir, "bin/native/releaseShared/lib${defBaseName}_api.h")
    )
    into(File(rootDir, "app/src/main/jniLibs"))
    doLast {
        println("The distributions move to app successfully!")
    }
}
