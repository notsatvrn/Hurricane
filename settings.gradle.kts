pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}
rootProject.name = "hurricane"
include("hurricane-api", "hurricane-server")
