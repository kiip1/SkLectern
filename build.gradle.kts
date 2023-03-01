plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

allprojects {
    group = "nl.kiipdevelopment"
    version = "1.0.1-alpha"
    description = "Supercharge your scripts with various new language features and performance gains"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

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
            doLast {
                copy {
                    from(buildDir)
                    into(rootProject.buildDir)
                }
            }

            dependsOn(shadowJar)
        }
    }
}