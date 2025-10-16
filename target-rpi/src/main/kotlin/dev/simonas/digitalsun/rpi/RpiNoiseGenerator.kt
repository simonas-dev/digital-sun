package dev.simonas.digitalsun.rpi

import dev.simonas.digitalsun.core.NoiseGenerator
import kotlin.math.floor

/**
 * Simple Perlin noise implementation for RPI target
 * Based on Ken Perlin's improved noise (2002)
 */
class RpiNoiseGenerator : NoiseGenerator {

    private val permutation = IntArray(512)

    init {
        // Standard permutation table
        val p = intArrayOf(
            151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225,
            140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148,
            247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32,
            57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175,
            74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122,
            60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54,
            65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169,
            200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64,
            52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212,
            207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213,
            119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
            129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104,
            218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241,
            81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157,
            184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93,
            222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
        )

        // Duplicate the permutation table
        for (i in 0..255) {
            permutation[i] = p[i]
            permutation[256 + i] = p[i]
        }
    }

    override fun perlin(seed: Int, x: Double, y: Double, z: Double): Double {
        // Offset by seed
        val xs = x + seed * 123.456
        val ys = y + seed * 234.567
        val zs = z + seed * 345.678

        // Find unit cube that contains point
        val X = floor(xs).toInt() and 255
        val Y = floor(ys).toInt() and 255
        val Z = floor(zs).toInt() and 255

        // Find relative x, y, z of point in cube
        val xf = xs - floor(xs)
        val yf = ys - floor(ys)
        val zf = zs - floor(zs)

        // Compute fade curves
        val u = fade(xf)
        val v = fade(yf)
        val w = fade(zf)

        // Hash coordinates of 8 cube corners
        val aaa = permutation[permutation[permutation[X] + Y] + Z]
        val aba = permutation[permutation[permutation[X] + inc(Y)] + Z]
        val aab = permutation[permutation[permutation[X] + Y] + inc(Z)]
        val abb = permutation[permutation[permutation[X] + inc(Y)] + inc(Z)]
        val baa = permutation[permutation[permutation[inc(X)] + Y] + Z]
        val bba = permutation[permutation[permutation[inc(X)] + inc(Y)] + Z]
        val bab = permutation[permutation[permutation[inc(X)] + Y] + inc(Z)]
        val bbb = permutation[permutation[permutation[inc(X)] + inc(Y)] + inc(Z)]

        // Blend results from 8 corners
        var x1 = lerp(grad(aaa, xf, yf, zf), grad(baa, xf - 1, yf, zf), u)
        var x2 = lerp(grad(aba, xf, yf - 1, zf), grad(bba, xf - 1, yf - 1, zf), u)
        val y1 = lerp(x1, x2, v)

        x1 = lerp(grad(aab, xf, yf, zf - 1), grad(bab, xf - 1, yf, zf - 1), u)
        x2 = lerp(grad(abb, xf, yf - 1, zf - 1), grad(bbb, xf - 1, yf - 1, zf - 1), u)
        val y2 = lerp(x1, x2, v)

        return lerp(y1, y2, w)
    }

    override fun perlinLinear(seed: Int, x: Double, y: Double, z: Double): Double {
        // Linear interpolation version (no fade curves)
        val xs = x + seed * 123.456
        val ys = y + seed * 234.567
        val zs = z + seed * 345.678

        val X = floor(xs).toInt() and 255
        val Y = floor(ys).toInt() and 255
        val Z = floor(zs).toInt() and 255

        val xf = xs - floor(xs)
        val yf = ys - floor(ys)
        val zf = zs - floor(zs)

        val aaa = permutation[permutation[permutation[X] + Y] + Z]
        val aba = permutation[permutation[permutation[X] + inc(Y)] + Z]
        val aab = permutation[permutation[permutation[X] + Y] + inc(Z)]
        val abb = permutation[permutation[permutation[X] + inc(Y)] + inc(Z)]
        val baa = permutation[permutation[permutation[inc(X)] + Y] + Z]
        val bba = permutation[permutation[permutation[inc(X)] + inc(Y)] + Z]
        val bab = permutation[permutation[permutation[inc(X)] + Y] + inc(Z)]
        val bbb = permutation[permutation[permutation[inc(X)] + inc(Y)] + inc(Z)]

        var x1 = lerp(grad(aaa, xf, yf, zf), grad(baa, xf - 1, yf, zf), xf)
        var x2 = lerp(grad(aba, xf, yf - 1, zf), grad(bba, xf - 1, yf - 1, zf), xf)
        val y1 = lerp(x1, x2, yf)

        x1 = lerp(grad(aab, xf, yf, zf - 1), grad(bab, xf - 1, yf, zf - 1), xf)
        x2 = lerp(grad(abb, xf, yf - 1, zf - 1), grad(bbb, xf - 1, yf - 1, zf - 1), xf)
        val y2 = lerp(x1, x2, yf)

        return lerp(y1, y2, zf)
    }

    override fun fbm(
        seed: Int,
        x: Double,
        y: Double,
        z: Double,
        octaves: Int,
        lacunarity: Double,
        gain: Double,
        useLinear: Boolean
    ): Double {
        var total = 0.0
        var frequency = 1.0
        var amplitude = 1.0
        var maxValue = 0.0

        for (i in 0 until octaves) {
            val noise = if (useLinear) {
                perlinLinear(seed, x * frequency, y * frequency, z * frequency)
            } else {
                perlin(seed, x * frequency, y * frequency, z * frequency)
            }
            total += noise * amplitude

            maxValue += amplitude

            amplitude *= gain
            frequency *= lacunarity
        }

        return total / maxValue
    }

    private fun inc(num: Int): Int {
        return (num + 1) and 255
    }

    private fun fade(t: Double): Double {
        // 6t^5 - 15t^4 + 10t^3
        return t * t * t * (t * (t * 6 - 15) + 10)
    }

    private fun lerp(a: Double, b: Double, t: Double): Double {
        return a + t * (b - a)
    }

    private fun grad(hash: Int, x: Double, y: Double, z: Double): Double {
        // Convert low 4 bits of hash code into 12 gradient directions
        val h = hash and 15
        val u = if (h < 8) x else y
        val v = if (h < 4) y else if (h == 12 || h == 14) x else z
        return (if ((h and 1) == 0) u else -u) + (if ((h and 2) == 0) v else -v)
    }
}
