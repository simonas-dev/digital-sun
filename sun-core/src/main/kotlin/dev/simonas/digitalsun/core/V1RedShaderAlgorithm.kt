package dev.simonas.digitalsun.core

import kotlin.math.pow

/**
 * First version of the red pixel shader algorithm - pure logic without OPENRNDR dependencies
 */
class V1RedShaderAlgorithm(private val noiseGenerator: NoiseGenerator) : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        // Calculate noise based on selected type
        val noise = when (params.noiseType) {
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

        // Calculate alpha based on selected mapping
        val alpha = ((1.0 + noise) / 2.0)
            .pow(params.alphaPower)
            .coerceIn(params.alphaMin, params.alphaMax)

        return ColorValue.RED.copy(a = alpha)
    }
}
