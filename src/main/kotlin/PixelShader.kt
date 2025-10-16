import org.openrndr.color.ColorRGBa

interface PixelShader<Params> {
    fun shade(x: Int, y: Int, t: Double, params: Params): ColorRGBa
}