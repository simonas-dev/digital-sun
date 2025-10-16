import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.internal.os.OperatingSystem
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

group = "dev.simonas.digitalsun"
version = "1.0.0"

val applicationMainClass = "dev.simonas.digitalsun.openrndr.TemplateProgramKt"

/** ##additional ORX features to be added to this project */
val orxFeatures = setOf<String>(
    "orx-camera",
    "orx-color",
    "orx-composition",
    "orx-compositor",
    "orx-delegate-magic",
    "orx-envelopes",
    "orx-fx",
    "orx-gui",
    "orx-image-fit",
    "orx-no-clear",
    "orx-noise",
    "orx-olive",
    "orx-panel",
    "orx-shade-styles",
    "orx-shapes",
    "orx-svg",
    "orx-text-writer",
    "orx-video-profiles",
    "orx-view-box",
)

/** ## additional ORML features to be added to this project */
val ormlFeatures = setOf<String>(
)

/** ## additional OPENRNDR features to be added to this project */
val openrndrFeatures = setOfNotNull(
    if (DefaultNativePlatform("current").architecture.name != "arm-v8") "video" else null
)

/** ## configure the type of logging this project uses */
enum class Logging { NONE, SIMPLE, FULL }

val applicationLogging = Logging.FULL

// ------------------------------------------------------------------------------------------------------------------ //

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runtime)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    // Depend on sun-core module
    implementation(project(":sun-core"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.slf4j.api)
    implementation(libs.kotlin.logging)

    when (applicationLogging) {
        Logging.NONE -> runtimeOnly(libs.slf4j.nop)
        Logging.SIMPLE -> runtimeOnly(libs.slf4j.simple)
        Logging.FULL -> {
            runtimeOnly(libs.log4j.slf4j2)
            runtimeOnly(libs.log4j.core)
            runtimeOnly(libs.jackson.databind)
            runtimeOnly(libs.jackson.json)
        }
    }
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(libs.junit)
}

// ------------------------------------------------------------------------------------------------------------------ //

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

// ------------------------------------------------------------------------------------------------------------------ //

application {
    mainClass = if (hasProperty("openrndr.application"))
        "${property("openrndr.application")}"
    else
        applicationMainClass
}

tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = applicationMainClass
            attributes["Implementation-Version"] = project.version
        }
        minimize {
            exclude(dependency("org.openrndr:openrndr-gl3:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
            exclude(dependency("org.slf4j:slf4j-simple:.*"))
            exclude(dependency("org.apache.logging.log4j:log4j-slf4j2-impl:.*"))
            exclude(dependency("com.fasterxml.jackson.core:jackson-databind:.*"))
            exclude(dependency("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:.*"))
            exclude(dependency("org.bytedeco:.*"))
        }
    }
}

// ------------------------------------------------------------------------------------------------------------------ //

runtime {
    jpackage {
        imageName = "digital-sun-openrndr"
        skipInstaller = true
        if (OperatingSystem.current().isMacOsX) {
            jvmArgs.add("-XstartOnFirstThread")
            jvmArgs.add("-Duser.dir=${"$"}APPDIR/../Resources")
        }
    }
    options = listOf("--strip-debug", "--compress", "1", "--no-header-files", "--no-man-pages")
    modules = listOf("jdk.unsupported", "java.management", "java.desktop")
}

// ------------------------------------------------------------------------------------------------------------------ //

class Openrndr {
    val openrndrVersion = libs.versions.openrndr.get()
    val orxVersion = libs.versions.orx.get()
    val ormlVersion = libs.versions.orml.get()

    // choices are "orx-tensorflow-gpu", "orx-tensorflow"
    val orxTensorflowBackend = "orx-tensorflow"

    val currArch = DefaultNativePlatform("current").architecture.name
    val currOs = OperatingSystem.current()
    val os = if (project.hasProperty("targetPlatform")) {
        val supportedPlatforms = setOf("windows", "macos", "linux-x64", "linux-arm64")
        val platform: String = project.property("targetPlatform") as String
        if (platform !in supportedPlatforms) {
            throw IllegalArgumentException("target platform not supported: $platform")
        } else {
            platform
        }
    } else when {
        currOs.isWindows -> "windows"
        currOs.isMacOsX -> when (currArch) {
            "aarch64", "arm-v8" -> "macos-arm64"
            else -> "macos"
        }

        currOs.isLinux -> when (currArch) {
            "x86-64" -> "linux-x64"
            "aarch64" -> "linux-arm64"
            else -> throw IllegalArgumentException("architecture not supported: $currArch")
        }

        else -> throw IllegalArgumentException("os not supported: ${currOs.name}")
    }

    fun orx(module: String) = "org.openrndr.extra:$module:$orxVersion"
    fun orml(module: String) = "org.openrndr.orml:$module:$ormlVersion"
    fun openrndr(module: String) = "org.openrndr:openrndr-$module:$openrndrVersion"
    fun openrndrNatives(module: String) = "org.openrndr:openrndr-$module-natives-$os:$openrndrVersion"
    fun orxNatives(module: String) = "org.openrndr.extra:$module-natives-$os:$orxVersion"

    init {
        dependencies {
            runtimeOnly(openrndr("gl3"))
            runtimeOnly(openrndrNatives("gl3"))
            implementation(openrndr("openal"))
            runtimeOnly(openrndrNatives("openal"))
            implementation(openrndr("application"))
            implementation(openrndr("animatable"))
            implementation(openrndr("extensions"))
            implementation(openrndr("filter"))
            implementation(openrndr("dialogs"))
            if ("video" in openrndrFeatures) {
                implementation(openrndr("ffmpeg"))
                runtimeOnly(openrndrNatives("ffmpeg"))
            }
            for (feature in orxFeatures) {
                implementation(orx(feature))
            }
            for (feature in ormlFeatures) {
                implementation(orml(feature))
            }
            if ("orx-tensorflow" in orxFeatures) runtimeOnly("org.openrndr.extra:$orxTensorflowBackend-natives-$os:$orxVersion")
            if ("orx-kinect-v1" in orxFeatures) runtimeOnly(orxNatives("orx-kinect-v1"))
            if ("orx-olive" in orxFeatures) implementation(libs.kotlin.script.runtime)
        }
    }
}

val openrndr = Openrndr()
