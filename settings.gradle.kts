rootProject.name = "nexus"

dependencyResolutionManagement {
    repositories {
        mavenCentral();
    }
}

include("core", "inject", "netty", "plugin", "test-plugin")
