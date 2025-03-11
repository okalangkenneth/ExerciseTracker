buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
}

allprojects {
//    repositories {
//        google()
//        mavenCentral()
//    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

