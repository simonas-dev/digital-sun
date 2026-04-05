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
 * MONET LILIES — Inspired by Claude Monet (1840–1926)
 *
 * Story:
 * In the last decades of his life, Monet painted the same water garden at Giverny
 * over and over — not because he ran out of subjects, but because the subject was
 * never the lilies. It was the light. The way morning sun fractured across the pond's
 * surface, the way reflections of sky and willow merged with the dark water below,
 * the way a single moment held infinite color.
 *
 * This shader recreates that shimmering surface. Soft pools of green and blue drift
 * like lily pads, punctuated by warm rose and lavender blooms. The "water" beneath
 * ripples slowly, its surface catching fragments of sky-blue and gold. Through the
 * diffusion panel, the effect is pure impressionism — colors bleeding into each other
 * the way they do when you squint at a pond on a summer afternoon.
 *
 * Stand close and you see color. Step back and you see Giverny.
 */
class MonetLiliesShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val nx = x / DISPLAY_WIDTH
        val ny = y / DISPLAY_HEIGHT
        val slowT = t * TIME_SCALE

        // Water surface — layered sine waves for ripple effect
        val ripple1 = sin(nx * 8.0 + slowT * 0.7) * cos(ny * 6.0 + slowT * 0.5)
        val ripple2 = sin(nx * 5.0 - slowT * 0.4 + 1.3) * cos(ny * 7.0 + slowT * 0.3)
        val ripple3 = sin((nx + ny) * 4.0 + slowT * 0.6) * 0.5
        val waterSurface = (ripple1 + ripple2 + ripple3) / 2.5

        // Lily pad zones — circular soft blobs that drift
        val pad1x = 0.3 + 0.1 * sin(slowT * 0.2)
        val pad1y = 0.4 + 0.08 * cos(slowT * 0.15)
        val pad2x = 0.7 + 0.08 * cos(slowT * 0.25 + 1.0)
        val pad2y = 0.6 + 0.1 * sin(slowT * 0.18 + 0.7)
        val pad3x = 0.5 + 0.12 * sin(slowT * 0.13 + 2.0)
        val pad3y = 0.3 + 0.06 * cos(slowT * 0.22 + 1.5)

        val dist1 = sqrt((nx - pad1x).pow(2) + (ny - pad1y).pow(2))
        val dist2 = sqrt((nx - pad2x).pow(2) + (ny - pad2y).pow(2))
        val dist3 = sqrt((nx - pad3x).pow(2) + (ny - pad3y).pow(2))

        val padInfluence1 = smoothBlob(dist1, PAD_RADIUS)
        val padInfluence2 = smoothBlob(dist2, PAD_RADIUS * 0.8)
        val padInfluence3 = smoothBlob(dist3, PAD_RADIUS * 0.6)

        val totalPad = (padInfluence1 + padInfluence2 + padInfluence3).coerceIn(0.0, 1.0)

        // Flower blooms — smaller, warmer spots near lily pads
        val bloom1 = smoothBlob(dist1, BLOOM_RADIUS) * 0.8
        val bloom2 = smoothBlob(dist2, BLOOM_RADIUS) * 0.6

        // Water color: deep blue-green with sky reflections
        val waterHue = 0.55 + 0.08 * waterSurface + 0.03 * sin(slowT * 0.1)
        val waterSat = 0.5 + 0.15 * waterSurface
        val waterVal = 0.3 + 0.15 * (waterSurface + 1.0) / 2.0

        // Lily pad color: soft green
        val padHue = 0.33 + 0.04 * sin(slowT * 0.2 + nx * 2.0)
        val padSat = 0.55
        val padVal = 0.4 + 0.1 * sin(slowT * 0.15 + ny * 3.0)

        // Bloom color: rose to lavender
        val bloomHue = 0.9 + 0.06 * sin(slowT * 0.12)
        val bloomSat = 0.5
        val bloomVal = 0.7 + 0.15 * sin(slowT * 0.2 + 0.5)

        // Blend: water -> pad -> bloom
        val bloomAmount = (bloom1 + bloom2).coerceIn(0.0, 1.0)
        val hue = lerp(lerp(waterHue, padHue, totalPad), bloomHue, bloomAmount)
        val sat = lerp(lerp(waterSat, padSat, totalPad), bloomSat, bloomAmount)
        val value = lerp(lerp(waterVal, padVal, totalPad), bloomVal, bloomAmount)

        // Dappled light — sun fragments on the water
        val dapple = 0.08 * sin(nx * 12.0 + slowT * 0.8) * cos(ny * 10.0 - slowT * 0.6)
        val finalVal = (value + dapple).coerceIn(0.0, 1.0)

        return hsvToRgb(hue, sat.coerceIn(0.0, 1.0), finalVal)
    }

    private fun smoothBlob(dist: Double, radius: Double): Double {
        if (dist > radius) return 0.0
        val t = dist / radius
        return 1.0 - t * t * (3.0 - 2.0 * t)
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
        private const val TIME_SCALE = 0.06
        private const val PAD_RADIUS = 0.25
        private const val BLOOM_RADIUS = 0.1
    }
}
