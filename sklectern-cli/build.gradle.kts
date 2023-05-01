plugins {
    application
}

repositories {
    mavenCentral()
    maven(url = "https://repo.kenzie.mx/releases")
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    val junitVersion: String by project

    implementation(project(":sklectern-shared"))
    implementation(project(":sklectern-core"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

application {
    mainClass.set("nl.kiipdevelopment.sklectern.Main")
}
