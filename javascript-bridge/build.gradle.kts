/**
 * This subproject(module) just as a common source sets between
 * project 'app' and 'javascript'.Must set source sets for them
 * manually instead of adding this project as dependency because
 * this is project of kotlin/jvm,which is incompatible with kotlin/js.
 * So the project 'javascript' can't rely on this through dependency
 * implementation.
 */
plugins {
    kotlin("jvm")
}

