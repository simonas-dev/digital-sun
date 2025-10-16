package dev.simonas.digitalsun.rpi

import dev.simonas.digitalsun.core.ShaderParameters
import dev.simonas.digitalsun.core.Stage
import dev.simonas.digitalsun.core.V1RedShaderAlgorithm
import kotlin.system.exitProcess

fun main() {
    println("Digital Sun - RPI LED Control")
    println("==============================")

    val stage = Stage()
    val pixels = stage.getPixels()
    val ledCount = pixels.size()
    val gpioPin = 18

    println("Initializing $ledCount LEDs on GPIO $gpioPin...")

    try {
        LedStrip(ledCount, gpioPin, brightness = 128u).use { leds ->
            println("Initialization successful!")

            println("Stage initialized with ${pixels.size} pixels")

            // Create shader with RPI noise generator
            val noiseGenerator = RpiNoiseGenerator()
            val shader = V1RedShaderAlgorithm(noiseGenerator)
            val params = ShaderParameters()

            println("Starting animation loop (Ctrl+C to stop)...")
            println("Parameters: seed=${params.seed}, spatialScale=${params.spatialScale}, timeScale=${params.timeScale}")

            val startTime = System.currentTimeMillis()
            var frameCount = 0

            while (true) {
                val t = (System.currentTimeMillis() - startTime) / 1000.0

                // Shade each pixel using the core shader algorithm
                pixels.forEachIndexed { index, pixel ->
                    if (index < ledCount) {
                        val colorValue = shader.shade(pixel.x, pixel.y, t, params)

                        // Convert ColorValue (0.0-1.0) to Color (0-255)
                        val r = (colorValue.r * colorValue.a * 255).toInt().coerceIn(0, 255).toUByte()
                        val g = (colorValue.g * colorValue.a * 255).toInt().coerceIn(0, 255).toUByte()
                        val b = (colorValue.b * colorValue.a * 255).toInt().coerceIn(0, 255).toUByte()

                        leds[index] = Color(r, g, b)
                    }
                }

                leds.show()
                frameCount++

                // Print FPS every 100 frames
                if (frameCount % 100 == 0) {
                    val fps = frameCount / t
                    println("Time: %.2fs, FPS: %.1f, Frame: %d".format(t, fps, frameCount))
                }

                // Target ~60 FPS
                Thread.sleep(16)
            }
        }
    } catch (e: InterruptedException) {
        println("\nShutting down gracefully...")
    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
        exitProcess(1)
    }
}
