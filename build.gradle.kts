plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "dev.smartshub"
version = "1.2.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
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

subprojects {
    apply(plugin = "java")

    group = "dev.smartshub"
    version = "1.2.1-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc-repo" }
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {}
        maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
        maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://repo.eternalcode.pl/releases") }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":plugin"))

    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.12")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.12")

    implementation("dev.dejvokep:boosted-yaml:1.3.6")

    implementation("fr.mrmicky:fastboard:2.1.5")
    implementation("com.h2database:h2:2.1.214")
    implementation("dev.triumphteam:triumph-gui:3.1.13")

    implementation("com.saicone.rtag:rtag:1.5.11")
    implementation("com.saicone.rtag:rtag-item:1.5.11")

    implementation("it.sauronsoftware.cron4j:cron4j:2.2.5")

    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.UlrichBR:UClans-API:8.4.0")
    compileOnly("net.sacredlabyrinth.phaed.simpleclans:SimpleClans:2.19.2")
    compileOnly("com.github.booksaw:BetterTeams:4.13.4")
    compileOnly(files("libs/KingdomsX-1.17.16-BETA.jar"))
    compileOnly("dev.kitteh:factions:4.0.0")
    compileOnly("com.palmergames.bukkit.towny:towny:0.101.2.0")
    compileOnly("world.bentobox:bentobox:3.7.3-SNAPSHOT")
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:2025.1")
}

tasks.shadowJar {
    archiveClassifier.set("")

    manifest {
        attributes(
            "Main-Class" to "dev.smartshub.shkoth.SHKoth"
        )
    }

    from(project(":plugin").sourceSets.main.get().output)
    from(project(":api").sourceSets.main.get().output)

    relocate("dev.triumphteam.gui", "dev.smartshub.shkoth.libs.gui")
    relocate("org.h2", "dev.smartshub.shkoth.libs.h2")
    relocate("revxrsal.commands", "dev.smartshub.shkoth.libs.lamp")
    relocate("fr.mrmick", "dev.smartshub.shkoth.libs.fastboard")
    relocate("com.saicone.rtag", "dev.smartshub.shkoth.libs.rtag")
    relocate("dev.dejvokep.boostedyaml", "dev.smartshub.shkoth.libs.boostedyaml")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    exclude("META-INF/MANIFEST.MF")
    exclude("**/*.kotlin_metadata")
    exclude("**/*.kotlin_module")
    exclude("**/*.SF")
    exclude("**/*.DSA")
    exclude("**/*.RSA")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}