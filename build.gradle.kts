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
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }

    //Jetpack
    extra["coreKtx"] = "1.7.0"
    extra["lifecycle"] = "2.4.0"
    extra["appcompat"] = "1.3.1"
    extra["material"] = "1.4.0"
    extra["room"] = "2.3.0"
    extra["startup"] = "1.1.0"
    extra["palette"] = "1.0.0"
    extra["paging"] = "3.0.1"
    extra["swiperRefreshLayout"] = "1.1.0"
    extra["constraintLayout"] = "2.1.1"
    extra["work"] = "2.7.0" //for targeting S+
    extra["browser"] = "1.4.0-rc01"
    extra["webkit"] = "1.4.0"

    //Dynamic version
    extra["window"] = "+"
    extra["splash"] = "+"

    //Third party
    extra["retrofit"] = "2.9.0"
    extra["okhttp"] = "4.9.1"
    extra["coil"] = "1.3.2"
    extra["photoView"] = "2.2.0"
    extra["gson"] = "2.8.7"

}