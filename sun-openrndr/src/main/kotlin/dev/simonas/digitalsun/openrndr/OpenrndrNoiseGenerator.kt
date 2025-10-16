package dev.simonas.digitalsun.openrndr

import dev.simonas.digitalsun.core.NoiseGenerator

/**
 * OPENRNDR implementation of NoiseGenerator that uses OPENRNDR's noise functions
 */
class OpenrndrNoiseGenerator : NoiseGenerator {

    override fun perlin(seed: Int, x: Double, y: Double, z: Double): Double {
        return org.openrndr.extra.noise.perlin(seed, x, y, z)
    }

    override fun perlinLinear(seed: Int, x: Double, y: Double, z: Double): Double {
        return org.openrndr.extra.noise.perlinLinear(seed, x, y, z)
    }

    override fun fbm(
        seed: Int,
        x: Double,
        y: Double,
        z: Double,
        octaves: Int,
        lacunarity: Double,
        gain: Double,
        useLinear: Boolean
    ): Double {
        return if (useLinear) {
            org.openrndr.extra.noise.fbm(seed, x, y, z, ::perlinLinear, octaves, lacunarity, gain)
        } else {
            org.openrndr.extra.noise.fbm(seed, x, y, z, ::perlin, octaves, lacunarity, gain)
        }
    }
}