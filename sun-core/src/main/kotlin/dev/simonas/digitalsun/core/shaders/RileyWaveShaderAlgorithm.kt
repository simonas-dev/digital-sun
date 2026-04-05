package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * RILEY'S CURRENT — Inspired by Bridget Riley (1931–)
 *
 * Story:
 * Bridget Riley's paintings vibrate. Parallel stripes bend and buckle across the canvas,
 * creating the illusion of movement, depth, and vertigo on a flat surface. Her 1960s
 * black-and-white works like "Current" and "Fall" seem to ripple and breathe — viewers
 * report dizziness, nausea, and a sense that the painting is alive.
 *
 * When she introduced color in the late 1960s, the effect became even more hypnotic.
 * Thin stripes of complementary colors interact at the boundary of perception, creating
 * phantom hues that exist only in the viewer's eye.
 *
 * This shader generates undulating vertical stripes that shift and interfere across the
 * display. Multiple sine waves modulate the stripe positions, creating a living moiré
 * pattern. The stripes alternate between cool and warm colors — teal and coral, navy
 * and cream — their boundaries shimmering with optical energy. The waves move at
 * different speeds, so the pattern never quite repeats.
 */
class RileyWaveShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val slowT = t * TIME_SCALE

        // Normalize coordinates
        val nx = x / DISPLAY_WIDTH
        val ny = y / DISPLAY_HEIGHT

        // Multiple wave distortions applied to x-position
        // Each wave has different frequency, amplitude, and speed
        val wave1 = WAVE1_AMP * sin(ny * WAVE1_FREQ * PI + slowT * 1.0)
        val wave2 = WAVE2_AMP * sin(ny * WAVE2_FREQ * PI + slowT * 0.7 + nx * 2.0)
        val wave3 = WAVE3_AMP * sin(ny * WAVE3_FREQ * PI + slowT * 0.4 + 1.5)

        val distortedX = nx + wave1 + wave2 + wave3

        // Create stripe pattern from distorted x
        val stripePhase = distortedX * STRIPE_COUNT
        val stripe = sin(stripePhase * PI)

        // Second set of stripes at different angle for moiré interference
        val angle = 0.3 + 0.1 * sin(slowT * 0.2)
        val rotX = nx * cos(angle) - ny * sin(angle)
        val moireWave = WAVE1_AMP * 0.5 * sin(rotX * DISPLAY_WIDTH * 0.15 + slowT * 0.5)
        val moireStripe = sin((rotX + moireWave) * STRIPE_COUNT * 0.8 * PI)

        // Combine stripes for interference pattern
        val combined = (stripe + moireStripe * 0.5) / 1.5

        // Map to two-color palette with smooth transition
        // Riley often used complementary pairs
        val blend = (combined + 1.0) / 2.0 // normalize to [0, 1]

        // Color pair shifts slowly over time
        val pairPhase = ((slowT * 0.05) % 1.0)
        val pairIndex = (pairPhase * COLOR_PAIRS.size).toInt().coerceIn(0, COLOR_PAIRS.size - 1)
        val pair = COLOR_PAIRS[pairIndex]

        val r = lerp(pair.first.r, pair.second.r, blend)
        val g = lerp(pair.first.g, pair.second.g, blend)
        val b = lerp(pair.first.b, pair.second.b, blend)

        return ColorValue(
            r = r.coerceIn(0.0, 1.0),
            g = g.coerceIn(0.0, 1.0),
            b = b.coerceIn(0.0, 1.0),
            a = 1.0
        )
    }

    private fun lerp(a: Double, b: Double, t: Double): Double = a + (b - a) * t

    companion object {
        private const val DISPLAY_HEIGHT = 42.0
        private const val DISPLAY_WIDTH = 62.0
        private const val TIME_SCALE = 0.2
        private const val STRIPE_COUNT = 12.0
        private const val WAVE1_AMP = 0.06
        private const val WAVE1_FREQ = 4.0
        private const val WAVE2_AMP = 0.03
        private const val WAVE2_FREQ = 7.0
        private const val WAVE3_AMP = 0.015
        private const val WAVE3_FREQ = 11.0

        // Riley's complementary color pairs
        private val COLOR_PAIRS = listOf(
            Pair(ColorValue(0.10, 0.55, 0.60), ColorValue(0.95, 0.55, 0.40)), // teal & coral
            Pair(ColorValue(0.10, 0.12, 0.45), ColorValue(0.95, 0.92, 0.80)), // navy & cream
            Pair(ColorValue(0.55, 0.15, 0.50), ColorValue(0.40, 0.80, 0.35)), // plum & green
            Pair(ColorValue(0.90, 0.25, 0.20), ColorValue(0.20, 0.25, 0.85)), // red & blue
        )
    }
}
