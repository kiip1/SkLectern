plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

allprojects {
    group = "nl.kiipdevelopment"
    version = "1.0.0"
    description = "Supercharge your scripts with various new language features and performance gains"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    shadowJar {
        archiveBaseName.set(project.name)
        mergeServiceFiles()
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}
