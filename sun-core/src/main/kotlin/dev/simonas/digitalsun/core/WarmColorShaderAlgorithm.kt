package dev.simonas.digitalsun.core

import kotlin.math.pow

/**
 * Warm color shader using Perlin noise to generate colors ranging from yellow through red to magenta.
 * Uses Perlin noise to control both hue (color) and value (brightness) in nearby dimensions.
 */
class WarmColorShaderAlgorithm(private val noiseGenerator: NoiseGenerator) : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        // Use Perlin noise for hue - sample in xyz space
        val hueNoise = when (params.noiseType) {
            NoiseType.PERLIN -> {
                noiseGenerator.perlin(
                    params.seed,
                    x = x.toDouble() * params.spatialScale,
                    y = y * params.spatialScale,
                    z = t * params.timeScale
                )
            }
            NoiseType.FBM_PERLIN -> {
                noiseGenerator.fbm(
                    params.seed,
                    x = x.toDouble() * params.spatialScale,
                    y = y * params.spatialScale,
                    z = t * params.timeScale,
                    octaves = params.fbmOctaves,
                    lacunarity = params.fbmLacunarity,
                    gain = params.fbmGain,
                    useLinear = false
                )
            }
            NoiseType.FBM_PERLIN_LINEAR -> {
                noiseGenerator.fbm(
                    params.seed,
                    x = x.toDouble() * params.spatialScale,
                    y = y * params.spatialScale,
                    z = t * params.timeScale,
                    octaves = params.fbmOctaves,
                    lacunarity = params.fbmLacunarity,
                    gain = params.fbmGain,
                    useLinear = true
                )
            }
        }

        // Use Perlin noise for value - sample in nearby dimension (offset in w-axis conceptually)
        // We offset the coordinates slightly to get correlated but not identical noise
        val valueOffset = 0.0 // Offset to get different but nearby noise
        val valueNoise = when (params.noiseType) {
            NoiseType.PERLIN -> {
                noiseGenerator.perlin(
                    params.seed,
                    x = (x.toDouble() + valueOffset) * params.spatialScale,
                    y = (y + valueOffset) * params.spatialScale,
                    z = (t + valueOffset) * params.timeScale
                )
            }
            NoiseType.FBM_PERLIN -> {
                noiseGenerator.fbm(
                    params.seed,
                    x = (x.toDouble() + valueOffset) * params.spatialScale,
                    y = (y + valueOffset) * params.spatialScale,
                    z = (t + valueOffset) * params.timeScale,
                    octaves = params.fbmOctaves,
                    lacunarity = params.fbmLacunarity,
                    gain = params.fbmGain,
                    useLinear = false
                )
            }
            NoiseType.FBM_PERLIN_LINEAR -> {
                noiseGenerator.fbm(
                    params.seed,
                    x = (x.toDouble() + valueOffset) * params.spatialScale,
                    y = (y + valueOffset) * params.spatialScale,
                    z = (t + valueOffset) * params.timeScale,
                    octaves = params.fbmOctaves,
                    lacunarity = params.fbmLacunarity,
                    gain = params.fbmGain,
                    useLinear = true
                )
            }
        }

        // Map hue noise (-1 to 1) to the configured hue range
        // Convert noise from [-1, 1] to [0, 1]
        val hueNorm = (hueNoise + 1.0) / 2.0

        // Map to the configured hue range
        // When min > max, the range wraps around the hue circle (e.g., magenta -> red -> yellow)
        val hue = if (params.hueRange.min > params.hueRange.max) {
            // Wrapping range (e.g., 0.833 -> 1.0 -> 0.0 -> 0.166)
            val totalRange = (1.0 - params.hueRange.min) + params.hueRange.max
            if (hueNorm < 0.5) {
                // First half: min -> 1.0
                params.hueRange.min + hueNorm * 2.0 * (1.0 - params.hueRange.min)
            } else {
                // Second half: 0.0 -> max
                (hueNorm - 0.5) * 2.0 * params.hueRange.max
            }
        } else {
            // Non-wrapping range: simple linear interpolation
            params.hueRange.min + hueNorm * (params.hueRange.max - params.hueRange.min)
        }

        // Map value noise (-1 to 1) to brightness (0.0 to 1.0) with power curve
        val value = ((valueNoise + 1.0) / 2.0)
            .pow(params.alphaPower)
            .coerceIn(params.alphaMin, params.alphaMax)

        // Create HSV color with full saturation for vivid colors
        val hsvColor = HSVColor(
            h = hue,
            s = 1.0, // Full saturation for vivid warm colors
            v = value
        )

        return hsvColor.toRGB()
    }
}
