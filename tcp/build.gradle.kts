plugins {
    `java-library`
    id("Nexus.java-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation("io.netty:netty-all:4.1.78.Final")
}