plugins { java }

subprojects {
    group = "dev.smartshub"
    version = "1.2.1-SNAPSHOT"

    apply(plugin = "java-library")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    repositories {
        mavenCentral()

        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        maven("https://repo.codemc.io/repository/maven-public")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-snapshots")
        maven("https://jitpack.io")
        maven("https://repo.bg-software.com/repository/api/")
        maven("https://repo.triumphteam.dev/snapshots/")
        maven("https://repo.eternalcode.pl/releases")
        maven("https://repo.roinujnosde.me/releases/")
        maven("https://repo.glaremasters.me/repository/towny/")
        maven("https://repo.codemc.org/repository/bentoboxworld")
        maven("https://dependency.download/releases/")
    }
}