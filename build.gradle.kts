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
    implementation("io.github.orioncraftmc:Mixin")
    implementation("net.fabricmc:mapping-io:0.3.0")

    runtimeOnly(":original-obf")
    runtimeOnly(":secret-agent")
}