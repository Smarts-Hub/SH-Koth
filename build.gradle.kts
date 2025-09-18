plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "dev.smartshub"
version = "1.0.2-SNAPSHOT"

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
    maven { url = uri("https://repo.bg-software.com/repository/api/") }
    maven("https://repo.triumphteam.dev/snapshots/")
    maven {
        name = "roinujnosde-repo"
        url = uri("https://repo.roinujnosde.me/releases/")
    }
    maven {
        name = "glaremasters repo"
        url = uri("https://repo.glaremasters.me/repository/towny/")
    }
    maven {
        name = "codemc-public"
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
    maven {
        name = "bentoboxworld"
        url = uri("https://repo.codemc.org/repository/bentoboxworld/")
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
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.12")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.12")

    implementation("dev.dejvokep:boosted-yaml:1.3.6")

    implementation("fr.mrmicky:fastboard:2.1.5")
    implementation("com.h2database:h2:2.1.214")
    implementation("dev.triumphteam:triumph-gui:3.1.13-SNAPSHOT")

    implementation("com.saicone.rtag:rtag:1.5.11")
    implementation("com.saicone.rtag:rtag-item:1.5.11")

    compileOnly ("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.UlrichBR:UClans-API:8.4.0")
    compileOnly("net.sacredlabyrinth.phaed.simpleclans:SimpleClans:2.19.2")
    compileOnly("com.github.booksaw:BetterTeams:4.13.4")
    compileOnly(files("libs/KingdomsX-1.17.16-BETA.jar"))
    compileOnly("dev.kitteh:factions:4.0.0")
    compileOnly("com.palmergames.bukkit.towny:towny:0.101.2.0")
    compileOnly("world.bentobox:bentobox:3.7.3-SNAPSHOT")
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:2025.1")
}

tasks.register<Copy>("copyDeps") {
    from(configurations.compileClasspath)
    into("libs")
}

tasks.shadowJar {
    archiveClassifier.set("")

    relocate("dev.triumphteam.gui", "dev.smartshub.shkoth.gui")
    relocate("org.h2", "dev.smartshub.libs.h2")
    relocate("revxrsal.commands", "dev.smartshub.libs.lamp")
    relocate("fr.mrmick", "dev.smartshub.libs.fastboard")
    relocate("com.saicone.rtag", "dev.smartshub.libs.rtag")
    relocate("dev.dejvokep.boostedyaml", "dev.smartshub.libs.boostedyaml")

    mergeServiceFiles()

    minimize {
        exclude(dependency(".*:.*"))
    }

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}