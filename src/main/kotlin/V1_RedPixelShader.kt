import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.fbm
import org.openrndr.extra.noise.perlin
import org.openrndr.extra.noise.perlinLinear
import kotlin.math.pow

class V1_RedPixelShader : PixelShader<Parameters> {

    override fun shade(x: Int, y: Int, t: Double, params: Parameters): ColorRGBa {

        // Calculate noise based on selected type
        val noise = when (params.noiseType) {
            Parameters.NoiseType.PERLIN -> {
                perlin(
                    params.seed,
                    x = x.toDouble() * params.spatialScale,
                    y = y * params.spatialScale,
                    z = t * params.timeScale
                )
            }
            Parameters.NoiseType.FBM_PERLIN -> {
                fbm(
                    params.seed,
                    x = x.toDouble() * params.spatialScale,
                    y = y * params.spatialScale,
                    z = t * params.timeScale,
                    noise = ::perlin,
                    params.fbmOctaves,
                    params.fbmLacunarity,
                    params.fbmGain
                )
            }
            Parameters.NoiseType.FBM_PERLIN_LINEAR -> {
                fbm(
                    params.seed,
                    x = x.toDouble() * params.spatialScale,
                    y = y * params.spatialScale,
                    z = t * params.timeScale,
                    noise = ::perlinLinear,
                    params.fbmOctaves,
                    params.fbmLacunarity,
                    params.fbmGain
                )
            }
        }

        // Calculate alpha based on selected mapping
        val alpha = ((1.0 + noise) / 2.0)
            .pow(params.alphaPower)
            .coerceIn(params.alphaMin, params.alphaMax)

        return ColorRGBa.RED.copy(alpha = alpha)
    }
}