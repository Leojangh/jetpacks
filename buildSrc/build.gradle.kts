plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

tasks.compileKotlin.configure {
    targetCompatibility = "11"
    sourceCompatibility = "11"
}