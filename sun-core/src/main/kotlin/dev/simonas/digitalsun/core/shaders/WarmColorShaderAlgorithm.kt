package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.HSVColor
import dev.simonas.digitalsun.core.HueRange
import dev.simonas.digitalsun.core.NamedShader
import dev.simonas.digitalsun.core.NoiseGenerator
import dev.simonas.digitalsun.core.NoiseType
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.pow

/**
 * Warm color shader using Perlin noise to generate colors ranging from yellow through red to magenta.
 * Uses Perlin noise to control both hue (color) and value (brightness) in nearby dimensions.
 */
class WarmColorShaderAlgorithm(
    private val noiseGenerator: NoiseGenerator,
    private val getParams: () -> ShaderParameters =  { ShaderParameters() },
) : PixelShader {

    override fun shade(x: Int, y: Int, t: Double): ColorValue {
        val params = getParams()
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

    companion object {
        fun all(noiseGenerator: NoiseGenerator): List<NamedShader> {
            val variations = listOf(
                // Default warm
                "warm" to ShaderParameters(),
                // Inferno — full range, aggressive FBM ★ FAVORITE
                "inferno" to ShaderParameters(
                    seed = 234, spatialScale = 0.065, timeScale = 0.18,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 2.0,
                    fbmOctaves = 8, fbmLacunarity = 2.5, fbmGain = 0.35,
                    hueRange = HueRange(min = 330.0 / 360.0, max = 60.0 / 360.0),
                ),
                // Hellscape — extreme contrast, dark base ★ FAVORITE
                "hellscape" to ShaderParameters(
                    seed = 890, spatialScale = 0.075, timeScale = 0.22,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 5.0,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.3, fbmGain = 0.38,
                    hueRange = HueRange(min = 355.0 / 360.0, max = 20.0 / 360.0),
                ),
                // Plasma — wide range, fast, bright linear FBM ★ FAVORITE
                "plasma" to ShaderParameters(
                    seed = 258, spatialScale = 0.13, timeScale = 0.55,
                    noiseType = NoiseType.FBM_PERLIN_LINEAR, alphaPower = 1.1,
                    alphaMin = 0.1, alphaMax = 1.0,
                    fbmOctaves = 7, fbmLacunarity = 1.9, fbmGain = 0.5,
                    hueRange = HueRange(min = 310.0 / 360.0, max = 55.0 / 360.0),
                ),
                // Luminescence — high contrast, bright peaks against deep black
                "luminescence" to ShaderParameters(
                    seed = 421, spatialScale = 0.08, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 4.8,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                // Luminescence soft — gentle glow, mostly lit with soft falloff
                "lum-soft" to ShaderParameters(
                    seed = 421, spatialScale = 0.08, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 1.5,
                    alphaMin = 0.15, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                // Luminescence mild — moderate contrast, balanced bright/dark
                "lum-mild" to ShaderParameters(
                    seed = 421, spatialScale = 0.08, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 2.5,
                    alphaMin = 0.05, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                // Luminescence medium — noticeable dark regions, clear peaks
                "lum-medium" to ShaderParameters(
                    seed = 421, spatialScale = 0.08, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 3.5,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                // Luminescence harsh — mostly dark, sharp bright streaks
                "lum-harsh" to ShaderParameters(
                    seed = 421, spatialScale = 0.08, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 6.5,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                // Luminescence extreme — near-black with rare bright sparks
                "lum-extreme" to ShaderParameters(
                    seed = 421, spatialScale = 0.08, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 9.0,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                // Alpha power 8, spatial scale variations
                "lum8-tight" to ShaderParameters(
                    seed = 421, spatialScale = 0.02, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 8.0,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                "lum8-wide" to ShaderParameters(
                    seed = 421, spatialScale = 0.04, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 8.0,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                "lum8-mid" to ShaderParameters(
                    seed = 421, spatialScale = 0.06, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 8.0,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                "lum8-default" to ShaderParameters(
                    seed = 421, spatialScale = 0.08, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 8.0,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                "lum8-fine" to ShaderParameters(
                    seed = 421, spatialScale = 0.12, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 8.0,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                "lum8-grain" to ShaderParameters(
                    seed = 421, spatialScale = 0.18, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 8.0,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                "lum8-pixel" to ShaderParameters(
                    seed = 421, spatialScale = 0.25, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 8.0,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
                "lum8-scatter" to ShaderParameters(
                    seed = 421, spatialScale = 0.35, timeScale = 0.12,
                    noiseType = NoiseType.FBM_PERLIN, alphaPower = 8.0,
                    alphaMin = 0.0, alphaMax = 1.0,
                    fbmOctaves = 8, fbmLacunarity = 2.4, fbmGain = 0.36,
                    hueRange = HueRange(min = 345.0 / 360.0, max = 50.0 / 360.0),
                ),
            )

            return variations.map { (name, p) ->
                NamedShader(
                    name = name,
                    shader = WarmColorShaderAlgorithm(
                        noiseGenerator = noiseGenerator,
                        getParams = { p }
                    ),
                )
            }
        }
    }
}
