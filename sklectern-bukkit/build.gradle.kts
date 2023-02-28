plugins {
    java
}

repositories {
    mavenCentral()
    maven(url = "https://repo.kenzie.mx/releases")
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    val paperVersion: String by project
    val centurionVersion: String by project

    val junitVersion: String by project
    val mockBukkitVersion: String by project

    implementation(project(":sklectern-shared"))
    implementation(project(":sklectern-core"))

    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    implementation("mx.kenzie:centurion-minecraft:$centurionVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.19:$mockBukkitVersion")
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
}