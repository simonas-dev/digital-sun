package dev.simonas.digitalsun.rpi

import dev.simonas.digitalsun.core.PixelShader
import dev.simonas.digitalsun.core.ShaderParameters
import dev.simonas.digitalsun.core.Stages
import dev.simonas.digitalsun.core.shaders.NamedShader
import dev.simonas.digitalsun.core.shaders.ShaderFactory
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.system.exitProcess
import kotlin.system.measureNanoTime

fun main() {
    try {
        start()
    } catch (e: Exception) {
        main()
    }
}

private fun printShaderMenu(shaders: List<NamedShader>, current: String) {
    println()
    println("Shaders:")
    shaders.forEachIndexed { i, named ->
        val marker = if (named.name == current) " <--" else ""
        println("  ${i + 1}) ${named.name}$marker")
    }
    println()
    println("Press 1-${shaders.size}, n(ext), or p(rev) to switch shader:")
}

fun start() {
    println("Digital Sun - RPI LED Control")
    println("==============================")

    val hw = HardwareProfiles.hwForStage(Stages.fromEnv())
    println("Hardware profile: ${hw.name}")

    val pixels = hw.wiring(hw.stage.getPixels())
    val ledCount = pixels.size
    val gpioPin = hw.gpioPin

    println("Initializing $ledCount LEDs on GPIO $gpioPin...")

    val ledStrip = LedStrip(ledCount, gpioPin, brightness = 255u)
    ledStrip.clear()

    // Scope for all animation coroutines - we'll cancel this on shutdown
    val animationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Single shutdown hook handles ALL cleanup - invoked on SIGTERM, SIGINT, or normal exit
    Runtime.getRuntime().addShutdownHook(Thread {
        println("\nShutdown signal received, cleaning up...")
        try {
            // Restore terminal to normal mode
            ProcessBuilder("stty", "icanon", "echo").inheritIO().start().waitFor()

            // CRITICAL: Cancel coroutines FIRST to stop ledStrip access
            animationScope.cancel()
            Thread.sleep(150) // Give coroutines time to finish their current iteration

            // Now safe to access hardware - no concurrent access
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

    val noiseGenerator = RpiNoiseGenerator()
    val shaders = ShaderFactory.all(noiseGenerator)

    val initialShaderName = System.getenv("SHADER")?.lowercase() ?: "warm"
    val initialShader = shaders.firstOrNull { it.name == initialShaderName } ?: shaders.first()
    val currentShader = AtomicReference<PixelShader>(initialShader.shader)
    val currentShaderName = AtomicReference(initialShader.name)
    val params = ShaderParameters()

    println("Using shader: ${currentShader.get().javaClass.simpleName}")
    printShaderMenu(shaders, currentShaderName.get())

    println("Starting animation loop (Ctrl+C to stop)...")
    println("Parameters: seed=${params.seed}, spatialScale=${params.spatialScale}, timeScale=${params.timeScale}")

    val startTime = System.currentTimeMillis()

    runBlocking {
        val sharedLeds = createArrayOfSize(ledCount, Color(0.toUByte(), 0.toUByte(), 0.toUByte()))

        // FPS tracking for shader coroutine
        var shaderFrameCount = 0L
        var shaderLastReport = System.currentTimeMillis()

        // FPS tracking for render coroutine
        var renderFrameCount = 0L
        var renderLastReport = System.currentTimeMillis()

        // Stdin listener coroutine - reads single key presses to switch shaders
        animationScope.launch(Dispatchers.IO) {
            // Set terminal to raw mode using ProcessBuilder.inheritIO() so stty
            // operates on the actual parent terminal (not a child subprocess's stdin)
            try {
                ProcessBuilder("stty", "-icanon", "-echo")
                    .inheritIO()
                    .start()
                    .waitFor()
            } catch (_: Exception) { }

            val stream = System.`in`
            while (isActive) {
                val byte = stream.read()
                if (byte == -1) break
                val ch = byte.toChar().lowercaseChar()
                val index = when (ch) {
                    'n' -> {
                        val current = shaders.indexOfFirst { it.name == currentShaderName.get() }
                        (current + 1) % shaders.size
                    }
                    'p' -> {
                        val current = shaders.indexOfFirst { it.name == currentShaderName.get() }
                        (current - 1 + shaders.size) % shaders.size
                    }
                    else -> ch.digitToIntOrNull()?.minus(1)
                }
                if (index != null && index in shaders.indices) {
                    val named = shaders[index]
                    currentShader.set(named.shader)
                    currentShaderName.set(named.name)
                    println("Switched to shader: ${named.shader.javaClass.simpleName}")
                    printShaderMenu(shaders, currentShaderName.get())
                }
            }
        }

        // Shader coroutine - calculates pixel colors
        animationScope.launch {
            while (isActive) {
                val t = (System.currentTimeMillis() - startTime) / 1000.0
                val shader = currentShader.get()

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

                // Report shader FPS on 1000 frames
                if (shaderFrameCount == 1000L) {
                    val now = System.currentTimeMillis()
                    val elapsed = (now - shaderLastReport) / 1000.0
                    val fps = 1000.0 / elapsed
                    println("[Shader] FPS: %.1f, Frame: %d".format(fps, shaderFrameCount))
                    shaderLastReport = now
                }
                delay(2)
            }
        }

        // Render coroutine - updates LED strip
        animationScope.launch {
            while (isActive) {
                sharedLeds.forEachIndexed { index, pixel ->
                    ledStrip[index] = pixel
                }
                ledStrip.show()

                renderFrameCount++

                // Report render FPS on 1000 frames
                if (renderFrameCount == 1000L) {
                    val now = System.currentTimeMillis()
                    val elapsed = (now - renderLastReport) / 1000.0
                    val fps = 1000.0 / elapsed
                    println("[Render] FPS: %.1f, Frame: %d".format(fps, renderFrameCount))
                    renderLastReport = now
                }
            }
        }

        // Wait for animation scope to complete (happens when shutdown hook cancels it)
        animationScope.coroutineContext[Job]?.join()
    }
}

private inline fun <reified T> createArrayOfSize(size: Int, value: T): Array<T> {
    return (1..size).map { value }.toTypedArray()
}
