rootProject.name = "nexus"

dependencyResolutionManagement {
    repositories {
        mavenCentral();
    }
}

include("core", "inject", "plugin", "test-plugin")
