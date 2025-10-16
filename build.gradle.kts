plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.runtime) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

group = "dev.simonas.digitalsun"
version = "1.0.0"

// Root project is just a container for subprojects
// Actual implementations are in:
// - sun-core: Pure Kotlin logic
// - sun-openrndr: OPENRNDR visualization
// - sun-fastled: Raspberry Pi LED rendering
