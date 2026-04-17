plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    application
}

group = "dev.simonas.digitalsun"
version = "1.0.0"

application {
    mainClass.set("dev.simonas.digitalsun.rpi.MainKt")
}

dependencies {
    implementation(project(":sun-core"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.slf4j.api)
    implementation(libs.kotlin.logging)

    // JNA for native library access
    implementation("net.java.dev.jna:jna:5.14.0")

    implementation(libs.mosaic.runtime)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "dev.simonas.digitalsun.rpi.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
