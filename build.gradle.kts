plugins {
    java
}

group = "io.github.orioncraftmc"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://raw.githubusercontent.com/OrionCraftMC/OrionMaven/main/")
    flatDir {
        dir("jars")
    }
}

dependencies {
    implementation("info.picocli:picocli:4.6.3")
    implementation("io.github.orioncraftmc:sponge-mixin:0.0.1+mixin.0.8.5")
    implementation("net.fabricmc:mapping-io:0.3.0")

    runtimeOnly(":original-obf")
    runtimeOnly(":secret-agent")
}

