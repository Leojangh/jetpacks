package plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.task
import java.io.File

class AndroidNativePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        configureTasks(target)

    }

    private fun configureTasks(target: Project) {
        val buildDir = target.buildDir
        val rootDir = target.rootDir
        val defBaseName = "native"
        val task1 = "copyDynamicalLib"
        val task2 = "copyHeader"
        target.task<Copy>(task1) {
            from(File("$buildDir/bin/native/releaseShared/lib$defBaseName.so"))
            into(File("$rootDir/app/src/main/cpp/lib/arm64-v8a"))
            doLast {
                println("The dynamic lib move to app successfully!")
            }
        }
        target.task<Copy>(task2) {
            from(File("$buildDir/bin/native/releaseShared/lib${defBaseName}_api.h"))
            into(File("$rootDir/app/src/main/cpp/lib/includes"))
            doLast {
                println("The header move to app successfully!")
            }
        }
        target.tasks.getByName("build") {
            finalizedBy(
                task1,
                task2
            )
        }
    }
}