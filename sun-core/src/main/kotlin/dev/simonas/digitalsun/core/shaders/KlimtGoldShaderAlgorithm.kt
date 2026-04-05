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
 * KLIMT GOLD — Inspired by Gustav Klimt (1862–1918)
 *
 * Story:
 * Before he was a painter, Klimt was a goldsmith's son. He grew up watching his
 * father beat metal into leaf, learning that gold is not a color but a light —
 * it doesn't reflect the world, it replaces it. When Klimt entered his Golden Phase,
 * he covered his canvases in real gold leaf, creating paintings that were part icon,
 * part mosaic, part fever dream.
 *
 * "The Kiss" is not a painting of two lovers. It is a painting of two figures
 * dissolved into pattern — spirals, rectangles, circles — all rendered in gold
 * against gold, differentiated only by the geometry of their ornament. The bodies
 * disappear into decoration. The decoration becomes transcendent.
 *
 * This shader channels that Byzantine opulence. A field of shifting golds — amber,
 * honey, champagne, bronze — shimmers with mosaic-like cellular patterns. Deep
 * jewel tones — emerald, sapphire, ruby — pulse beneath the gold like hidden
 * gemstones. The patterns shift slowly, tiles of light rearranging themselves in
 * an endless, ornamental dance.
 *
 * Through the diffusion panel: liquid gold, breathing.
 */
class KlimtGoldShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val nx = x / DISPLAY_WIDTH
        val ny = y / DISPLAY_HEIGHT
        val slowT = t * TIME_SCALE

        // Mosaic cell pattern — creates Klimt's characteristic tessellation
        val cellSize = 0.12 + 0.02 * sin(slowT * 0.2)
        val cellX = ((nx + slowT * 0.02) % cellSize) / cellSize
        val cellY = ((ny + slowT * 0.015) % cellSize) / cellSize

        // Cell center distance — creates rounded mosaic tiles
        val cellDist = sqrt((cellX - 0.5).pow(2) + (cellY - 0.5).pow(2))
        val tileEdge = smoothstep(0.35, 0.5, cellDist)

        // Which tile are we in? Use for per-tile color variation
        val tileIdX = ((nx + slowT * 0.02) / cellSize).toInt()
        val tileIdY = ((ny + slowT * 0.015) / cellSize).toInt()
        val tileHash = pseudoRandom(tileIdX, tileIdY)

        // Gold palette — different gold tones per tile
        val goldHue = 0.1 + tileHash * 0.06  // range: amber to honey gold
        val goldSat = 0.6 + tileHash * 0.15
        val goldVal = 0.6 + 0.2 * (1.0 - tileEdge) + 0.05 * sin(slowT * 0.3 + tileHash * 6.28)

        // Jewel accent tiles — some tiles are deep jewel colors instead of gold
        val isJewel = tileHash > 0.75
        val jewelPhase = (slowT * 0.1 + tileHash * 3.0) % 1.0
        val jewelHue = when {
            jewelPhase < 0.33 -> 0.35 + tileHash * 0.05  // emerald
            jewelPhase < 0.66 -> 0.6 + tileHash * 0.05   // sapphire
            else -> 0.95 + tileHash * 0.05                // ruby
        }
        val jewelSat = 0.7
        val jewelVal = 0.35 + 0.1 * sin(slowT * 0.25 + tileHash * 4.0)

        // Select gold or jewel
        val hue: Double
        val sat: Double
        val baseVal: Double
        if (isJewel) {
            hue = jewelHue
            sat = jewelSat
            baseVal = jewelVal
        } else {
            hue = goldHue
            sat = goldSat
            baseVal = goldVal
        }

        // Shimmer — gold leaf catches light at different angles
        val shimmer = 0.08 * sin(nx * 20.0 + ny * 15.0 + slowT * 0.8) *
                cos(nx * 12.0 - ny * 18.0 + slowT * 0.6)
        val value = (baseVal + shimmer).coerceIn(0.0, 1.0)

        // Tile border darkening — subtle grout lines between mosaic tiles
        val border = tileEdge * 0.15
        val finalVal = (value - border).coerceIn(0.0, 1.0)

        return hsvToRgb(hue, sat.coerceIn(0.0, 1.0), finalVal)
    }

    private fun pseudoRandom(ix: Int, iy: Int): Double {
        val n = ix * 374761393 + iy * 668265263
        val m = (n xor (n shr 13)) * 1274126177
        return (m and 0x7fffffff) / 2147483647.0
    }

    private fun smoothstep(edge0: Double, edge1: Double, x: Double): Double {
        val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0.0, 1.0)
        return t * t * (3.0 - 2.0 * t)
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
        private const val TIME_SCALE = 0.06
    }
}
