package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * LEWITT WALL — Inspired by Sol LeWitt (1928–2007)
 *
 * Story:
 * Sol LeWitt didn't paint his wall drawings. He wrote instructions, and others
 * executed them. "Wall Drawing #1136": "A wall divided into four parts. Each part
 * with a different color ink wash." The genius was in the system, not the hand.
 * He proved that an idea could be art — that the instruction was the artwork, and
 * every execution was equally valid.
 *
 * His later wall drawings exploded with color: bold bands of saturated ink wash —
 * red, yellow, blue, orange — layered at different angles, creating optical
 * vibrations where they crossed. The walls of museums became luminous quilts of
 * pure algorithmic color.
 *
 * This shader is itself a Sol LeWitt instruction: "Bands of color at four angles
 * (0°, 45°, 90°, 135°), each rotating slowly at a different speed. Where bands
 * overlap, their colors mix additively." The result is a continuously evolving
 * geometric composition — systematic, bold, and endlessly surprising. LeWitt
 * would approve: the algorithm is the art.
 */
class LeWittWallShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val nx = x / DISPLAY_WIDTH
        val ny = y / DISPLAY_HEIGHT
        val slowT = t * TIME_SCALE

        // Four sets of colored bands at different angles, each rotating slowly
        // Band set 1: Red — horizontal bands, slowly rotating
        val angle1 = slowT * 0.2
        val proj1 = cos(angle1) * nx + sin(angle1) * ny
        val band1 = bandPattern(proj1, BAND_WIDTH)

        // Band set 2: Blue — diagonal bands, rotating opposite
        val angle2 = 0.785 + slowT * 0.15  // Start at 45°
        val proj2 = cos(angle2) * nx + sin(angle2) * ny
        val band2 = bandPattern(proj2, BAND_WIDTH)

        // Band set 3: Yellow — vertical bands
        val angle3 = 1.571 + slowT * 0.25  // Start at 90°
        val proj3 = cos(angle3) * nx + sin(angle3) * ny
        val band3 = bandPattern(proj3, BAND_WIDTH)

        // Band set 4: Orange — opposite diagonal
        val angle4 = 2.356 + slowT * 0.18  // Start at 135°
        val proj4 = cos(angle4) * nx + sin(angle4) * ny
        val band4 = bandPattern(proj4, BAND_WIDTH * 1.2)

        // Additive color mixing — where bands overlap, colors combine
        // LeWitt's ink washes are translucent; overlaps create new colors
        var r = 0.03  // Near-black background
        var g = 0.03
        var b = 0.03

        // Red bands
        r += band1 * 0.6
        g += band1 * 0.05
        b += band1 * 0.05

        // Blue bands
        r += band2 * 0.05
        g += band2 * 0.1
        b += band2 * 0.6

        // Yellow bands
        r += band3 * 0.55
        g += band3 * 0.55
        b += band3 * 0.05

        // Orange bands
        r += band4 * 0.55
        g += band4 * 0.3
        b += band4 * 0.05

        return ColorValue(
            r = r.coerceIn(0.0, 1.0),
            g = g.coerceIn(0.0, 1.0),
            b = b.coerceIn(0.0, 1.0)
        )
    }

    private fun bandPattern(projection: Double, width: Double): Double {
        // Creates soft-edged bands with smooth transitions
        val pos = ((projection % width) + width) % width
        val center = width / 2.0
        val dist = abs(pos - center) / center
        // Smoothstep band shape — solid center, soft edges
        return if (dist < 0.6) 1.0
        else if (dist > 1.0) 0.0
        else {
            val t = (dist - 0.6) / 0.4
            1.0 - t * t * (3.0 - 2.0 * t)
        }
    }

    companion object {
        private const val DISPLAY_HEIGHT = 42.0
        private const val DISPLAY_WIDTH = 62.0
        private const val TIME_SCALE = 0.05
        private const val BAND_WIDTH = 0.18
    }
}
