plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

group = "dev.smartshub"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.triumphteam.dev/snapshots/")
}

dependencies {
    paperweight.paperDevBundle("1.21.7-R0.1-SNAPSHOT")
    implementation("dev.triumphteam:triumph-gui:3.1.13-SNAPSHOT")
}
