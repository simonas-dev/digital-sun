rootProject.name = "digital-sun"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

// Common modules
include("sun-core")

// Visualization module (macOS/Linux with graphics)
include("sun-openrndr")

// Raspberry Pi LED control module
include("target-rpi")