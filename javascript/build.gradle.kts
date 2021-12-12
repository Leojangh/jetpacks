plugins {
//    id("com.android.library") incompatible
    kotlin("js")
    id("com.google.devtools.ksp")
    idea
}
//seems no effects
idea {
    module {
        sourceDirs = sourceDirs + file("build/generated/ksp/main/kotlin")
        testSourceDirs = testSourceDirs + file("build/generated/ksp/test/kotlin")
        generatedSourceDirs =
            generatedSourceDirs + file("build/generated/ksp/main/kotlin") + file("build/generated/ksp/test/kotlin")
    }
}

dependencies {
    ksp(project(":javascript-bridge-compiler"))
    testImplementation(kotlin("test-js"))
}

val distributionsName = "injection.js"
kotlin {

    sourceSets["main"].kotlin.srcDirs(
        //Because project implementation as dependencies is unavailable.
        "$rootDir/javascript-bridge/src/main/kotlin",
        //Although ksp added it to classpath,idea not recognized yet...
        "$buildDir/generated/ksp/main/kotlin"
    )

    //I can't use IR compiler as using ksp...
    js(/*IR*/) {

        moduleName = "javascript"

        browser {
            webpackTask {
                cssSupport.enabled = true
                outputFileName = distributionsName
                output.libraryTarget = "commonjs2"
            }
        }

        binaries.executable()
    }
}

task<Copy>("copyCompiledJs2app") {
    dependsOn += tasks.build
    description =
        "Copy the distribution that compiling from Kotlin/JS to project app's assets directory."
    from(File(buildDir, "distributions/$distributionsName"))
    into(File(rootDir, "app/src/main/assets"))
    doLast {
        println("The distributions move to app assets successfully!")
    }
}