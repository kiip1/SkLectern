plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven(url = "https://repo.kenzie.mx/releases")
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    val junitVersion: String by project

    implementation(project(":sklectern-shared"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
