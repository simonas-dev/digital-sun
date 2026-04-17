package dev.simonas.digitalsun.openrndr

import dev.simonas.digitalsun.core.ColorValue
import dev.simonas.digitalsun.core.ShaderFactory
import dev.simonas.digitalsun.core.ShaderParameters
import dev.simonas.digitalsun.core.ShaderPreset
import dev.simonas.digitalsun.core.Stages
import dev.simonas.digitalsun.core.shaders.WarmColorShaderAlgorithm
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
        val presets = ShaderFactory.allPresets()

        val shaderSelector = ShaderSelector(presets.map { it.name })
        val params = Parameters()
        var activeIndex = shaderSelector.selectedIndex
        params.applyPreset(presets[activeIndex])

        // Single shader instance — preset selection writes into `params`
        val shader = WarmColorShaderAlgorithm(noiseGenerator) { params.toShaderParameters() }

        val gui = GUI()
        gui.add(shaderSelector, "Shader")
        gui.add(params, "Noise Parameters")
        gui.visible = true

        // Keys 1–N switch shaders instantly, n/p for next/prev
        keyboard.keyDown.listen { event: KeyEvent ->
            when (event.name) {
                "n" -> {
                    shaderSelector.selectedIndex = (shaderSelector.selectedIndex + 1) % presets.size
                }
                "p" -> {
                    shaderSelector.selectedIndex = (shaderSelector.selectedIndex - 1 + presets.size) % presets.size
                }
                else -> {
                    val index = event.name.toIntOrNull()?.minus(1)
                    if (index != null && index in presets.indices) {
                        shaderSelector.selectedIndex = index
                    }
                }
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
            // Apply preset to GUI params if selection changed
            if (shaderSelector.selectedIndex != activeIndex) {
                activeIndex = shaderSelector.selectedIndex
                params.applyPreset(presets[activeIndex])
            }

            offscreen.clearColor(0, ColorRGBa.BLACK)
            drawer.isolatedWithTarget(offscreen) {
                pixels.forEach { pixel ->
                    val color = shader.shade(pixel.x, pixel.y, seconds).toOpenrndrColor()
                    drawer.drawPixel(pixel.x + drawOffsetX, pixel.y + drawOffsetY, color)
                }
            }
            blur.apply(offscreen.colorBuffer(0), blurred)
            drawer.image(blurred)

            drawer.fill = ColorRGBa.WHITE
            val shaderLabels = presets.mapIndexed { i, p -> "${i + 1}:${p.name}" }.joinToString("  ")
            drawer.text("[${presets[activeIndex].name}]  $shaderLabels", 300.0, height - 60.0)
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

private fun ColorValue.toOpenrndrColor(): ColorRGBa = ColorRGBa(r, g, b, a)

private fun Parameters.applyPreset(preset: ShaderPreset) {
    val p = preset.params
    seed = p.seed
    spatialScale = p.spatialScale
    timeScale = p.timeScale
    noiseType = p.noiseType
    alphaPower = p.alphaPower
    alphaMin = p.alphaMin
    alphaMax = p.alphaMax
    fbmOctaves = p.fbmOctaves
    fbmLacunarity = p.fbmLacunarity
    fbmGain = p.fbmGain
}
