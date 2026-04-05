package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * TURRELL GANZFELD — Inspired by James Turrell (1943–)
 *
 * Story:
 * James Turrell doesn't paint with pigment. He sculpts with light itself. In his
 * Ganzfeld installations, you step into a room of pure, uniform color — no edges,
 * no walls, no floor. Just light. Your eyes lose all reference points. Depth
 * vanishes. You float in color.
 *
 * Then, imperceptibly, the color shifts. Rose becomes peach. Peach becomes gold.
 * Gold becomes the palest blue, and you can't remember when it changed. Time
 * dissolves alongside space. You are inside the color, and the color is inside you.
 *
 * This shader pursues that same dissolution. The entire display breathes as one
 * luminous field, cycling through Turrell's signature palette with glacial slowness.
 * A very gentle spatial gradient — barely perceptible — gives the light just enough
 * dimensionality to feel like a room rather than a screen. The diffusion panel
 * completes the illusion: there is no display, only light.
 *
 * Patience is required. Turrell's work rewards those who stay.
 */
class TurrellGanzfeldShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val nx = x / DISPLAY_WIDTH
        val ny = y / DISPLAY_HEIGHT

        // Extremely slow time — Turrell demands patience
        val glacialT = t * TIME_SCALE

        // Primary hue cycle — moves through Turrell's palette:
        // rose -> peach -> amber -> pale gold -> sky blue -> twilight violet -> rose
        val huePhase = glacialT % 1.0
        val hue = turrellHue(huePhase)

        // Saturation breathes very gently
        val baseSat = 0.45 + 0.15 * sin(glacialT * 0.7)

        // Value is high — Turrell's light is luminous, not dim
        val baseVal = 0.7 + 0.1 * sin(glacialT * 0.5 + 0.3)

        // Very subtle spatial gradient — gives dimensionality without breaking uniformity
        // Light seems to emanate from a slowly drifting center
        val cx = 0.5 + 0.1 * sin(glacialT * 0.3)
        val cy = 0.5 + 0.1 * cos(glacialT * 0.25 + 0.5)
        val dist = sqrt((nx - cx) * (nx - cx) + (ny - cy) * (ny - cy))

        // Radial gradient — very subtle, max 8% variation
        val spatialVar = dist * 0.08
        val value = (baseVal - spatialVar).coerceIn(0.0, 1.0)

        // Slight hue shift at edges — Turrell rooms have subtle color temperature gradients
        val hueShift = dist * 0.02
        val finalHue = hue + hueShift

        // Gentle saturation increase at edges
        val sat = (baseSat + dist * 0.05).coerceIn(0.0, 1.0)

        return hsvToRgb(finalHue, sat, value)
    }

    /**
     * Maps a 0-1 phase to Turrell's characteristic color palette.
     * Smooth transitions through rose, peach, amber, pale sky, twilight.
     */
    private fun turrellHue(phase: Double): Double {
        // Turrell palette waypoints (hue values):
        // 0.0: rose (0.95)
        // 0.2: peach/salmon (0.05)
        // 0.4: amber/gold (0.12)
        // 0.6: pale sky blue (0.55)
        // 0.8: twilight violet (0.75)
        // 1.0: rose (0.95) — wraps
        return when {
            phase < 0.2 -> lerp(0.95, 1.05, phase / 0.2) % 1.0  // rose -> peach (crossing 0)
            phase < 0.4 -> lerp(0.05, 0.12, (phase - 0.2) / 0.2)  // peach -> amber
            phase < 0.6 -> lerp(0.12, 0.55, (phase - 0.4) / 0.2)  // amber -> sky
            phase < 0.8 -> lerp(0.55, 0.75, (phase - 0.6) / 0.2)  // sky -> twilight
            else -> lerp(0.75, 0.95, (phase - 0.8) / 0.2)          // twilight -> rose
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
        private const val DISPLAY_HEIGHT = 42.0
        private const val DISPLAY_WIDTH = 62.0
        // Full palette cycle takes ~10 minutes
        private const val TIME_SCALE = 0.0017
    }
}
