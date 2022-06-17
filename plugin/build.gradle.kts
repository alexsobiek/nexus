plugins {
    `java-library`
    id("Nexus.java-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":inject"));
    implementation("org.ow2.asm:asm:9.3")
}