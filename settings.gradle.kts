pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
    }
}

includeBuild("../one_piece_rpg") {
    dependencySubstitution {
        substitute(module("maven.modrinth:one_piece_api")).using(project(":"))
    }
}
includeBuild("../onepiece-1.21.4") {
    dependencySubstitution {
        substitute(module("github.ssourabh58.onepiece:onepiece")).using(project(":"))
    }
}

rootProject.name = "one-piece-content"
