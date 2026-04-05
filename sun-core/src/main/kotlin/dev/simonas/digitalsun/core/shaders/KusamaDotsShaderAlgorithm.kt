package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * KUSAMA'S INFINITY — Inspired by Yayoi Kusama (1929–)
 *
 * Story:
 * Since childhood, Yayoi Kusama has seen the world dissolving into dots. Hallucinations
 * of infinite polka dots spreading across every surface — her body, the furniture, the
 * sky — drove her to make art as an act of survival. "Our earth is only one polka dot
 * among a million stars in the cosmos," she writes.
 *
 * Her Infinity Mirror Rooms envelop visitors in endless reflections of colored lights,
 * obliterating the boundary between self and space. You don't observe the art — you
 * become part of the pattern.
 *
 * This shader scatters glowing dots across the diamond display. Each dot pulses with
 * its own rhythm, growing and shrinking like breathing cells. The dots drift slowly,
 * their colors cycling through Kusama's favorite vivid reds, yellows, and magentas
 * against a deep dark background. Some dots bloom into existence while others fade
 * away, creating an endlessly shifting constellation — a tiny infinity room
 * you can hold in your hands.
 */
class KusamaDotsShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val slowT = t * TIME_SCALE

        // Background: deep dark with a subtle color shift
        var r = 0.02 + 0.01 * sin(slowT * 0.1)
        var g = 0.01
        var b = 0.03 + 0.01 * sin(slowT * 0.08)

        // Check distance to each dot in the grid
        // Use a tiled grid approach — each cell can contain one dot
        val cellX = floor(x / CELL_SIZE).toInt()
        val cellY = floor(y / CELL_SIZE).toInt()

        // Check neighboring cells too for dots that might bleed over
        for (di in -1..1) {
            for (dj in -1..1) {
                val ci = cellX + di
                val cj = cellY + dj

                // Pseudo-random dot position within this cell
                val hash = hash2D(ci, cj)
                val dotCenterX = (ci + hash.first) * CELL_SIZE
                val dotCenterY = (cj + hash.second) * CELL_SIZE

                // Each dot has its own pulse phase and color
                val dotPhase = hash.first * 6.28 + hash.second * 3.14
                val dotPulse = 0.5 + 0.5 * sin(slowT * PULSE_SPEED + dotPhase)

                // Dot radius varies with pulse
                val dotRadius = (MIN_RADIUS + (MAX_RADIUS - MIN_RADIUS) * dotPulse)

                // Distance from pixel to dot center
                val dx = x - dotCenterX
                val dy = y - dotCenterY
                val dist = sqrt(dx * dx + dy * dy)

                if (dist < dotRadius) {
                    // Soft circular falloff
                    val intensity = (1.0 - dist / dotRadius)
                    val soft = intensity * intensity // quadratic falloff for soft edges

                    // Color from Kusama's palette based on cell hash
                    val colorIndex = ((ci * 7 + cj * 13) % PALETTE.size + PALETTE.size) % PALETTE.size
                    val dotColor = PALETTE[colorIndex]

                    // Additive blend — dots glow
                    r += dotColor.r * soft * dotPulse
                    g += dotColor.g * soft * dotPulse
                    b += dotColor.b * soft * dotPulse
                }
            }
        }

        return ColorValue(
            r = r.coerceIn(0.0, 1.0),
            g = g.coerceIn(0.0, 1.0),
            b = b.coerceIn(0.0, 1.0),
            a = 1.0
        )
    }

    /**
     * Simple 2D hash returning pseudo-random pair in [0.15, 0.85] for dot placement within cell.
     */
    private fun hash2D(ix: Int, iy: Int): Pair<Double, Double> {
        val n1 = ((ix * 127 + iy * 311) and 0x7FFFFFFF) % 1000 / 1000.0
        val n2 = ((ix * 269 + iy * 173) and 0x7FFFFFFF) % 1000 / 1000.0
        return Pair(0.15 + n1 * 0.7, 0.15 + n2 * 0.7)
    }

    companion object {
        private const val TIME_SCALE = 0.15
        private const val CELL_SIZE = 6.0
        private const val MIN_RADIUS = 1.5
        private const val MAX_RADIUS = 3.5
        private const val PULSE_SPEED = 1.2

        // Kusama's vivid palette: scarlet, hot yellow, magenta, orange, electric pink
        private val PALETTE = listOf(
            ColorValue(0.95, 0.10, 0.10), // scarlet red
            ColorValue(1.00, 0.85, 0.05), // hot yellow
            ColorValue(0.90, 0.10, 0.55), // magenta
            ColorValue(1.00, 0.45, 0.05), // vivid orange
            ColorValue(1.00, 0.20, 0.60), // electric pink
        )
    }
}
