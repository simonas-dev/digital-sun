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
 * ROTHKO FIELD — Inspired by Mark Rothko (1903–1970)
 *
 * Story:
 * Rothko believed paintings should evoke basic human emotions — tragedy, ecstasy, doom.
 * His mature works are deceptively simple: stacked rectangles of color hovering on a canvas,
 * their edges dissolving into mist. Stand close enough and the colors consume you — they
 * breathe, they pulse, they swallow the room.
 *
 * This shader recreates that experience on the diamond display. Two or three horizontal
 * zones of deep, saturated color drift slowly in hue and brightness, their boundaries
 * feathered and alive. The colors shift on geological timescales — a deep cadmium red
 * bleeds into a bruised purple, which yields to a smoldering orange. The edges between
 * zones ripple gently, as if the light itself is breathing.
 *
 * Like standing in front of "No. 61 (Rust and Blue)" in a dim gallery, the display
 * becomes a portal — not to an image, but to a feeling.
 */
class RothkoFieldShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        // Normalize y to [0, 1] range across the display
        val ny = y / DISPLAY_HEIGHT
        // Normalize x for subtle horizontal variation
        val nx = x / DISPLAY_WIDTH

        // Slow time — Rothko's paintings demand patience
        val slowT = t * TIME_SCALE

        // Three color zones stacked vertically, boundaries shifting with time
        // Zone boundaries drift slowly
        val boundary1 = ZONE1_CENTER + 0.04 * sin(slowT * 0.7)
        val boundary2 = ZONE2_CENTER + 0.04 * sin(slowT * 0.5 + 1.2)

        // Soft feathered edges between zones — the Rothko signature
        // Add subtle horizontal ripple to the boundaries
        val ripple1 = 0.02 * sin(nx * 3.0 + slowT * 0.3)
        val ripple2 = 0.02 * sin(nx * 2.5 + slowT * 0.4 + 0.8)

        val zone1Blend = smoothEdge(ny, boundary1 + ripple1, EDGE_SOFTNESS)
        val zone2Blend = smoothEdge(ny, boundary2 + ripple2, EDGE_SOFTNESS)

        // Each zone has its own slowly evolving color
        // Zone A (top): deep reds and magentas
        val hueA = 0.0 + 0.03 * sin(slowT * 0.2)
        val satA = 0.85
        val valA = 0.6 + 0.15 * sin(slowT * 0.15)

        // Zone B (middle): burnt oranges and dark yellows
        val hueB = 0.07 + 0.04 * sin(slowT * 0.17 + 1.0)
        val satB = 0.9
        val valB = 0.5 + 0.2 * sin(slowT * 0.12 + 0.5)

        // Zone C (bottom): deep purples and maroons
        val hueC = 0.85 + 0.05 * sin(slowT * 0.13 + 2.0)
        val satC = 0.8
        val valC = 0.4 + 0.15 * sin(slowT * 0.1 + 1.5)

        // Blend zones: A -> B -> C from top to bottom
        val hue = lerp(lerp(hueA, hueB, zone1Blend), hueC, zone2Blend)
        val sat = lerp(lerp(satA, satB, zone1Blend), satC, zone2Blend)
        val baseVal = lerp(lerp(valA, valB, zone1Blend), valC, zone2Blend)

        // Subtle luminous glow — Rothko's colors seem to emit light from within
        val glow = 0.05 * sin(nx * 2.0 + ny * 3.0 + slowT * 0.25)
        val value = (baseVal + glow).coerceIn(0.0, 1.0)

        // Convert HSV to RGB
        return hsvToRgb(hue, sat, value)
    }

    private fun smoothEdge(pos: Double, edge: Double, softness: Double): Double {
        val t = ((pos - edge) / softness + 0.5).coerceIn(0.0, 1.0)
        // Smoothstep for organic-feeling transitions
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
        return ColorValue(
            r = r.coerceIn(0.0, 1.0),
            g = g.coerceIn(0.0, 1.0),
            b = b.coerceIn(0.0, 1.0),
            a = 1.0
        )
    }

    companion object {
        private const val DISPLAY_HEIGHT = 42.0
        private const val DISPLAY_WIDTH = 62.0
        private const val TIME_SCALE = 0.08
        private const val ZONE1_CENTER = 0.35
        private const val ZONE2_CENTER = 0.65
        private const val EDGE_SOFTNESS = 0.2
    }
}
