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

// Platform-specific modules
val osName = System.getProperty("os.name").lowercase()
val osArch = System.getProperty("os.arch").lowercase()

when {
    osName.contains("mac") || osName.contains("darwin") -> {
        // macOS: Include OPENRNDR visualization module
        include("sun-openrndr")
        println("Building for macOS: Including sun-openrndr")
    }
    osName.contains("linux") && (osArch.contains("arm") || osArch.contains("aarch64")) -> {
        // Raspberry Pi (Linux ARM): Include rpi_ws281x hardware module
        include("target-rpi")
        println("Building for Raspberry Pi: Including target-rpi")
    }
    osName.contains("linux") -> {
        // Linux x86: Include both for testing
        include("sun-openrndr")
        include("target-rpi")
        println("Building for Linux x86: Including both modules")
    }
    else -> {
        println("Unknown platform: $osName ($osArch)")
        println("Including all modules by default")
        include("sun-openrndr")
        include("target-rpi")
    }
}