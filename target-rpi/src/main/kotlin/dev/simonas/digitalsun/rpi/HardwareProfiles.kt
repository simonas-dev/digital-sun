package dev.simonas.digitalsun.rpi

import dev.simonas.digitalsun.core.Pixel
import dev.simonas.digitalsun.core.Stage
import dev.simonas.digitalsun.core.Stages

data class HardwareProfile(
    val name: String,
    val gpioPin: Int,
    val stage: Stage,
    val wiring: (List<Pixel>) -> List<Pixel> = { it },
)

object HardwareProfiles {

    val V1 = HardwareProfile(
        name = "v1",
        gpioPin = 18,
        stage = Stages.V1,
    )

    val V2 = HardwareProfile(
        name = "v2",
        gpioPin = 10,
        stage = Stages.V2,
    )

    val V3 = HardwareProfile(
        name = "v3",
        gpioPin = 18,
        stage = Stages.V3,
        wiring = { it.serpentine() },
    )

    fun hwForStage(stage: Stage): HardwareProfile =
        when (stage) {
            Stages.V1 -> HardwareProfiles.V1
            Stages.V2 -> HardwareProfiles.V2
            Stages.V3 -> HardwareProfiles.V3
            else -> error("Unknown stage: $stage")
        }
}
