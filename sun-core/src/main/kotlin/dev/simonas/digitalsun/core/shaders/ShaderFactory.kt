package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.HueRange
import dev.simonas.digitalsun.core.NoiseGenerator
import dev.simonas.digitalsun.core.NoiseType
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters

data class NamedShader(
    val name: String,
    val shader: PixelShader,
)

object ShaderFactory {
    fun all(noiseGenerator: NoiseGenerator, params: ShaderParameters = ShaderParameters()): List<NamedShader> {
        val variations = listOf(
            // Default warm
            "warm" to params,
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
            NamedShader(name, WarmColorShaderAlgorithm(noiseGenerator, p))
        }
    }
}
