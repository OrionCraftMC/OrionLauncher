plugins {
    `java-library`
    `maven-publish`
}

group = "io.github.orioncraftmc"
base { archivesName.set("orion-launcher") }
version = "0.0.10-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://raw.githubusercontent.com/OrionCraftMC/OrionMaven/main/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("info.picocli:picocli:4.6.3")
    api("net.fabricmc:sponge-mixin:0.11.4+mixin.0.8.5")
    implementation("com.github.FabricMc:mapping-io:597f0722d6")
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("net.fabricmc:tiny-remapper:0.8.4")
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


