package dev.simonas.digitalsun.core

/**
 * Generic pixel shader interface that can be implemented for different platforms
 */
interface PixelShader {
    /**
     * Shade a pixel at given coordinates and time
     * @param x X coordinate
     * @param y Y coordinate
     * @param t Time in seconds
     * @param params Shader parameters
     * @return Color value for the pixel
     */
    fun shade(x: Int, y: Int, t: Double, params: ShaderParameters): ColorValue
}
