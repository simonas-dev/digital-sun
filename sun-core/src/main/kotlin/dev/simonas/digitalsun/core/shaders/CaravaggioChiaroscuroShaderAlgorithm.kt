package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * CARAVAGGIO CHIAROSCURO — Inspired by Michelangelo Merisi da Caravaggio (1571–1610)
 *
 * Story:
 * Caravaggio painted from darkness. His canvases begin as voids — not the gentle
 * shadow of Rembrandt, but absolute black, the darkness of a Roman cellar at
 * midnight. Then a beam of light slashes through the scene like a blade, illuminating
 * a face, a hand, a gesture. Everything the light doesn't touch ceases to exist.
 *
 * This is tenebrism: the drama of light against the annihilation of shadow. In
 * "The Calling of Saint Matthew," a shaft of divine light cuts diagonally across
 * a dim tavern, pointing at Matthew like a finger of God. The light doesn't just
 * illuminate — it judges, it chooses, it transforms.
 *
 * This shader casts dramatic light beams across the dark display. One or two shafts
 * of warm, amber-gold light sweep slowly through deep darkness. Where the beams
 * fall, the surface glows with warm flesh tones and candlelight gold. Where they
 * don't, only the faintest suggestion of form remains. The beams move with
 * theatrical deliberation, as if controlled by a Renaissance stage director.
 *
 * Through the diffusion panel: a single candle in a dark room, slowly turning.
 */
class CaravaggioChiaroscuroShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val nx = x / DISPLAY_WIDTH
        val ny = y / DISPLAY_HEIGHT
        val slowT = t * TIME_SCALE

        // Primary light beam — sweeps diagonally across the scene
        val beam1Angle = slowT * 0.3
        val beam1X = 0.5 + 0.4 * cos(beam1Angle)
        val beam1Y = 0.5 + 0.3 * sin(beam1Angle * 0.7)

        // Beam direction — angled, like Caravaggio's divine light
        val beam1DirX = cos(beam1Angle * 0.5 + 0.8)
        val beam1DirY = sin(beam1Angle * 0.5 + 0.8)

        // Distance from pixel to the beam's line
        val dx1 = nx - beam1X
        val dy1 = ny - beam1Y
        val along1 = dx1 * beam1DirX + dy1 * beam1DirY
        val perpDist1 = sqrt((dx1 - along1 * beam1DirX).pow(2) + (dy1 - along1 * beam1DirY).pow(2))

        // Beam intensity — bright core, soft edges, fades along length
        val beamCore1 = (1.0 - (perpDist1 / BEAM_WIDTH).coerceIn(0.0, 1.0)).pow(2)
        val lengthFade1 = (1.0 - (along1.pow(2) / BEAM_LENGTH)).coerceIn(0.0, 1.0)
        val beam1 = beamCore1 * lengthFade1

        // Secondary beam — dimmer, different angle
        val beam2Angle = slowT * 0.2 + 2.0
        val beam2X = 0.5 + 0.35 * sin(beam2Angle)
        val beam2Y = 0.5 + 0.25 * cos(beam2Angle * 0.8)
        val beam2DirX = cos(beam2Angle * 0.4 + 1.5)
        val beam2DirY = sin(beam2Angle * 0.4 + 1.5)

        val dx2 = nx - beam2X
        val dy2 = ny - beam2Y
        val along2 = dx2 * beam2DirX + dy2 * beam2DirY
        val perpDist2 = sqrt((dx2 - along2 * beam2DirX).pow(2) + (dy2 - along2 * beam2DirY).pow(2))

        val beamCore2 = (1.0 - (perpDist2 / (BEAM_WIDTH * 0.7)).coerceIn(0.0, 1.0)).pow(2)
        val lengthFade2 = (1.0 - (along2.pow(2) / (BEAM_LENGTH * 0.6))).coerceIn(0.0, 1.0)
        val beam2 = beamCore2 * lengthFade2 * 0.5  // Dimmer secondary

        // Total illumination
        val illumination = (beam1 + beam2).coerceIn(0.0, 1.0)

        // Caravaggio's warm palette in lit areas
        // Candlelight amber at bright center, deeper red-brown at edges
        val hue = 0.07 - 0.03 * illumination  // more golden in bright areas
        val sat = 0.65 + 0.15 * (1.0 - illumination)  // more saturated in mid-tones
        val value = illumination.pow(0.8) * 0.85  // power curve for dramatic falloff

        // Absolute darkness in unlit areas — Caravaggio's void
        if (illumination < 0.01) {
            return ColorValue(0.0, 0.0, 0.0)
        }

        return hsvToRgb(hue, sat.coerceIn(0.0, 1.0), value.coerceIn(0.0, 1.0))
    }

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
        private const val BEAM_WIDTH = 0.15
        private const val BEAM_LENGTH = 0.5
    }
}
