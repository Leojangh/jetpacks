package plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get

class KotlinCommon : Plugin<Project> {
    override fun apply(target: Project) {
        val any = target.extensions["kotlin"]

        println(any.javaClass)
    }
}