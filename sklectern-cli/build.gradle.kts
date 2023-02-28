plugins {
    application
}

repositories {
    mavenCentral()
    maven(url = "https://repo.kenzie.mx/releases")
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":sklectern-shared"))
    implementation(project(":sklectern-core"))
}

application {
    mainClass.set("nl.kiipdevelopment.sklectern.Main")
}
