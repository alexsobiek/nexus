rootProject.name = "nexus"

dependencyResolutionManagement {
    repositories {
        mavenCentral();
    }
}

include("core", "inject", "tcp", "plugin", "test-plugin")
