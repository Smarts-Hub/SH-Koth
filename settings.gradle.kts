pluginManagement {
    plugins {
        kotlin("jvm") version "2.1.21"
        id("io.github.goooler.shadow") version "8.1.7"
        id("java-library")
        id("java")
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "SH-Koth"
include("api")
include("plugin")