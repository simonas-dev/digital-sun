package dev.simonas.digitalsun.rpi

import dev.simonas.digitalsun.core.ShaderFactory
import dev.simonas.digitalsun.core.Stages
import dev.simonas.digitalsun.core.shaders.WarmColorShaderAlgorithm
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.LinkedBlockingQueue

fun main() {
    try {
        start()
    } catch (e: Exception) {
        main()
    }
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
        try {
            // Restore terminal in case Mosaic didn't get to clean up
            ProcessBuilder("stty", "icanon", "echo").inheritIO().start().waitFor()

            // CRITICAL: Cancel coroutines FIRST to stop ledStrip access
            animationScope.cancel()
            Thread.sleep(150) // Give coroutines time to finish their current iteration

            // Now safe to access hardware - no concurrent access
            ledStrip.clear()
            ledStrip.show()
            ledStrip.close()
        } catch (e: Exception) {
            System.err.println("Error during cleanup: ${e.message}")
        }
    })

    val noiseGenerator = RpiNoiseGenerator()
    val presets = ShaderFactory.allPresets()

    val initialPresetName = System.getenv("SHADER")?.lowercase() ?: "warm_classic"
    val initialPreset = presets.firstOrNull { it.name == initialPresetName } ?: presets.first()
    val currentParams = AtomicReference(initialPreset.params)
    val currentPresetName = AtomicReference(initialPreset.name)
    val shaderFpsRef = AtomicReference(0.0)
    val renderFpsRef = AtomicReference(0.0)

    // Single shader instance — TUI/web swap params, not the shader itself
    val shader = WarmColorShaderAlgorithm(noiseGenerator) { currentParams.get() }

    val startTime = System.currentTimeMillis()

    // Double buffer: shader writes into a free buffer, hands it to render via the queue.
    // Render returns the consumed buffer back so shader can reuse it (avoids allocation).
    val readyFrame = LinkedBlockingQueue<Array<Color>>(1)
    val freeBuffer = LinkedBlockingQueue<Array<Color>>(1)
    freeBuffer.put(Array(ledCount) { Color(0.toUByte(), 0.toUByte(), 0.toUByte()) })

    // Shader coroutine - calculates pixel colors into a free buffer, then hands it to render
    animationScope.launch {
        var frames = 0L
        var lastReport = System.currentTimeMillis()
        while (isActive) {
            val buf = freeBuffer.poll() ?: continue
            val t = (System.currentTimeMillis() - startTime) / 1000.0

            pixels.forEachIndexed { index, pixel ->
                if (index < ledCount) {
                    val c = shader.shade(pixel.x, pixel.y, t)
                    val r = (c.r * c.a * 255).toInt().coerceIn(0, 255).toUByte()
                    val g = (c.g * c.a * 255).toInt().coerceIn(0, 255).toUByte()
                    val b = (c.b * c.a * 255).toInt().coerceIn(0, 255).toUByte()
                    buf[index] = Color(r, g, b)
                }
            }

            readyFrame.put(buf)

            frames++
            if (frames % 60L == 0L) {
                val now = System.currentTimeMillis()
                val elapsed = (now - lastReport) / 1000.0
                if (elapsed > 0) shaderFpsRef.set(60.0 / elapsed)
                lastReport = now
            }
        }
    }

    // Render coroutine - blocks until shader has a complete frame ready, then sends to LEDs
    animationScope.launch {
        var frames = 0L
        var lastReport = System.currentTimeMillis()
        while (isActive) {
            val buf = readyFrame.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS) ?: continue
            buf.forEachIndexed { index, pixel -> ledStrip[index] = pixel }
            ledStrip.show()
            freeBuffer.put(buf)

            frames++
            if (frames % 60L == 0L) {
                val now = System.currentTimeMillis()
                val elapsed = (now - lastReport) / 1000.0
                if (elapsed > 0) renderFpsRef.set(60.0 / elapsed)
                lastReport = now
            }
        }
    }

    // TUI takes over the terminal until 'q' or signal
    runTui(presets, currentParams, currentPresetName, shaderFpsRef, renderFpsRef)
}
