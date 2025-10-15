import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.extra.noise.fbm
import org.openrndr.extra.noise.perlin
import org.openrndr.extra.noise.perlinLinear
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.pow

const val verticalMargin = 8;
const val verticalOffset = 6;
const val horizontalMargin = 5;

val pixels = mutableListOf<Pixel>().apply {
    addRow(y = 0 * verticalMargin + verticalOffset, fromX = 2 + horizontalMargin * 3, toX = 50 - horizontalMargin * 3)
    addRow(y = 1 * verticalMargin + verticalOffset, fromX = 2 + horizontalMargin * 1, toX = 50 - horizontalMargin * 1)
    addRow(y = 2 * verticalMargin + verticalOffset, fromX = 2, toX = 50)
    addRow(y = 3 * verticalMargin + verticalOffset, fromX = 2, toX = 50)
    addRow(y = 4 * verticalMargin + verticalOffset, fromX = 2 + horizontalMargin * 1, toX = 50 - horizontalMargin * 1)
    addRow(y = 5 * verticalMargin + verticalOffset, fromX = 2 + horizontalMargin * 3, toX = 50 - horizontalMargin * 3)
}

fun main() = application {
    configure {
        width = 1050
        height = 1050
    }

    program {
        extend {
            pixels.forEach { pixel ->
                val space = 0.15
                val timeSpace = 0.55
//                val noise = fbm(100, pixel.x.toDouble() * space, pixel.y * space, seconds * timeSpace, ::perlinLinear)
                val noise = perlin(100, pixel.x.toDouble() * space, pixel.y * space, seconds * timeSpace)
//                drawer.drawPixel(pixel.x, pixel.y, ColorRGBa.RED.copy(alpha = (abs(noise) * 0.98) + 0.02))

                drawer.drawPixel(pixel.x, pixel.y, ColorRGBa.RED.copy(alpha = ((1.0 + noise) / 2.0).pow(3.0)))
            }
        }
    }
}



private fun MutableList<Pixel>.addRow(y: Int, fromX: Int, toX: Int) {
    for (x in fromX until toX) {
        add(Pixel(x, y))
    }
}

const val P_SIZE = 20.0
const val P_MARGIN = P_SIZE * 0.05

private fun Drawer.drawPixel(x: Int, y: Int, color: ColorRGBa) {
    fill = color
    rectangle(x * P_SIZE, y * P_SIZE, P_SIZE-P_MARGIN, P_SIZE-P_MARGIN)
}

