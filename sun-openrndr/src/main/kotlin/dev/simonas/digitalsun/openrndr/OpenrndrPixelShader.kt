package dev.simonas.digitalsun.openrndr

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.openrndr.Parameters
import org.openrndr.color.ColorRGBa

/**
 * OPENRNDR-specific pixel shader that wraps a core PixelShader
 */
class OpenrndrPixelShader(private val coreShader: PixelShader) {

    fun shade(x: Int, y: Int, t: Double, params: Parameters): ColorRGBa {
        val coreParams = params.toShaderParameters()
        val colorValue = coreShader.shade(x, y, t, coreParams)
        return colorValue.toOpenrndrColor()
    }

    private fun ColorValue.toOpenrndrColor(): ColorRGBa {
        return ColorRGBa(r, g, b, a)
    }
}