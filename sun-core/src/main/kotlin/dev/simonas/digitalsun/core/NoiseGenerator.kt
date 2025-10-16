package dev.simonas.digitalsun.core

/**
 * Interface for noise generation implementations
 */
interface NoiseGenerator {
    fun perlin(seed: Int, x: Double, y: Double, z: Double): Double
    fun perlinLinear(seed: Int, x: Double, y: Double, z: Double): Double
    fun fbm(seed: Int, x: Double, y: Double, z: Double, octaves: Int, lacunarity: Double, gain: Double, useLinear: Boolean = false): Double
}