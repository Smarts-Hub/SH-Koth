plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
}

dependencies {
    implementation(project(":api"))

    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.12")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.12")

    implementation("dev.dejvokep:boosted-yaml:1.3.6")
    implementation("fr.mrmicky:fastboard:2.1.5")
    implementation("com.h2database:h2:2.1.214")
    implementation("dev.triumphteam:triumph-gui:3.1.13")

    implementation("com.saicone.rtag:rtag:1.5.11")
    implementation("com.saicone.rtag:rtag-item:1.5.11")
    implementation("it.sauronsoftware.cron4j:cron4j:2.2.5")

    implementation("io.github.4drian3d:jdwebhooks:1.1.0")

    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")

    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.UlrichBR:UClans-API:8.4.0")
    compileOnly("net.sacredlabyrinth.phaed.simpleclans:SimpleClans:2.19.2")
    compileOnly("com.github.booksaw:BetterTeams:4.13.4")

    compileOnly("dev.kitteh:factions:4.0.0")
    compileOnly("com.palmergames.bukkit.towny:towny:0.101.2.0")
    compileOnly("world.bentobox:bentobox:3.7.3-SNAPSHOT")
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:2025.1")

    compileOnly(files("../libs/KingdomsX.jar"))
}

tasks {
    shadowJar {
        archiveFileName.set("SH-Koth-${project.version}.jar")
        archiveClassifier.set("")

        from(project(":plugin").sourceSets.main.get().output)
        from(project(":api").sourceSets.main.get().output)

        relocate("dev.triumphteam.gui", "dev.smartshub.shkoth.libs.gui")
        relocate("org.h2", "dev.smartshub.shkoth.libs.h2")
        relocate("revxrsal.commands", "dev.smartshub.shkoth.libs.lamp")
        relocate("fr.mrmick", "dev.smartshub.shkoth.libs.fastboard")
        relocate("com.saicone.rtag", "dev.smartshub.shkoth.libs.rtag")
        relocate("it.sauronsoftware", "dev.smartshub.shkoth.libs.cron4j")
        relocate("dev.dejvokep.boostedyaml", "dev.smartshub.shkoth.libs.boostedyaml")

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        exclude("**/org/jetbrains/**")
        exclude("**/org/intellij/**")
        exclude("META-INF/MANIFEST.MF")
        exclude("**/*.kotlin_metadata")
        exclude("**/*.kotlin_module")
        exclude("**/*.SF")
        exclude("**/*.DSA")
        exclude("**/*.RSA")
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
    build {
        dependsOn(shadowJar)
    }
}