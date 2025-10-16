package dev.simonas.digitalsun.core

/**
 * Generic RGBA color representation without OPENRNDR dependencies
 */
data class ColorValue(
    val r: Double,
    val g: Double,
    val b: Double,
    val a: Double = 1.0
) {
    companion object {
        val RED = ColorValue(1.0, 0.0, 0.0, 1.0)
        val GREEN = ColorValue(0.0, 1.0, 0.0, 1.0)
        val BLUE = ColorValue(0.0, 0.0, 1.0, 1.0)
        val WHITE = ColorValue(1.0, 1.0, 1.0, 1.0)
        val BLACK = ColorValue(0.0, 0.0, 0.0, 1.0)
    }
}
