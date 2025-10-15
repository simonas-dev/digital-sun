import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.noise.fbm
import org.openrndr.extra.noise.perlin
import org.openrndr.extra.noise.perlinLinear
import org.openrndr.extra.parameters.BooleanParameter
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.IntParameter
import org.openrndr.extra.parameters.OptionParameter
import kotlin.math.abs
import kotlin.math.pow

const val verticalMargin = 8
const val verticalOffset = 6
const val horizontalMargin = 5

val pixels = mutableListOf<Pixel>().apply {
    addRow(y = 0 * verticalMargin + verticalOffset, fromX = 30 +  2 + horizontalMargin * 3, toX = 30 + 50 - horizontalMargin * 3)
    addRow(y = 1 * verticalMargin + verticalOffset, fromX = 30 +  2 + horizontalMargin * 1, toX = 30 + 50 - horizontalMargin * 1)
    addRow(y = 2 * verticalMargin + verticalOffset, fromX = 30 +  2, toX = 30 + 50)
    addRow(y = 3 * verticalMargin + verticalOffset, fromX = 30 +  2, toX = 30 + 50)
    addRow(y = 4 * verticalMargin + verticalOffset, fromX = 30 +  2 + horizontalMargin * 1, toX = 30 + 50 - horizontalMargin * 1)
    addRow(y = 5 * verticalMargin + verticalOffset, fromX = 30 +  2 + horizontalMargin * 3, toX = 30 + 50 - horizontalMargin * 3)
}

// Noise function enum for selection
enum class NoiseType {
    PERLIN,
    FBM_PERLIN,
    FBM_PERLIN_LINEAR
}

// Alpha mapping enum
enum class AlphaMapping {
    ABSOLUTE,
    NORMALIZED,
    NORMALIZED_POW
}

// GUI Parameters class
class NoiseParameters {
    @IntParameter("Seed", 1, 1000)
    var seed: Int = 100

    @DoubleParameter("Spatial Scale", 0.01, 2.0)
    var spatialScale: Double = 0.15

    @DoubleParameter("Time Scale", 0.0, 2.0)
    var timeScale: Double = 0.55

    @OptionParameter("Noise Type", order = 0)
    var noiseType: NoiseType = NoiseType.PERLIN

    @OptionParameter("Alpha Mapping", order = 1)
    var alphaMapping: AlphaMapping = AlphaMapping.NORMALIZED_POW

    @DoubleParameter("Alpha Power", 0.1, 5.0)
    var alphaPower: Double = 3.0

    @DoubleParameter("Alpha Min", 0.0, 1.0)
    var alphaMin: Double = 0.0

    @DoubleParameter("Alpha Max", 0.0, 1.0)
    var alphaMax: Double = 1.0

    // FBM specific parameters
    @IntParameter("FBM Octaves", 1, 8)
    var fbmOctaves: Int = 4

    @DoubleParameter("FBM Lacunarity", 1.0, 4.0)
    var fbmLacunarity: Double = 2.0

    @DoubleParameter("FBM Gain", 0.1, 1.0)
    var fbmGain: Double = 0.5

    @BooleanParameter("Animate")
    var animate: Boolean = true

    @BooleanParameter("Show Debug Info")
    var showDebug: Boolean = false
}

data class Pixel(val x: Int, val y: Int)

fun main() = application {
    configure {
        width = 1800  // Increased width to accommodate GUI
        height = 1050
    }

    program {
        val gui = GUI()
        val params = NoiseParameters()

        gui.add(params, "Noise Parameters")
        gui.visible = true

        extend(gui) {
            persistState = false
            compartmentsCollapsedByDefault = false
        }

        extend {
            pixels.forEach { pixel ->
                val timeValue = if (params.animate) seconds else 0.0

                // Calculate noise based on selected type
                val noise = when (params.noiseType) {
                    NoiseType.PERLIN -> {
                        perlin(
                            params.seed,
                            pixel.x.toDouble() * params.spatialScale,
                            pixel.y * params.spatialScale,
                            timeValue * params.timeScale
                        )
                    }
                    NoiseType.FBM_PERLIN -> {
                        fbm(
                            params.seed,
                            pixel.x.toDouble() * params.spatialScale,
                            pixel.y * params.spatialScale,
                            timeValue * params.timeScale,
                            ::perlin,
                            params.fbmOctaves,
                            params.fbmLacunarity,
                            params.fbmGain
                        )
                    }
                    NoiseType.FBM_PERLIN_LINEAR -> {
                        fbm(
                            params.seed,
                            pixel.x.toDouble() * params.spatialScale,
                            pixel.y * params.spatialScale,
                            timeValue * params.timeScale,
                            ::perlinLinear,
                            params.fbmOctaves,
                            params.fbmLacunarity,
                            params.fbmGain
                        )
                    }
                }

                // Calculate alpha based on selected mapping
                val alpha = when (params.alphaMapping) {
                    AlphaMapping.ABSOLUTE -> {
                        abs(noise) * 0.98 + 0.02
                    }
                    AlphaMapping.NORMALIZED -> {
                        (1.0 + noise) / 2.0
                    }
                    AlphaMapping.NORMALIZED_POW -> {
                        ((1.0 + noise) / 2.0).pow(params.alphaPower)
                    }
                }.coerceIn(params.alphaMin, params.alphaMax)

                drawer.drawPixel(pixel.x, pixel.y, ColorRGBa.RED.copy(alpha = alpha))
            }

            // Show debug information
            if (params.showDebug) {
                drawer.fill = ColorRGBa.WHITE
                drawer.text("Time: %.2f".format(seconds), 20.0, height - 40.0)
                drawer.text("FPS: %.1f".format(frameCount / seconds), 20.0, height - 20.0)
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
    rectangle(x * P_SIZE, y * P_SIZE, P_SIZE - P_MARGIN, P_SIZE - P_MARGIN)
}