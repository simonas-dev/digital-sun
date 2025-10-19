package dev.simonas.digitalsun.core

import kotlin.math.floor

/**
 * HSV color representation
 * @param h Hue (0.0 - 1.0, wraps around)
 * @param s Saturation (0.0 - 1.0)
 * @param v Value/Brightness (0.0 - 1.0)
 */
data class HSVColor(
    val h: Double,
    val s: Double,
    val v: Double
) {
    /**
     * Convert HSV to RGB ColorValue
     */
    fun toRGB(): ColorValue {
        val hNorm = ((h % 1.0) + 1.0) % 1.0 // Normalize to 0-1 range
        val c = v * s
        val x = c * (1.0 - kotlin.math.abs((hNorm * 6.0) % 2.0 - 1.0))
        val m = v - c

        val (r1, g1, b1) = when (floor(hNorm * 6.0).toInt()) {
            0 -> Triple(c, x, 0.0)
            1 -> Triple(x, c, 0.0)
            2 -> Triple(0.0, c, x)
            3 -> Triple(0.0, x, c)
            4 -> Triple(x, 0.0, c)
            5 -> Triple(c, 0.0, x)
            else -> Triple(0.0, 0.0, 0.0)
        }

        return ColorValue(r1 + m, g1 + m, b1 + m, 1.0)
    }

    companion object {
        // Hue constants (0.0 - 1.0 range)
        const val HUE_RED = 0.0
        const val HUE_YELLOW = 0.166667  // 60°/360°
        const val HUE_GREEN = 0.333333   // 120°/360°
        const val HUE_CYAN = 0.5         // 180°/360°
        const val HUE_BLUE = 0.666667    // 240°/360°
        const val HUE_MAGENTA = 0.833333 // 300°/360°
    }
}
