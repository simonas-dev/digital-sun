package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * ELIASSON'S SUN — Inspired by Olafur Eliasson (1967–)
 *
 * Story:
 * In 2003, Olafur Eliasson hung an artificial sun in the Tate Modern's vast Turbine Hall.
 * "The Weather Project" was a semicircle of hundreds of mono-frequency lamps behind a
 * screen of haze, reflected in a mirrored ceiling to complete the orb. Two million people
 * came. They lay on the floor and stared up at the glowing disc for hours. Some wept.
 *
 * Eliasson had created something impossible: an indoor sunset that never ended. The
 * mono-frequency light stripped the world of all color except shades of yellow and black,
 * making every visitor a silhouette, every face a shadow puppet against the glow.
 *
 * This shader recreates that eternal golden moment. A radiant disc sits at the display's
 * center, its light falling off in concentric halos of deepening amber and orange. Subtle
 * atmospheric ripples drift through the glow like heat haze, and the light breathes with
 * a slow, barely perceptible pulse — not quite a heartbeat, but close enough to feel alive.
 * The edges of the display dissolve into deep amber darkness, as if the sun is setting
 * into an infinite horizon.
 */
class EliassonSunShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val slowT = t * TIME_SCALE

        // Center coordinates, normalized so radius 1.0 reaches display edge
        val cx = (x - DISPLAY_WIDTH / 2.0) / (DISPLAY_HEIGHT / 2.0)
        val cy = (y - DISPLAY_HEIGHT / 2.0) / (DISPLAY_HEIGHT / 2.0)

        val radius = sqrt(cx * cx + cy * cy)

        // Atmospheric haze — subtle displacement
        val hazeX = 0.03 * sin(cy * 5.0 + slowT * 0.8) * cos(cx * 3.0 + slowT * 0.5)
        val hazeY = 0.03 * cos(cx * 4.0 + slowT * 0.6) * sin(cy * 6.0 + slowT * 0.7)
        val hazeRadius = sqrt((cx + hazeX) * (cx + hazeX) + (cy + hazeY) * (cy + hazeY))

        // Core brightness: intense center with smooth radial falloff
        // The "mono-frequency" sun disc
        val coreBrightness = (1.0 - hazeRadius / SUN_RADIUS).coerceIn(0.0, 1.0).pow(0.5)

        // Halo rings — concentric glow layers beyond the core
        val haloIntensity = (1.0 / (1.0 + (hazeRadius * HALO_FALLOFF).pow(2.5))) * HALO_BRIGHTNESS

        // Breathing pulse — very slow, organic
        val breathe = 1.0 + BREATHE_AMP * sin(slowT * BREATHE_SPEED)

        // Combined light intensity
        val intensity = (coreBrightness + haloIntensity) * breathe

        // Eliasson's mono-frequency palette: pure amber to deep orange
        // Brighter areas are golden yellow, dimmer areas shift to deep orange/red
        val hue = lerp(GOLDEN_HUE, AMBER_HUE, (1.0 - intensity).coerceIn(0.0, 1.0))
        val sat = lerp(0.7, 1.0, (1.0 - intensity).coerceIn(0.0, 1.0))
        val value = intensity.coerceIn(0.0, 1.0)

        return hsvToRgb(hue, sat, value)
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
        private const val TIME_SCALE = 0.1
        private const val SUN_RADIUS = 0.6
        private const val HALO_FALLOFF = 1.8
        private const val HALO_BRIGHTNESS = 0.4
        private const val BREATHE_AMP = 0.08
        private const val BREATHE_SPEED = 0.4
        private const val GOLDEN_HUE = 0.125  // ~45° golden yellow
        private const val AMBER_HUE = 0.04     // ~14° deep amber/orange
    }
}
