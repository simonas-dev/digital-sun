package dev.simonas.digitalsun.core

import kotlinx.serialization.Serializable

@Serializable
data class ShaderParameters(
    val seed: Int = 618,
    val spatialScale: Double = 0.05,
    val timeScale: Double = 0.224,
    val noiseType: NoiseType = NoiseType.PERLIN,
    val alphaPower: Double = 4.0,
    val alphaMin: Double = 0.0,
    val alphaMax: Double = 1.0,
    val fbmOctaves: Int = 8,
    val fbmLacunarity: Double = 1.415,
    val fbmGain: Double = 0.593
)

enum class NoiseType {
    PERLIN,
    FBM_PERLIN,
    FBM_PERLIN_LINEAR
}
