plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:$KSP")
}

kotlinOptions {
    freeCompilerArgs = listOf(
        "-opt-in=kotlin.RequiresOptIn",
        "-Xuse-k2"
    )
}

repositories {
    mavenCentral()
}
