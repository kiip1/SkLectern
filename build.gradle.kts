plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

group = "nl.kiipdevelopment"
version = "1.0.0"
description = "Supercharge your scripts with various new language features and performance gains"

repositories {
    mavenCentral()
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    val annotationsVersion: String by project
    val paperVersion: String by project
    val junitVersion: String by project
    val mockBukkitVersion: String by project

    compileOnly("org.jetbrains:annotations:$annotationsVersion")
    compileOnly("io.papermc.paper:paper-api:$paperVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.19:$mockBukkitVersion")
}

sourceSets {
    main {
        java {
            srcDir("src/bukkit/java")
            srcDir("src/main/java")
        }

        resources {
            srcDir("src/bukkit/resources")
            srcDir("src/main/resources")
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    shadowJar {
        archiveBaseName.set(project.name)
        mergeServiceFiles()
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("nl.kiipdevelopment.sklectern.Main")
}
