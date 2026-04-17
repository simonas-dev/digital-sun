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
        val SUNSET = HueRange(min = 355.0 / 360.0, max = 60.0 / 360.0)
    }
}

@Serializable
data class ShaderParameters(
    val seed: Int = 618,
    val spatialScale: Double = 0.05,
    val timeScale: Double = 0.124,
    val noiseType: NoiseType = NoiseType.PERLIN,
    val alphaPower: Double = 2.0,
    val alphaMin: Double = 0.0,
    val alphaMax: Double = 1.0,
    val fbmOctaves: Int = 8,
    val fbmLacunarity: Double = 1.415,
    val fbmGain: Double = 0.593,
    val hueRange: HueRange = HueRange.SUNSET,
)

enum class NoiseType {
    PERLIN,
    FBM_PERLIN,
    FBM_PERLIN_LINEAR
}
