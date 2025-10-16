import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

group = "dev.simonas.digitalsun"
version = "1.0.0"

dependencies {
    // Kotlin standard library
    implementation(kotlin("stdlib-jdk8"))

    // Coroutines for async operations
    implementation(libs.kotlinx.coroutines.core)

    // Serialization for parameter saving/loading
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    // Logging
    implementation(libs.slf4j.api)
    implementation(libs.kotlin.logging)

    // Testing
    testImplementation(libs.junit)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_0
        apiVersion = KotlinVersion.KOTLIN_2_0
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks.test {
    useJUnitPlatform()
}
