package dev.simonas.digitalsun.rpi

import kotlinx.cinterop.*
import platform.posix.*

/**
 * Example usage of rpi_ws281x wrapper for Raspberry Pi
 */
fun main() {
    println("rpi_ws281x LED Control Example")
    println("================================")

    // Configuration
    val ledCount = 60
    val gpioPin = 18 // GPIO 18 (PWM0)

    println("Initializing $ledCount LEDs on GPIO $gpioPin...")

    try {
        LedStrip(ledCount, gpioPin).use { leds ->
            println("Initialization successful!")

            // Example 1: Turn on first LED as red
            println("\nExample 1: First LED RED")
            leds.clear()
            leds[0] = Color.RED
            leds.show()
            sleep(2)

            // Example 2: Rainbow pattern
            println("Example 2: Rainbow pattern")
            leds.clear()
            leds[0] = Color.RED
            leds[1] = Color(255u, 127u, 0u) // Orange
            leds[2] = Color.YELLOW
            leds[3] = Color.GREEN
            leds[4] = Color.CYAN
            leds[5] = Color.BLUE
            leds[6] = Color.MAGENTA
            leds.show()
            sleep(2)

            // Example 3: Fill all LEDs with green
            println("Example 3: Fill all GREEN")
            leds.fill(Color.GREEN)
            leds.show()
            sleep(2)

            // Example 4: Breathing effect with red
            println("Example 4: Breathing effect")
            leds.fill(Color.RED)
            for (brightness in 255 downTo 0 step 5) {
                leds.setBrightness(brightness.toUByte())
                leds.show()
                usleep(20000) // 20ms
            }
            for (brightness in 0..255 step 5) {
                leds.setBrightness(brightness.toUByte())
                leds.show()
                usleep(20000)
            }

            // Example 5: Simple chase effect
            println("Example 5: Chase effect")
            leds.clear()
            leds.setBrightness(255u)
            for (cycle in 0..2) {
                for (i in 0 until minOf(10, ledCount)) {
                    leds[i] = Color.BLUE
                    leds.show()
                    if (i > 0) leds[i - 1] = Color.BLACK
                    usleep(100000) // 100ms
                }
            }

            // Clear at the end
            println("\nClearing all LEDs...")
            leds.clear()
            leds.show()
        }

        println("Done!")

    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * Sleep for seconds
 */
private fun sleep(seconds: Int) {
    platform.posix.sleep(seconds.toUInt())
}
