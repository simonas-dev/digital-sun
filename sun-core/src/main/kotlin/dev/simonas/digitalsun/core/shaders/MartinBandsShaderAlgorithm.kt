package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * MARTIN BANDS — Inspired by Agnes Martin (1912–2004)
 *
 * Story:
 * Agnes Martin drove alone into the New Mexico desert and found silence. Not the
 * absence of sound, but the presence of stillness — the kind that lives between
 * breaths. She spent decades painting that silence: pale horizontal bands on
 * six-foot canvases, so subtle you might walk past and see nothing. But stand
 * still, quiet your mind, and the painting opens like a window onto infinity.
 *
 * Her bands are not about color. They are about the spaces between — the way a
 * faint blue wash meets a faint pink wash meets a faint gold wash, each so gentle
 * it might be imagined. The grid beneath is invisible but felt, like the structure
 * of a haiku.
 *
 * This shader breathes with that quietness. Pale horizontal bands of barely-there
 * color drift with glacial slowness. The palette is Martin's: ghost-pink, dawn-blue,
 * cream, palest lavender. Through the diffusion panel, the bands merge into a
 * luminous field of hushed light — not darkness, not brightness, but the tender
 * space between.
 */
class MartinBandsShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val ny = y / DISPLAY_HEIGHT
        val nx = x / DISPLAY_WIDTH
        val slowT = t * TIME_SCALE

        // Horizontal bands — the fundamental Martin element
        // Multiple overlapping band frequencies for subtle complexity
        val band1 = sin(ny * BAND_FREQUENCY + slowT * 0.3) * 0.5 + 0.5
        val band2 = sin(ny * BAND_FREQUENCY * 1.5 + slowT * 0.2 + 1.0) * 0.5 + 0.5
        val band3 = sin(ny * BAND_FREQUENCY * 0.7 + slowT * 0.15 + 2.0) * 0.5 + 0.5

        // Martin's palette: all colors are high-value, low-saturation
        // Band 1: ghost pink
        val h1 = 0.97
        val s1 = 0.08
        val v1 = 0.82 + 0.05 * band1

        // Band 2: dawn blue
        val h2 = 0.58
        val s2 = 0.07
        val v2 = 0.80 + 0.05 * band2

        // Band 3: palest lavender
        val h3 = 0.75
        val s3 = 0.06
        val v3 = 0.81 + 0.04 * band3

        // Blend bands based on vertical position — each dominates a region
        val zone1 = smoothBand(ny, 0.25 + 0.03 * sin(slowT * 0.1), 0.3)
        val zone2 = smoothBand(ny, 0.55 + 0.02 * sin(slowT * 0.12 + 0.5), 0.3)

        val hue = lerp(lerp(h1, h2, zone1), h3, zone2)
        val sat = lerp(lerp(s1, s2, zone1), s3, zone2)
        val baseVal = lerp(lerp(v1, v2, zone1), v3, zone2)

        // Very subtle horizontal variation — the ghost of a grid
        val gridHint = 0.01 * sin(nx * 20.0 + slowT * 0.05)

        // Overall gentle breathing
        val breath = 0.02 * sin(slowT * 0.4)

        val value = (baseVal + gridHint + breath).coerceIn(0.0, 1.0)

        return hsvToRgb(hue, sat, value)
    }

    private fun smoothBand(pos: Double, center: Double, width: Double): Double {
        val t = ((pos - center + width / 2) / width).coerceIn(0.0, 1.0)
        return t * t * (3.0 - 2.0 * t)
    }

    private fun lerp(a: Double, b: Double, t: Double): Double = a + (b - a) * t

    private fun hsvToRgb(h: Double, s: Double, v: Double): ColorValue {
        val hue = ((h % 1.0) + 1.0) % 1.0
        val hi = (hue * 6.0).toInt() % 6
        val f = hue * 6.0 - hi
        val p = v * (1.0 - s)
        val q = v * (1.0 - f * s)
        val t = v * (1.0 - (1.0 - f) * s)
        val (r, g, b) = when (hi) {
            0 -> Triple(v, t, p)
            1 -> Triple(q, v, p)
            2 -> Triple(p, v, t)
            3 -> Triple(p, q, v)
            4 -> Triple(t, p, v)
            else -> Triple(v, p, q)
        }
        return ColorValue(r.coerceIn(0.0, 1.0), g.coerceIn(0.0, 1.0), b.coerceIn(0.0, 1.0))
    }

    companion object {
        private const val DISPLAY_HEIGHT = 42.0
        private const val DISPLAY_WIDTH = 62.0
        private const val TIME_SCALE = 0.03  // Glacially slow
        private const val BAND_FREQUENCY = 12.0
    }
}
