package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * KUSAMA INFINITY — Inspired by Yayoi Kusama's Infinity Mirror Rooms (1965–)
 *
 * Story:
 * When Kusama was ten, she began seeing hallucinations — fields of dots that
 * covered everything, dissolving the boundaries between herself and the world.
 * She called it "self-obliteration." Decades later, she built rooms to share
 * that vision: mirrored chambers filled with hundreds of colored lights, reflected
 * infinitely in every direction. You step inside and the floor, walls, and ceiling
 * vanish. You float among countless points of light stretching to infinity.
 *
 * "Infinity Mirrored Room — The Souls of Millions of Light Years Away" is perhaps
 * her most famous: a dark void filled with slowly color-cycling LED lights,
 * each one reflected into thousands of copies. The effect is cosmic — you stand
 * among stars, or neurons, or souls.
 *
 * This shader scatters points of warm, colored light across the dark display.
 * Each point pulses at its own rhythm, shifting through warm hues — amber, rose,
 * violet, gold. They drift very slowly, like lanterns on still water. The darkness
 * between them is vast and deep. Through the diffusion panel, each point becomes
 * a soft orb floating in the void — an infinity room in miniature.
 */
class KusamaInfinityShaderAlgorithm : PixelShader {

    // Pre-computed light positions (pseudo-random scatter)
    private val lights = listOf(
        Light(0.15, 0.25, 0.0, 0.23),
        Light(0.4, 0.15, 0.7, 0.18),
        Light(0.7, 0.3, 1.4, 0.20),
        Light(0.85, 0.55, 2.1, 0.16),
        Light(0.55, 0.7, 2.8, 0.22),
        Light(0.25, 0.6, 3.5, 0.17),
        Light(0.5, 0.45, 4.2, 0.19),
        Light(0.8, 0.8, 4.9, 0.15),
        Light(0.3, 0.85, 5.6, 0.21),
        Light(0.65, 0.5, 6.3, 0.14),
        Light(0.1, 0.5, 7.0, 0.16),
        Light(0.9, 0.15, 7.7, 0.18),
    )

    data class Light(
        val baseX: Double,
        val baseY: Double,
        val phaseOffset: Double,
        val hueSpeed: Double
    )

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val nx = x / DISPLAY_WIDTH
        val ny = y / DISPLAY_HEIGHT
        val slowT = t * TIME_SCALE

        var totalR = 0.0
        var totalG = 0.0
        var totalB = 0.0

        for (light in lights) {
            // Each light drifts slowly
            val lx = light.baseX + 0.05 * sin(slowT * 0.3 + light.phaseOffset)
            val ly = light.baseY + 0.05 * cos(slowT * 0.25 + light.phaseOffset * 0.7)

            val dx = nx - lx
            val dy = ny - ly
            val dist = sqrt(dx * dx + dy * dy)

            // Soft radial glow
            val glow = 1.0 / (1.0 + (dist * dist) * GLOW_FALLOFF)

            // Each light pulses independently
            val pulse = 0.6 + 0.4 * sin(slowT * light.hueSpeed * 3.0 + light.phaseOffset)
            val intensity = glow * pulse

            // Color cycling — each light on its own phase through warm hues
            val huePhase = (slowT * light.hueSpeed + light.phaseOffset) % 1.0
            val hue = warmHue(huePhase)
            val sat = 0.7
            val value = intensity.coerceIn(0.0, 1.0)

            val color = hsvToRgb(hue, sat, value)
            totalR += color.r
            totalG += color.g
            totalB += color.b
        }

        return ColorValue(
            r = totalR.coerceIn(0.0, 1.0),
            g = totalG.coerceIn(0.0, 1.0),
            b = totalB.coerceIn(0.0, 1.0)
        )
    }

    private fun warmHue(phase: Double): Double {
        // Cycle through: amber -> rose -> violet -> gold -> amber
        return when {
            phase < 0.25 -> lerp(0.08, 0.95, phase / 0.25)   // amber -> rose
            phase < 0.5 -> lerp(0.95, 0.8, (phase - 0.25) / 0.25)  // rose -> violet
            phase < 0.75 -> lerp(0.8, 0.12, (phase - 0.5) / 0.25)  // violet -> gold
            else -> lerp(0.12, 0.08, (phase - 0.75) / 0.25)         // gold -> amber
        }
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
        private const val DISPLAY_WIDTH = 62.0
        private const val DISPLAY_HEIGHT = 42.0
        private const val TIME_SCALE = 0.07
        private const val GLOW_FALLOFF = 200.0
    }
}
