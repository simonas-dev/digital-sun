package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * HILMA'S SPIRALS — Inspired by Hilma af Klint (1862–1944)
 *
 * Story:
 * Decades before Kandinsky or Mondrian, a Swedish mystic was already painting the invisible.
 * Hilma af Klint channeled what she called "the High Ones" — spiritual guides who dictated
 * compositions of spirals, nested circles, and biomorphic forms in colors so bold they
 * feel radioactive. Her "Group IV, The Ten Largest" series depicts stages of human life
 * as enormous mandalas of peach, gold, violet, and teal.
 *
 * She stipulated her work remain hidden for 20 years after her death, believing the world
 * was not ready. When it finally emerged, it rewrote the history of abstraction.
 *
 * This shader channels her cosmic geometry: concentric rings of color spiral outward from
 * the display's center, slowly rotating and breathing. The palette cycles through her
 * signature combinations — warm peach against cool lilac, golden yellow beside deep blue.
 * Each ring pulses at its own tempo, as if the display is a living mandala receiving
 * transmissions from somewhere beyond.
 */
class HilmaSpiralsShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        // Center the coordinate system
        val cx = (x - DISPLAY_WIDTH / 2.0) / (DISPLAY_HEIGHT / 2.0)
        val cy = (y - DISPLAY_HEIGHT / 2.0) / (DISPLAY_HEIGHT / 2.0)

        // Polar coordinates
        val radius = sqrt(cx * cx + cy * cy)
        val angle = atan2(cy, cx)

        val slowT = t * TIME_SCALE

        // Spiral: combine radius and angle with time for rotation
        val spiral = radius * RING_COUNT - angle / (2.0 * PI) + slowT * ROTATION_SPEED
        val ringPhase = spiral % 1.0
        val ringIndex = spiral.toInt()

        // Each ring gets a color from Hilma's palette, cycling through
        val paletteIndex = ((ringIndex % PALETTE.size) + PALETTE.size) % PALETTE.size
        val nextPaletteIndex = ((ringIndex + 1) % PALETTE.size + PALETTE.size) % PALETTE.size

        val baseColor = PALETTE[paletteIndex]
        val nextColor = PALETTE[nextPaletteIndex]

        // Soft transition between rings
        val blend = smoothstep(0.4, 0.6, ringPhase)

        // Each ring breathes at its own pace
        val breathe = 0.15 * sin(slowT * (1.0 + paletteIndex * 0.3) + paletteIndex * 0.7)

        // Radial fade — dimmer at edges like a vignette
        val vignette = (1.0 - radius * 0.3).coerceIn(0.2, 1.0)

        val r = (lerp(baseColor.r, nextColor.r, blend) + breathe).coerceIn(0.0, 1.0) * vignette
        val g = (lerp(baseColor.g, nextColor.g, blend) + breathe * 0.5).coerceIn(0.0, 1.0) * vignette
        val b = (lerp(baseColor.b, nextColor.b, blend) + breathe * 0.3).coerceIn(0.0, 1.0) * vignette

        return ColorValue(
            r = r.coerceIn(0.0, 1.0),
            g = g.coerceIn(0.0, 1.0),
            b = b.coerceIn(0.0, 1.0),
            a = 1.0
        )
    }

    private fun smoothstep(edge0: Double, edge1: Double, x: Double): Double {
        val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0.0, 1.0)
        return t * t * (3.0 - 2.0 * t)
    }

    private fun lerp(a: Double, b: Double, t: Double): Double = a + (b - a) * t

    companion object {
        private const val DISPLAY_HEIGHT = 42.0
        private const val DISPLAY_WIDTH = 62.0
        private const val TIME_SCALE = 0.12
        private const val ROTATION_SPEED = 0.3
        private const val RING_COUNT = 5.0

        // Hilma af Klint's palette: peach, golden yellow, lilac, deep blue, teal, rose
        private val PALETTE = listOf(
            ColorValue(0.95, 0.72, 0.55), // warm peach
            ColorValue(0.92, 0.82, 0.25), // golden yellow
            ColorValue(0.68, 0.52, 0.78), // lilac
            ColorValue(0.15, 0.18, 0.55), // deep blue
            ColorValue(0.20, 0.65, 0.60), // teal
            ColorValue(0.85, 0.35, 0.45), // rose
        )
    }
}
