plugins {
    id("fabric-loom") version "1.11-SNAPSHOT"
    id("maven-publish")
}

version = findProperty("mod_version") as String
group = findProperty("maven_group") as String

base {
    archivesName = findProperty("archives_base_name") as String
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    mavenCentral()
    maven { name = "Fabric"; url = uri("https://maven.fabricmc.net/") }
    maven { name = "Modrinth"; url = uri("https://api.modrinth.com/maven") }
    maven { name = "CurseMaven"; url = uri("https://cursemaven.com") }
    maven { name = "Ladysnake Mods"; url = uri("https://maven.ladysnake.org/releases") }
    maven { name = "KosmX"; url = uri("https://maven.kosmx.dev/") }
    maven { name = "Shedaniel"; url = uri("https://maven.shedaniel.me/") }


}


dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")


    //modApi(project(":one_piece_rpg"))
    modApi("maven.modrinth:one_piece_api:TPN4R108")

    // Pufferfish's Skills
    modApi("maven.modrinth:skills:2UxjrzOS")

    // Spell Engine and transitive dependencies
    modApi("maven.modrinth:spell-engine:1.8.16+1.21.1-fabric")
    modApi("maven.modrinth:spell-power:1.4.6+1.21.1-fabric")

    modRuntimeOnly("com.github.ZsoltMolnarrr:TinyConfig:3.1.0")
    modRuntimeOnly("maven.modrinth:cloth-config:15.0.140+fabric")
    modApi("org.ladysnake.cardinal-components-api:cardinal-components-base:6.1.0")
    modApi("org.ladysnake.cardinal-components-api:cardinal-components-entity:6.1.0")
    modRuntimeOnly("maven.modrinth:trinkets:3.10.0")
    modRuntimeOnly("maven.modrinth:playeranimator:2.0.0+1.21.1-fabric")

    // Development/testing mods
    modRuntimeOnly("curse.maven:amecs-reborn-1233121:6487881")
    modRuntimeOnly("curse.maven:modmenu-308702:5810603")
}


sourceSets {
    main {
        resources {
            srcDir("src/main/generated")
        }
    }
}


loom {

    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            runDir("run")
            programArgs("--username", "FabricDev")
            vmArgs("-javaagent:C:/Users/lamal/.gradle/caches/modules-2/files-2.1/net.fabricmc/sponge-mixin/0.16.3+mixin.0.8.7/3e535042688d1265447e52ad86950b7d9678a5fa/sponge-mixin-0.16.3+mixin.0.8.7.jar")
        }

        named("server") {
            server()
            configName = "Fabric Server"
            runDir("server")
            source(sourceSets.main.get())
            vmArgs("-javaagent:C:/Users/lamal/.gradle/caches/modules-2/files-2.1/net.fabricmc/sponge-mixin/0.16.3+mixin.0.8.7/3e535042688d1265447e52ad86950b7d9678a5fa/sponge-mixin-0.16.3+mixin.0.8.7.jar")
        }


        create("datagen") {
            server()
            configName = "Datagen - Main"
            source(sourceSets.main.get())
            runDir("build/datagen")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/main/generated")}")
            vmArg("-Dfabric-api.datagen.modid=one_piece_content")

        }

    }
    // Define two separate mods
    mods {
        create("one_piece_content") {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.named<Jar>("jar") {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}