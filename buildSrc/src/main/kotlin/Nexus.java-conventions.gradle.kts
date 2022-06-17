import com.github.monosoul.yadegrap.DelombokTask
import org.gradle.kotlin.dsl.dependencies

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow")
    id("com.github.monosoul.yadegrap")
}

group = "com.alexsobiek.nexus"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
    implementation("org.slf4j:slf4j-simple:1.7.36");
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1");
}

tasks {

    test {
        useJUnitPlatform()
    }

    jar {
        archiveClassifier.set("unshaded")
    }

    shadowJar {
        minimize() {
            exclude(dependency("ch.qos.logback:logback-classic"))
        }
        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }

    val delombok = "delombok"(DelombokTask::class)

    "javadoc"(Javadoc::class) {
        dependsOn(delombok)
        setSource(delombok)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}