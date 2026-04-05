package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Startup shader based on Danilo Guanabara's "Danguafer/Silexars" effect.
 * http://www.pouet.net/prod.php?which=57245
 *
 * A purely mathematical light/plasma effect — no noise generator required.
 * Each RGB channel is computed with a slight time offset, creating colorful
 * interference patterns that radiate from the center.
 */
class StartupShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        // Stage coordinates are normalized (centered at origin), so we just
        // need a virtual resolution to control the zoom/aspect of the effect.
        val resX = 62.0
        val resY = 42.0

        val c = DoubleArray(3)
        var z = t
        var lastL = 0.0

        for (i in 0 until 3) {
            // Normalize to ~[-0.5, 0.5] centered at origin
            var uvX = x / resX
            var uvY = y / resY

            // Aspect-correct
            var px = uvX
            var py = uvY
            px *= resX / resY

            z += 0.05
            val l = sqrt(px * px + py * py)
            lastL = l

            // Distort UV by radial wave
            val distortion = if (l != 0.0) (sin(z) + 1.0) * abs(sin(l * 2.0 - z - z)) / l else 0.0
            uvX += px * distortion
            uvY += py * distortion

            // Brightness from distance to nearest grid intersection
            val mx = ((uvX % 1.0) + 1.0) % 1.0 - 0.5
            val my = ((uvY % 1.0) + 1.0) % 1.0 - 0.5
            val modLen = sqrt(mx * mx + my * my)

            c[i] = if (modLen != 0.0) 0.05 / modLen else 1.0
        }

        // Divide by distance from center (matches original c/l)
        val divisor = if (lastL != 0.0) lastL else 1.0

        return ColorValue(
            r = (c[0] / divisor).coerceIn(0.0, 1.0),
            g = (c[1] / divisor).coerceIn(0.0, 1.0),
            b = (c[2] / divisor).coerceIn(0.0, 1.0),
            a = 1.0
        )
    }
}
