package dev.simonas.digitalsun.openrndr

import dev.simonas.digitalsun.core.Stages
import dev.simonas.digitalsun.core.shaders.ShaderFactory
import org.openrndr.KeyEvent
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.extra.fx.blur.GaussianBlur
import org.openrndr.extra.gui.GUI

fun main() = application {
    configure {
        width = 1800  // Increased width to accommodate GUI
        height = 1050
    }

    program {
        val stage = Stages.fromEnv()
        val pixels = stage.getPixels()
        val stageWidth = (pixels.maxOf { it.x } - pixels.minOf { it.x } + 1) * P_SIZE
        val stageHeight = (pixels.maxOf { it.y } - pixels.minOf { it.y } + 1) * P_SIZE
        val drawOffsetX = ((width - stageWidth) / 2 / P_SIZE).toInt() - pixels.minOf { it.x }
        val drawOffsetY = ((height - stageHeight) / 2 / P_SIZE).toInt() - pixels.minOf { it.y }

        val noiseGenerator = OpenrndrNoiseGenerator()
        val shaders = ShaderFactory.all(noiseGenerator)

        val shaderSelector = ShaderSelector(shaders.map { it.name })
        val params = Parameters()
        var activeIndex = shaderSelector.selectedIndex
        val shader = OpenrndrPixelShader(shaders[activeIndex].shader)

        val gui = GUI()
        gui.add(shaderSelector, "Shader")
        gui.add(params, "Noise Parameters")
        gui.visible = true

        // Keys 1–N switch shaders instantly
        keyboard.keyDown.listen { event: KeyEvent ->
            val index = event.name.toIntOrNull()?.minus(1)
            if (index != null && index in shaders.indices) {
                shaderSelector.selectedIndex = index
            }
        }

        extend(gui) {
            persistState = false
            compartmentsCollapsedByDefault = false
        }

        val offscreen = renderTarget(width, height) {
            colorBuffer()
            depthBuffer()
        }
        val blur = GaussianBlur().apply {
            window = 10
            gain = 4.0
            spread = 8.0
            sigma = 20.0
        }
        val blurred = colorBuffer(width, height)

        extend {
            // Swap shader if selection changed
            if (shaderSelector.selectedIndex != activeIndex) {
                activeIndex = shaderSelector.selectedIndex
                shader.coreShader = shaders[activeIndex].shader
            }

            offscreen.clearColor(0, ColorRGBa.BLACK)
            drawer.isolatedWithTarget(offscreen) {
                pixels.forEach { pixel ->
                    val color = shader.shade(pixel.x, pixel.y, seconds, params)
                    drawer.drawPixel(pixel.x + drawOffsetX, pixel.y + drawOffsetY, color)
                }
            }
            blur.apply(offscreen.colorBuffer(0), blurred)
            drawer.image(blurred)

            drawer.fill = ColorRGBa.WHITE
            val shaderLabels = shaders.mapIndexed { i, s -> "${i + 1}:${s.name}" }.joinToString("  ")
            drawer.text("[${shaders[activeIndex].name}]  $shaderLabels", 300.0, height - 60.0)
            drawer.text("Time: %.2f".format(seconds), 300.0, height - 40.0)
            drawer.text("FPS: %.1f".format(frameCount / seconds), 300.0, height - 20.0)
        }
    }
}

private const val P_SIZE = 20.0
private const val P_MARGIN = P_SIZE * 0.05

private fun Drawer.drawPixel(x: Int, y: Int, color: ColorRGBa) {
    fill = color
    rectangle(x * P_SIZE, y * P_SIZE, P_SIZE - P_MARGIN, P_SIZE - P_MARGIN)
}
