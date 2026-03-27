package dev.simonas.digitalsun.core

import kotlin.math.floor
import kotlin.math.sin

/**
 * Warp FBM shader — domain-warped fractional Brownian motion with a warm pink/fire colormap.
 * Based on Inigo Quilez's warp technique: https://iquilezles.org/articles/warp
 */
class WarpFbmShaderAlgorithm : PixelShader {

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val resX = 62.0

        val ux = x / resX
        val uy = y / resX // use same divisor for square aspect (matches original /iResolution.x)

        val shade = pattern(ux, uy, t)

        val r = colormapRed(shade)
        val g = colormapGreen(shade)
        val b = colormapBlue(shade)

        return ColorValue(
            r = r.coerceIn(0.0, 1.0),
            g = g.coerceIn(0.0, 1.0),
            b = b.coerceIn(0.0, 1.0),
            a = shade.coerceIn(0.0, 1.0)
        )
    }

    private fun pattern(x: Double, y: Double, t: Double): Double {
        val (fx1, fy1) = fbm(x, y, t)
        val (fx2, fy2) = fbm(x + fx1, y + fy1, t)
        val (fx3, _) = fbm(x + fx2, y + fy2, t)
        return fx3
    }

    private fun fbm(x: Double, y: Double, t: Double): Pair<Double, Double> {
        var px = x; var py = y
        var f = 0.0

        f += 0.500000 * noise(px + t, py + t); run { val nx = 0.80 * px + 0.60 * py; val ny = -0.60 * px + 0.80 * py; px = nx * 2.02; py = ny * 2.02 }
        f += 0.031250 * noise(px, py);          run { val nx = 0.80 * px + 0.60 * py; val ny = -0.60 * px + 0.80 * py; px = nx * 2.01; py = ny * 2.01 }
        f += 0.250000 * noise(px, py);          run { val nx = 0.80 * px + 0.60 * py; val ny = -0.60 * px + 0.80 * py; px = nx * 2.03; py = ny * 2.03 }
        f += 0.125000 * noise(px, py);          run { val nx = 0.80 * px + 0.60 * py; val ny = -0.60 * px + 0.80 * py; px = nx * 2.01; py = ny * 2.01 }
        f += 0.062500 * noise(px, py);          run { val nx = 0.80 * px + 0.60 * py; val ny = -0.60 * px + 0.80 * py; px = nx * 2.04; py = ny * 2.04 }
        f += 0.015625 * noise(px + sin(t), py + sin(t))

        return Pair(f / 0.96875, f / 0.96875)
    }

    private fun noise(px: Double, py: Double): Double {
        val ipx = floor(px)
        val ipy = floor(py)
        var ux = px - ipx
        var uy = py - ipy
        // Smoothstep: u = u*u*(3-2*u)
        ux = ux * ux * (3.0 - 2.0 * ux)
        uy = uy * uy * (3.0 - 2.0 * uy)

        val a = rand(ipx, ipy)
        val b = rand(ipx + 1.0, ipy)
        val c = rand(ipx, ipy + 1.0)
        val d = rand(ipx + 1.0, ipy + 1.0)

        val res = mix(mix(a, b, ux), mix(c, d, ux), uy)
        return res * res
    }

    private fun rand(nx: Double, ny: Double): Double {
        val dot = nx * 12.9898 + ny * 4.1414
        return fract(sin(dot) * 43758.5453)
    }

    private fun mix(a: Double, b: Double, t: Double) = a + (b - a) * t

    private fun fract(x: Double) = x - floor(x)

    private fun colormapRed(x: Double): Double = when {
        x < 0.0 -> 54.0 / 255.0
        x < 20049.0 / 82979.0 -> (829.79 * x + 54.51) / 255.0
        else -> 1.0
    }

    private fun colormapGreen(x: Double): Double = when {
        x < 20049.0 / 82979.0 -> 0.0
        x < 327013.0 / 810990.0 -> (8546482679670.0 / 10875673217.0 * x - 2064961390770.0 / 10875673217.0) / 255.0
        x <= 1.0 -> (103806720.0 / 483977.0 * x + 19607415.0 / 483977.0) / 255.0
        else -> 1.0
    }

    private fun colormapBlue(x: Double): Double = when {
        x < 0.0 -> 54.0 / 255.0
        x < 7249.0 / 82979.0 -> (829.79 * x + 54.51) / 255.0
        x < 20049.0 / 82979.0 -> 127.0 / 255.0
        x < 327013.0 / 810990.0 -> (792.02249341361393720147485376583 * x - 64.364790735602331034989206222672) / 255.0
        else -> 1.0
    }
}
