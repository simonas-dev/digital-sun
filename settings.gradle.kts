rootProject.name = "digital-sun"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

include("sun-core")
include("sun-openrndr")
include("sun-fastled")