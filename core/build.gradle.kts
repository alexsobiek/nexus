plugins {
    `java-library`
}

dependencies {
    implementation("org.slf4j:slf4j-simple:1.7.36");
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1");
}

tasks.test {
    useJUnitPlatform()
}