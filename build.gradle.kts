plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
    id("maven-publish")
    id("java-library")
}

group = "me.levitate"
version = "1.3.0-beta"

repositories {
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    // Plugin Hooks
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.luckperms:api:5.4")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    // JSON Storage
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1")

    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.moshi:moshi-adapters:1.15.1")

    // TOML
    implementation("de.exlll:configlib-paper:4.5.0")

    // Redis Caching
    implementation("redis.clients:jedis:5.2.0")

    // Commands
    api("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withJavadocJar()
    withSourcesJar()
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand(
                "version" to project.version
            )
        }
    }

    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    javadoc {
        options {
            (this as StandardJavadocDocletOptions).apply {
                addStringOption("Xdoclint:none", "-quiet")
                addStringOption("encoding", "UTF-8")
                addStringOption("charSet", "UTF-8")
                addBooleanOption("html5", true)
                links("https://docs.oracle.com/en/java/javase/17/docs/api/")
                links("https://jd.papermc.io/paper/1.20/")
            }
        }
    }

    shadowJar {
        relocate("de.exlll.configlib", "me.levitate.config")
        relocate("co.aikar.commands", "me.levitate.acf")
        relocate("co.aikar.locales", "me.levitate.locales")
        relocate("dev.triumphteam.gui", "me.levitate.gui")
        relocate("com.fasterxml.jackson", "me.levitate.jackson")

        archiveClassifier.set("")
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        enabled = false
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            artifact(tasks["shadowJar"])
            artifact(tasks["javadocJar"])
            artifact(tasks["sourcesJar"])
        }
    }

    repositories {
        mavenLocal()
    }
}