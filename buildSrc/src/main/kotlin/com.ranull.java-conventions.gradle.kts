/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal {
        content {}
    }
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        content {
            includeGroup("com.velocitypowered")
            includeGroup("io.papermc.paper")
            includeGroup("net.md-5")
        }
    }
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        content {
            includeGroup("net.md-5")
            includeGroup("org.bstats")
        }
    }
    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.Moulberry")
        }
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        content {
            // includeGroup("me.clip")
        }
    }
}

dependencies {
    implementation("org.jetbrains:annotations:23.1.0")
}

group = "com.ranull"
description = "Sync server chat across a network"
version = "1.5"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}