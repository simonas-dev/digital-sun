package dev.simonas.digitalsun.core.shaders

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * HOKUSAI WAVE — Inspired by Katsushika Hokusai (1760–1849)
 *
 * Story:
 * At seventy years old, Hokusai carved "The Great Wave off Kanagawa" and changed
 * art forever. The wave is not just water — it is a claw, an embrace, a frozen
 * moment of terrible beauty. Beneath it, Mount Fuji sits serene and tiny, a
 * reminder that nature's fury dwarfs human ambition.
 *
 * What makes the print immortal is its palette: the deep Prussian blue (imported
 * from Europe, exotic in Edo-period Japan), the indigo shadows, the white foam
 * fingers clawing at the sky. It is a painting about blue.
 *
 * This shader channels that oceanic energy. Deep indigo waves roll across the
 * display with slow, inevitable power. Their crests catch light — white foam
 * that appears and dissolves. The depths between waves are near-black, the
 * blue of deep ocean trenches. Through the diffusion panel, the waves become
 * swells of luminous blue, their foam a ghostly glow — as if you're watching
 * the ocean from beneath the surface, looking up at the storm.
 */
class HokusaiWaveShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val nx = x / DISPLAY_WIDTH
        val ny = y / DISPLAY_HEIGHT
        val slowT = t * TIME_SCALE

        // Multiple wave layers moving at different speeds — the ocean is polyrhythmic
        val wave1 = sin(nx * 6.0 - slowT * 1.0 + ny * 2.0) * 0.5
        val wave2 = sin(nx * 10.0 - slowT * 1.5 + 0.8) * 0.25
        val wave3 = sin(nx * 3.0 - slowT * 0.6 + ny * 1.5 + 2.0) * 0.35
        val wave4 = cos(nx * 8.0 - slowT * 1.2 + ny * 0.5 + 1.0) * 0.15

        // Combined wave height
        val waveHeight = wave1 + wave2 + wave3 + wave4

        // Wave crest detection — where the wave peaks, foam appears
        val waveSlope = cos(nx * 6.0 - slowT * 1.0 + ny * 2.0) * 6.0
        val isCrest = (waveSlope > 3.0 && waveHeight > 0.3)
        val foamIntensity = if (isCrest) {
            ((waveSlope - 3.0) / 3.0).coerceIn(0.0, 1.0) * ((waveHeight - 0.3) / 0.7).coerceIn(0.0, 1.0)
        } else 0.0

        // Secondary foam from wave2
        val foam2Slope = cos(nx * 10.0 - slowT * 1.5 + 0.8) * 10.0
        val foam2 = if (foam2Slope > 6.0 && wave2 > 0.1) {
            ((foam2Slope - 6.0) / 4.0).coerceIn(0.0, 1.0) * 0.3
        } else 0.0

        val totalFoam = (foamIntensity + foam2).coerceIn(0.0, 1.0)

        // Depth color — map wave height to blue intensity
        // Deep troughs are dark indigo, peaks are brighter blue
        val heightNorm = ((waveHeight + 1.25) / 2.5).coerceIn(0.0, 1.0)

        // Hokusai's palette: deep indigo to Prussian blue
        val deepR = 0.02
        val deepG = 0.04
        val deepB = 0.15

        val midR = 0.04
        val midG = 0.1
        val midB = 0.4

        val brightR = 0.08
        val brightG = 0.2
        val brightB = 0.55

        // Interpolate through depth
        val baseR = lerp(deepR, lerp(midR, brightR, heightNorm), heightNorm)
        val baseG = lerp(deepG, lerp(midG, brightG, heightNorm), heightNorm)
        val baseB = lerp(deepB, lerp(midB, brightB, heightNorm), heightNorm)

        // Add foam — white with slight blue tint
        val r = lerp(baseR, 0.85, totalFoam)
        val g = lerp(baseG, 0.9, totalFoam)
        val b = lerp(baseB, 0.95, totalFoam)

        // Subtle overall breathing
        val breath = 1.0 + 0.05 * sin(slowT * 0.3)

        return ColorValue(
            r = (r * breath).coerceIn(0.0, 1.0),
            g = (g * breath).coerceIn(0.0, 1.0),
            b = (b * breath).coerceIn(0.0, 1.0)
        )
    }

    private fun lerp(a: Double, b: Double, t: Double): Double = a + (b - a) * t

    companion object {
        private const val DISPLAY_HEIGHT = 42.0
        private const val DISPLAY_WIDTH = 62.0
        private const val TIME_SCALE = 0.08
    }
}
