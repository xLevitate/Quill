plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
    id("maven-publish")
    id("java-library")
}

group = "me.levitate"
version = "2.6.3"

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
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")

    implementation("de.exlll:configlib-paper:4.5.0")

    api("dev.triumphteam:triumph-gui:3.1.10")
    api("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}

// Configure Java version compatibility
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withJavadocJar()
    withSourcesJar()
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    shadowJar {
        // Relocate dependencies to avoid conflicts
        relocate("de.exlll.configlib", "me.levitate.config")
        relocate("co.aikar.commands", "me.levitate.acf")
        relocate("co.aikar.locales", "me.levitate.locales")
        relocate("dev.triumphteam.gui", "me.levitate.gui")
        relocate("com.fasterxml.jackson", "me.levitate.jackson")

        archiveClassifier.set("")
        minimize()
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

    build {
        dependsOn(shadowJar)
    }

    // Disable default jar task
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
}