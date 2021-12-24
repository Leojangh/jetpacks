plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.10.2")
    implementation("com.google.devtools.ksp:symbol-processing-api:$KSP")
}