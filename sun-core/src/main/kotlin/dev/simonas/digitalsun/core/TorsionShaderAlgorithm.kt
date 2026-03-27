package dev.simonas.digitalsun.core

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tanh

/**
 * Volumetric raymarching shader with turbulent torsion on a sphere.
 * Produces a glowing, twisting plasma orb effect.
 */
class TorsionShaderAlgorithm : PixelShader {

    // Pre-computed constant rotation matrices (row-major)
    private val rotx = doubleArrayOf(
        1.0, 0.0, 0.0,
        0.0, 0.6, -0.8,
        0.0, 0.8, 0.6
    )
    private val rotz = doubleArrayOf(
        0.8, -0.6, 0.0,
        0.6, 0.8, 0.0,
        0.0, 0.0, 1.0
    )
    private val roty = doubleArrayOf(
        0.8, 0.0, -0.6,
        0.0, 1.0, 0.0,
        0.6, 0.0, 0.8
    )

    override fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue {
        val resY = 42.0

        // Normalized coordinates centered at origin
        val ux = x / resY
        val uy = y / resY

        // Ray direction
        val dx = ux * 2.0
        val dy = uy * 2.0
        val dz = 1.0
        val dLen = sqrt(dx * dx + dy * dy + dz * dz)
        var dirX = dx / dLen
        var dirY = dy / dLen
        var dirZ = dz / dLen

        // Camera position
        var posX = 0.0
        var posY = 0.0
        var posZ = -5.0

        // Rotate camera around Y (pos.xz and dir.xz)
        val angle = t * 0.1
        val ca = cos(angle)
        val sa = sin(angle)
        val tmpX = posX * ca - posZ * sa
        val tmpZ = posX * sa + posZ * ca
        posX = tmpX; posZ = tmpZ
        val tmpDx = dirX * ca - dirZ * sa
        val tmpDz = dirX * sa + dirZ * ca
        dirX = tmpDx; dirZ = tmpDz

        // Raymarch
        var colR = 0.0
        var colG = 0.0
        var colB = 0.0

        for (i in 0 until STEPS) {
            val (tx, ty, tz) = torsion(posX, posY, posZ, t)
            val vol = sphere(tx, ty, tz)
            posX += dirX * vol / 2.5
            posY += dirY * vol / 2.5
            posZ += dirZ * vol / 2.5
            colR += 3.0 / vol
            colG += 2.0 / vol
            colB += 1.0 / vol
        }

        // tanh(BRIGHTNESS * sqrt(col^3))
        val r = tanh(BRIGHTNESS * sqrt(colR * colR * colR))
        val g = tanh(BRIGHTNESS * sqrt(colG * colG * colG))
        val b = tanh(BRIGHTNESS * sqrt(colB * colB * colB))

        return ColorValue(
            r = r.coerceIn(0.0, 1.0),
            g = g.coerceIn(0.0, 1.0),
            b = b.coerceIn(0.0, 1.0),
            a = 1.0
        )
    }

    private fun torsion(px: Double, py: Double, pz: Double, t: Double): Triple<Double, Double, Double> {
        var posX = px; var posY = py; var posZ = pz

        // E starts as identity, then E = E * rotx * rotz
        var e = identity()
        e = mul(e, rotx)
        e = mul(e, rotz)

        var freq = TURB_FREQ

        for (i in 0 until TURB_NUM) {
            if (i in 2..4) {
                e = mul(e, rotx)
                e = mul(e, rotz)
                e = mul(e, roty)
                freq *= TURB_EXP
                continue
            }

            // (pos * E).y in GLSL = dot(pos, column 1 of E)
            val dotY = posX * e[1] + posY * e[4] + posZ * e[7]
            val phase = freq * dotY + TURB_SPEED * t

            // pos += WAVE_AMP * E[0] * sin(phase) / freq
            val s = TURB_AMP * sin(phase) / freq
            posX += s * e[0]
            posY += s * e[3]
            posZ += s * e[6]

            e = mul(e, rotx)
            e = mul(e, rotz)
            e = mul(e, roty)
            freq *= TURB_EXP
        }

        return Triple(posX, posY, posZ)
    }

    private fun sphere(px: Double, py: Double, pz: Double): Double {
        val len = sqrt(px * px + py * py + pz * pz)
        val d = len - 3.0
        return if (d < 0.0) {
            -d * 0.7 + PASSTHROUGH
        } else {
            d * 0.7 + PASSTHROUGH * 2.0
        }
    }

    private fun identity() = doubleArrayOf(
        1.0, 0.0, 0.0,
        0.0, 1.0, 0.0,
        0.0, 0.0, 1.0
    )

    // Row-major 3x3 matrix multiply: result = a * b
    private fun mul(a: DoubleArray, b: DoubleArray): DoubleArray {
        val r = DoubleArray(9)
        for (row in 0..2) {
            for (col in 0..2) {
                r[row * 3 + col] =
                    a[row * 3 + 0] * b[0 * 3 + col] +
                    a[row * 3 + 1] * b[1 * 3 + col] +
                    a[row * 3 + 2] * b[2 * 3 + col]
            }
        }
        return r
    }

    companion object {
        private const val TURB_NUM = 10
        private const val TURB_AMP = 1.0
        private const val TURB_SPEED = 0.7
        private const val TURB_FREQ = 4.0
        private const val TURB_EXP = 1.5
        private const val PASSTHROUGH = 0.1
        private const val BRIGHTNESS = 0.0005
        private const val STEPS = 30
    }
}
