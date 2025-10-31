package dev.simonas.digitalsun.core

import kotlinx.serialization.Serializable

/**
 * Represents a hue range in HSV color space.
 * Hue values are normalized to [0.0, 1.0] where:
 * - 0.0 = 0° (red)
 * - 0.166667 = 60° (yellow)
 * - 0.5 = 180° (cyan)
 * - 0.833333 = 300° (magenta)
 * - 1.0 = 360° (red, wraps around)
 */
@Serializable
data class HueRange(
    val min: Double,
    val max: Double
) {
    companion object {
        /**
         * Warm color range: magenta (300°) through red (0°) to yellow (60°)
         */
//        val WARM = HueRange(min = 0.833333, max = 0.166667)
//       val FAINAS_V1 = HueRange(min = 355.0 / 360.0, max = 60.0 / 360.0) // bestest

        val FAINAS_V1 = HueRange(min = 169.0 / 360.0, max = 69.0 / 360.0) //Emilijos cool
        //val FAINAS_V1 = HueRange(min = 25.0 / 360.0, max = 10.0 / 360.0)
//        val FAINAS_V1 = HueRange(min = 60.0 / 360.0, max = 180.0 / 360.0)
//        val FAINAS_V1 = HueRange(min = 355.0 / 360.0, max = 60.0 / 360.0)
    }
}

@Serializable
data class ShaderParameters(
    val seed: Int = 618,
    val spatialScale: Double = 0.05,
    val timeScale: Double = 0.124,
    val noiseType: NoiseType = NoiseType.PERLIN,
    val alphaPower: Double = 4.0,
    val alphaMin: Double = 0.0,
    val alphaMax: Double = 1.0,
    val fbmOctaves: Int = 8,
    val fbmLacunarity: Double = 1.415,
    val fbmGain: Double = 0.593,
    val hueRange: HueRange = HueRange.FAINAS_V1
)

enum class NoiseType {
    PERLIN,
    FBM_PERLIN,
    FBM_PERLIN_LINEAR
}
