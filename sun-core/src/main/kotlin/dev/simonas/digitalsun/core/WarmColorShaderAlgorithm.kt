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
        val valueOffset = 100.0 // Offset to get different but nearby noise
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

        // Map hue noise (-1 to 1) to warm color range (yellow 60° to magenta 300°)
        // Yellow = 60°/360° = 0.166667
        // Red = 0°/360° = 0.0 (or 1.0, wraps around)
        // Magenta = 300°/360° = 0.833333
        // Range: 0.833333 (magenta) -> 0.0/1.0 (red) -> 0.166667 (yellow)

        // Convert noise from [-1, 1] to [0, 1]
        val hueNorm = (hueNoise + 1.0) / 2.0

        // Map to warm color range: magenta (0.833) through red (0.0/1.0) to yellow (0.166)
        // This creates a range that wraps around the hue circle in the warm zone
        val hue = if (hueNorm < 0.5) {
            // First half: magenta -> red
            0.733333 + hueNorm * 0.433334 // 0.833 -> 1.0
        } else {
            // Second half: red -> yellow
            (hueNorm - 0.5) * 0.433334 // 0.0 -> 0.166
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
