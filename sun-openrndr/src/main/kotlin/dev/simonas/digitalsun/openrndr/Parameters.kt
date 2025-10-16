package dev.simonas.digitalsun.openrndr

import dev.simonas.digitalsun.core.NoiseType
import dev.simonas.digitalsun.core.ShaderParameters
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.IntParameter
import org.openrndr.extra.parameters.OptionParameter

/**
 * OPENRNDR GUI-bound parameters that wrap core ShaderParameters
 */
class Parameters {

    @IntParameter("Seed", 1, 1000)
    var seed: Int = ShaderParameters().seed

    @DoubleParameter("Spatial Scale", 0.01, 2.0)
    var spatialScale: Double = ShaderParameters().spatialScale

    @DoubleParameter("Time Scale", 0.0, 2.0)
    var timeScale: Double = ShaderParameters().timeScale

    @OptionParameter("Noise Type", order = 0)
    var noiseType: NoiseType = ShaderParameters().noiseType

    @DoubleParameter("Alpha Power", 0.1, 5.0)
    var alphaPower: Double = ShaderParameters().alphaPower

    @DoubleParameter("Alpha Min", 0.0, 1.0)
    var alphaMin: Double = ShaderParameters().alphaMin

    @DoubleParameter("Alpha Max", 0.0, 1.0)
    var alphaMax: Double = ShaderParameters().alphaMax

    @IntParameter("FBM Octaves", 1, 8)
    var fbmOctaves: Int = ShaderParameters().fbmOctaves

    @DoubleParameter("FBM Lacunarity", 1.0, 4.0)
    var fbmLacunarity: Double = ShaderParameters().fbmLacunarity

    @DoubleParameter("FBM Gain", 0.1, 1.0)
    var fbmGain: Double = ShaderParameters().fbmGain

    /**
     * Convert to core ShaderParameters
     */
    fun toShaderParameters(): ShaderParameters {
        return ShaderParameters(
            seed = seed,
            spatialScale = spatialScale,
            timeScale = timeScale,
            noiseType = noiseType,
            alphaPower = alphaPower,
            alphaMin = alphaMin,
            alphaMax = alphaMax,
            fbmOctaves = fbmOctaves,
            fbmLacunarity = fbmLacunarity,
            fbmGain = fbmGain
        )
    }
}