plugins {
    java
    `maven-publish`
}

group = "io.github.orioncraftmc"
base { archivesName.set("orion-launcher") }
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://raw.githubusercontent.com/OrionCraftMC/OrionMaven/main/")
}

dependencies {
    implementation("info.picocli:picocli:4.6.3")
    implementation("io.github.orioncraftmc:sponge-mixin:0.0.1+mixin.0.8.5")
    implementation("net.fabricmc:mapping-io:0.3.0")
}

publishing {
    repositories {
        val lightCraftRepoDir = project.findProperty("lightcraft.repo.location")
        if (lightCraftRepoDir != null) {
            maven {
                name = "OrionCraftRepo"
                url = File(lightCraftRepoDir.toString()).toURI()
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            this.artifactId = "orion-launcher"
            from(components["java"])
        }
    }
}


