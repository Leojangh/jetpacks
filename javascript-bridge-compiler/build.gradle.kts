plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:$KSP")
}

repositories {
    mavenCentral()
}
