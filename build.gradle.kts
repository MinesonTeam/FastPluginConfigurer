import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.2.4"
    id("io.github.goooler.shadow") version "8.1.7"
}

val projectVersion: String by project
group = "kz.hxncus.mc"
version = property("projectVersion") as String
description = "Provides tools which help you configure your plugins faster."

allprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()

        maven("https://papermc.io/repo/repository/maven-public/") // Paper
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://libraries.minecraft.net/") // Minecraft repo
        maven("https://maven.enginehub.org/repo/")
        maven("https://jitpack.io") // JitPack

        mavenLocal()
    }

    dependencies {
        compileOnly(rootProject.libs.zmenu)
        compileOnly(fileTree("../libs/compileOnly/"))
        compileOnly("org.projectlombok:lombok:1.18.30")

        implementation("org.bstats:bstats-bukkit:3.0.2")
        implementation(fileTree("../libs/implementation/"))

        testImplementation(rootProject.libs.bundles.junit)
        annotationProcessor("org.projectlombok:lombok:1.18.30")
        testCompileOnly("org.projectlombok:lombok:1.18.30")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    }
}

dependencies {
    implementation(project(path = ":bukkit"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filesNotMatching(listOf("**/*.png", "**/*.ogg", "**/models/**", "**/textures/**", "**/font/**.json", "**/plugin.yml")) {
            expand(mapOf(project.version.toString() to projectVersion))
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = Charsets.UTF_8.name()
    }

    runServer {
        minecraftVersion("1.20.3")
    }

    shadowJar {
        archiveClassifier.set("")

        relocate("org.bstats", "kz.hxncus.mc.fastpluginconfigurer.metrics")

        manifest {
            attributes(
                mapOf(
                    "Built-By" to System.getProperty("user.name"),
                    "Version" to projectVersion,
                    "Build-Timestamp" to SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSSZ").format(Date.from(Instant.now())),
                    "Created-By" to "Gradle ${gradle.gradleVersion}",
                    "Build-Jdk" to "${System.getProperty("java.version")} ${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")}",
                    "Build-OS" to "${System.getProperty("os.name")} ${System.getProperty("os.arch")} ${System.getProperty("os.version")}",
                    "Compiled" to (project.findProperty("compiled")?.toString() ?: "true").toBoolean()
                )
            )
        }
        archiveFileName.set("FastPluginConfigurer-${projectVersion}.jar")
        archiveClassifier.set("")
    }

    compileJava.get().dependsOn(clean)
    build.get().dependsOn(shadowJar)
}
