plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain {
        with(this as JavaToolchainSpec){
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }
}