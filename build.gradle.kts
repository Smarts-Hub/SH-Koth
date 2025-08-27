plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    id("io.github.revxrsal.zapper") version "1.0.3"
}

group = "dev.smartshub"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
    }
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }

    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }

    maven { url = uri("https://jitpack.io") }
}

dependencies {
    paperweight.paperDevBundle("1.21.7-R0.1-SNAPSHOT")
    implementation(project(":api"))
    compileOnly ("me.clip:placeholderapi:2.11.6")
    zap("io.github.revxrsal:lamp.common:4.0.0-beta.25")
    zap("io.github.revxrsal:lamp.bukkit:4.0.0-beta.25")
    zap("fr.mrmicky:fastboard:2.1.5")
    zap("com.saicone.rtag:rtag:1.5.11")
    zap("com.saicone.rtag:rtag-item:1.5.11")

    zap("com.h2database:h2:2.2.224")
    implementation(kotlin("stdlib-jdk8"))
}


tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}