plugins {
    kotlin("js")
}

group = "com.genlz"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib-js"))
    testImplementation(kotlin("test-js"))
}

val distributionsName = "injection.js"

kotlin {
    js(IR) {
        browser {
            webpackTask {
                cssSupport.enabled = true
                outputFileName = distributionsName
                output.libraryTarget = "commonjs2"
            }

            runTask {
                cssSupport.enabled = true
            }

            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
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