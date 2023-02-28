plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven(url = "https://repo.kenzie.mx/releases")
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    val annotationsVersion: String by project
    val guavaVersion: String by project

    val centurionVersion: String by project
    val adventureVersion: String by project

    api("org.jetbrains:annotations:$annotationsVersion")
    api("com.google.guava:guava:$guavaVersion")

    api("mx.kenzie:centurion-core:$centurionVersion")
    api("net.kyori:adventure-api:$adventureVersion")
    api("net.kyori:adventure-text-serializer-plain:$adventureVersion")
}
