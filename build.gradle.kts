plugins {
    java
}

group = "io.github.orioncraftmc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    flatDir {
        dir("jars")
    }
}

dependencies {
    implementation("info.picocli:picocli:4.6.3")
    implementation("net.fabricmc:sponge-mixin:0.11.4+mixin.0.8.5")

    runtimeOnly(":original")
    runtimeOnly(":secret-agent")
}