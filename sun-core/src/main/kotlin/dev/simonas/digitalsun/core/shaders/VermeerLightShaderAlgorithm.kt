package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * VERMEER LIGHT — Inspired by Johannes Vermeer (1632–1675)
 *
 * Story:
 * Vermeer painted light the way a jeweler cuts diamonds — with infinite precision
 * and reverence. His rooms in Delft are small and quiet, but the light that enters
 * through the left-side window transforms everything it touches into something sacred.
 * A milk jug becomes a vessel of liquid gold. A pearl earring becomes a captured star.
 * The far wall glows with reflected warmth while shadows pool in the corners like
 * velvet.
 *
 * This shader recreates that domestic luminosity. A warm golden light source drifts
 * slowly from one side of the display, casting a gentle gradient from brilliant amber
 * to deep shadow. The light has that particular Vermeer quality — not harsh sunlight,
 * but the soft, buttery radiance of a north-facing Dutch window on a cloudy afternoon.
 * Subtle pearl-like highlights bloom and fade where the light is strongest.
 *
 * Through the diffusion panel, the effect is intimate and warm — a small room filled
 * with golden light, timeless and still.
 */
class VermeerLightShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val nx = x / DISPLAY_WIDTH
        val ny = y / DISPLAY_HEIGHT
        val slowT = t * TIME_SCALE

        // Light source position — drifts slowly, always from the left side (Vermeer's convention)
        val lightX = 0.15 + 0.1 * sin(slowT * 0.3)
        val lightY = 0.35 + 0.08 * cos(slowT * 0.25 + 0.5)

        // Distance from light source
        val dx = nx - lightX
        val dy = ny - lightY
        val dist = sqrt(dx * dx + dy * dy)

        // Light falloff — warm near source, dim at edges
        val falloff = 1.0 / (1.0 + dist * dist * FALLOFF_STRENGTH)

        // Base illumination — Vermeer's golden warmth
        val warmHue = 0.1 + 0.02 * sin(slowT * 0.15)  // golden amber
        val warmSat = 0.5 - 0.15 * falloff  // less saturated in bright areas (washed out light)
        val warmVal = 0.15 + 0.65 * falloff  // bright near source, dim in shadows

        // Shadow color — cooler, more blue-ish in the dark areas
        val shadowHue = 0.6  // cool blue shadow
        val shadowSat = 0.3
        val shadowVal = 0.08

        // Blend warm and shadow based on illumination
        val illumination = falloff.pow(0.7)  // soften the falloff curve
        val hue = lerp(shadowHue, warmHue, illumination)
        val sat = lerp(shadowSat, warmSat, illumination)
        val baseVal = lerp(shadowVal, warmVal, illumination)

        // Pearl highlights — Vermeer's signature luminous spots
        // Small bright spots that catch the light
        val pearl1x = 0.4 + 0.05 * sin(slowT * 0.2 + 1.0)
        val pearl1y = 0.4 + 0.03 * cos(slowT * 0.18)
        val pearlDist1 = sqrt((nx - pearl1x).pow(2) + (ny - pearl1y).pow(2))
        val pearl1 = pearlGlow(pearlDist1, 0.06) * illumination

        val pearl2x = 0.35 + 0.04 * cos(slowT * 0.15 + 2.0)
        val pearl2y = 0.55 + 0.04 * sin(slowT * 0.22 + 0.5)
        val pearlDist2 = sqrt((nx - pearl2x).pow(2) + (ny - pearl2y).pow(2))
        val pearl2 = pearlGlow(pearlDist2, 0.04) * illumination

        val pearlBoost = (pearl1 + pearl2) * 0.2

        // Subtle dust motes in the light beam — tiny brightness variations
        val motes = 0.03 * sin(nx * 15.0 + slowT * 0.5) * cos(ny * 12.0 + slowT * 0.4) * illumination

        val value = (baseVal + pearlBoost + motes).coerceIn(0.0, 1.0)

        return hsvToRgb(hue, sat.coerceIn(0.0, 1.0), value)
    }

    private fun pearlGlow(dist: Double, radius: Double): Double {
        if (dist > radius) return 0.0
        val t = dist / radius
        return (1.0 - t * t).pow(2)
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
        private const val TIME_SCALE = 0.05
        private const val FALLOFF_STRENGTH = 4.0
    }
}
