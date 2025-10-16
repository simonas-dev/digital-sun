plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "dev.simonas.digitalsun"
version = "1.0.0"

kotlin {
    linuxArm64("linuxArm64") {
        compilations.getByName("main") {
            cinterops {
                val ws2811 by creating {
                    defFile(project.file("src/nativeInterop/cinterop/ws2811.def"))
                    packageName("dev.simonas.digitalsun.rpi.native")
                }
            }
        }

        binaries {
            executable {
                entryPoint = "dev.simonas.digitalsun.rpi.main"
            }
        }
    }
}
