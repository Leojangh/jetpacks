@file:Suppress("LocalVariableName")

buildscript {

    val kotlin_version: String by project
    val hilt_version: String by project
    val nav_version: String by project

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath(kotlin("gradle-plugin", kotlin_version))
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }

    //Jetpack
    extra["coreKtx"] = "1.6.0"
    extra["lifecycle"] = "2.4.0-rc01"
    extra["appcompat"] = "1.3.1"
    extra["material"] = "1.4.0"
    extra["room"] = "2.3.0"
    extra["startup"] = "1.1.0"
    extra["palette"] = "1.0.0"
    extra["paging"] = "3.0.1"
    extra["swiperRefreshLayout"] = "1.1.0"
    extra["constraintLayout"] = "2.1.1"
    extra["work"] = "2.7.0-beta01"//for targeting S+

    //Dynamic version
    extra["window"] = "1.0.0-beta+"
    extra["splash"] = "1.0.0-alpha+"

    //Third party
    extra["retrofit"] = "2.9.0"
    extra["okhttp"] = "4.9.1"
    extra["coil"] = "1.3.2"
    extra["photoView"] = "2.2.0"
    extra["gson"] = "2.8.7"

}