package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * KANDINSKY COMPOSITION — Inspired by Wassily Kandinsky (1866–1944)
 *
 * Story:
 * Kandinsky heard colors. He believed that art should vibrate like music — that a
 * yellow triangle hums at a different frequency than a blue circle. His compositions
 * are visual symphonies: circles float like sustained notes, diagonal lines cut like
 * staccato bursts, and arcs sweep like legato phrases.
 *
 * This shader orchestrates geometric voices across the diamond display. Luminous
 * circles of primary color drift and overlap, their intersections creating unexpected
 * harmonies — where red meets blue, a violet chord rings out. Diagonal bands of
 * yellow energy slice through the composition. The forms move with musical timing:
 * slow swells, sudden accents, polyrhythmic pulses.
 *
 * Through the diffusion panel, hard edges soften into glowing auras — each geometric
 * form becomes a bell tone resonating in light. Kandinsky would have loved LEDs.
 */
class KandinskyCompositionShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val nx = x / DISPLAY_WIDTH
        val ny = y / DISPLAY_HEIGHT
        val slowT = t * TIME_SCALE

        // Floating circles — primary colors drifting with musical timing
        // Circle 1: Blue — deep, sustained bass note
        val c1x = 0.35 + 0.15 * sin(slowT * 0.3)
        val c1y = 0.45 + 0.12 * cos(slowT * 0.25)
        val d1 = sqrt((nx - c1x).pow(2) + (ny - c1y).pow(2))
        val circle1 = circleGlow(d1, 0.18, 0.06)

        // Circle 2: Red — warm, pulsing heartbeat
        val c2x = 0.65 + 0.12 * cos(slowT * 0.35 + 1.0)
        val c2y = 0.55 + 0.1 * sin(slowT * 0.28 + 0.5)
        val d2 = sqrt((nx - c2x).pow(2) + (ny - c2y).pow(2))
        val circle2 = circleGlow(d2, 0.14, 0.05)

        // Circle 3: Yellow — bright, staccato accent
        val c3x = 0.5 + 0.18 * sin(slowT * 0.4 + 2.0)
        val c3y = 0.35 + 0.15 * cos(slowT * 0.32 + 1.5)
        val d3 = sqrt((nx - c3x).pow(2) + (ny - c3y).pow(2))
        val circle3 = circleGlow(d3, 0.12, 0.04)

        // Diagonal energy band — sweeping across the composition
        val angle = slowT * 0.15
        val diag = abs(cos(angle) * (nx - 0.5) + sin(angle) * (ny - 0.5))
        val band = (1.0 - (diag / 0.08).coerceIn(0.0, 1.0)).pow(2) * 0.3

        // Arc — sweeping curve element
        val arcCenter = 0.5 + 0.1 * sin(slowT * 0.2)
        val arcDist = abs(sqrt((nx - arcCenter).pow(2) + (ny - 0.5).pow(2)) - 0.3)
        val arc = (1.0 - (arcDist / 0.04).coerceIn(0.0, 1.0)).pow(2) * 0.2

        // Compose colors — each element has its signature color
        var r = 0.02  // Dark background
        var g = 0.02
        var b = 0.05  // Slightly blue-black, like Kandinsky's backgrounds

        // Blue circle
        r += circle1 * 0.1
        g += circle1 * 0.15
        b += circle1 * 0.7

        // Red circle
        r += circle2 * 0.75
        g += circle2 * 0.08
        b += circle2 * 0.1

        // Yellow circle
        r += circle3 * 0.8
        g += circle3 * 0.75
        b += circle3 * 0.05

        // Diagonal band — warm orange energy
        r += band * 0.9
        g += band * 0.5
        b += band * 0.1

        // Arc — violet accent
        r += arc * 0.5
        g += arc * 0.1
        b += arc * 0.6

        // Subtle global pulse — the composition breathes
        val breath = 1.0 + 0.05 * sin(slowT * 0.5)
        r *= breath
        g *= breath
        b *= breath

        return ColorValue(
            r = r.coerceIn(0.0, 1.0),
            g = g.coerceIn(0.0, 1.0),
            b = b.coerceIn(0.0, 1.0)
        )
    }

    private fun circleGlow(dist: Double, radius: Double, softness: Double): Double {
        if (dist > radius + softness) return 0.0
        if (dist < radius) return 1.0
        val t = (dist - radius) / softness
        return 1.0 - t * t * (3.0 - 2.0 * t)
    }

    companion object {
        private const val DISPLAY_HEIGHT = 42.0
        private const val DISPLAY_WIDTH = 62.0
        private const val TIME_SCALE = 0.07
    }
}
