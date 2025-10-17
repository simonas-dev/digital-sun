package dev.simonas.digitalsun.rpi

import dev.simonas.digitalsun.core.ShaderParameters
import dev.simonas.digitalsun.core.Stage
import dev.simonas.digitalsun.core.V1RedShaderAlgorithm
import kotlinx.coroutines.*
import kotlin.system.exitProcess

fun main() {
    println("Digital Sun - RPI LED Control")
    println("==============================")

    val stage = Stage()
    val pixels = stage.getPixels()
    val ledCount = pixels.size
    val gpioPin = 18

    println("Initializing $ledCount LEDs on GPIO $gpioPin...")

    val ledStrip = LedStrip(ledCount, gpioPin, brightness = 128u)

    // Single shutdown hook handles ALL cleanup - invoked on SIGTERM, SIGINT, or normal exit
    Runtime.getRuntime().addShutdownHook(Thread {
        println("\nShutdown signal received, cleaning up...")
        try {
            ledStrip.clear()
            ledStrip.show()
            ledStrip.close()
            println("LEDs cleared and connection closed")
        } catch (e: Exception) {
            System.err.println("Error during cleanup: ${e.message}")
        }
    })

    println("Initialization successful!")
    println("Stage initialized with ${pixels.size} pixels")

    // Create shader with RPI noise generator
    val noiseGenerator = RpiNoiseGenerator()
    val shader = V1RedShaderAlgorithm(noiseGenerator)
    val params = ShaderParameters()

    println("Starting animation loop (Ctrl+C to stop)...")
    println("Parameters: seed=${params.seed}, spatialScale=${params.spatialScale}, timeScale=${params.timeScale}")

    val startTime = System.currentTimeMillis()

    runBlocking(Dispatchers.Default) {
        val sharedLeds = createArrayOfSize(ledCount, Color(0.toUByte(), 0.toUByte(), 0.toUByte()))

        // FPS tracking for shader coroutine
        var shaderFrameCount = 0L
        var shaderLastReport = System.currentTimeMillis()

        // FPS tracking for render coroutine
        var renderFrameCount = 0L
        var renderLastReport = System.currentTimeMillis()

        // Shader coroutine - calculates pixel colors
        launch {
            while (isActive) {
                val t = (System.currentTimeMillis() - startTime) / 1000.0

                // Shade each pixel using the core shader algorithm
                pixels.forEachIndexed { index, pixel ->
                    if (index < ledCount) {
                        val colorValue = shader.shade(pixel.x, pixel.y, t, params)

                        // Convert ColorValue (0.0-1.0) to Color (0-255)
                        val r = (colorValue.r * colorValue.a * 255).toInt().coerceIn(0, 255).toUByte()
                        val g = (colorValue.g * colorValue.a * 255).toInt().coerceIn(0, 255).toUByte()
                        val b = (colorValue.b * colorValue.a * 255).toInt().coerceIn(0, 255).toUByte()

                        sharedLeds[index] = Color(r, g, b)
                    }
                }

                shaderFrameCount++

                // Report shader FPS every 100 frames
                if (shaderFrameCount % 100 == 0L) {
                    val now = System.currentTimeMillis()
                    val elapsed = (now - shaderLastReport) / 1000.0
                    val fps = 100.0 / elapsed
                    println("[Shader] FPS: %.1f, Frame: %d".format(fps, shaderFrameCount))
                    shaderLastReport = now
                }
                delay(2)
            }
        }

        // Render coroutine - updates LED strip
        launch {
            while (isActive) {
                sharedLeds.forEachIndexed { index, pixel ->
                    ledStrip[index] = pixel
                }
                ledStrip.show()

                renderFrameCount++

                // Report render FPS every 100 frames
                if (renderFrameCount % 100 == 0L) {
                    val now = System.currentTimeMillis()
                    val elapsed = (now - renderLastReport) / 1000.0
                    val fps = 100.0 / elapsed
                    println("[Render] FPS: %.1f, Frame: %d".format(fps, renderFrameCount))
                    renderLastReport = now
                }
                delay(16)
            }
        }
    }
}

private inline fun <reified T> createArrayOfSize(size: Int, value: T): Array<T> {
    return (1..size).map { value }.toTypedArray()
}
