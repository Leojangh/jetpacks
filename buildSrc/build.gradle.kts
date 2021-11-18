plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        val java = "11"
        jvmTarget = java
        sourceCompatibility = java
        targetCompatibility = java
    }
}