
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

    maven {
        name = "roinujnosde-repo"
        url = uri("https://repo.roinujnosde.me/releases/")
    }

    maven {
        name = "glaremasters repo"
        url = uri("https://repo.glaremasters.me/repository/towny/")
    }

    exclusiveContent {
        forRepository {
            maven("https://dependency.download/releases")
        }

        filter {
            includeGroup("dev.kitteh")
        }
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.7-R0.1-SNAPSHOT")
    implementation(project(":api"))
    zap("io.github.revxrsal:lamp.common:4.0.0-beta.25")
    zap("io.github.revxrsal:lamp.bukkit:4.0.0-beta.25")
    zap("fr.mrmicky:fastboard:2.1.5")
    zap("com.saicone.rtag:rtag:1.5.11")
    zap("com.saicone.rtag:rtag-item:1.5.11")
    zap("com.h2database:h2:2.2.224")

    compileOnly ("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.UlrichBR:UClans-API:8.4.0")
    compileOnly("net.sacredlabyrinth.phaed.simpleclans:SimpleClans:2.19.2")
    compileOnly("com.github.booksaw:BetterTeams:4.13.4")
    compileOnly(files("libs/KingdomsX-1.17.20-BETA.jar"))
    compileOnly("dev.kitteh:factions:4.0.0")
    compileOnly("com.palmergames.bukkit.towny:towny:0.101.2.0")
}


tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}