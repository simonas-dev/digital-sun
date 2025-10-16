package dev.simonas.digitalsun.rpi

import kotlin.system.exitProcess

fun main() {
    println("rpi_ws281x LED Control")
    println("======================")

    val ledCount = 60
    val gpioPin = 18

    println("Initializing $ledCount LEDs on GPIO $gpioPin...")

    try {
        LedStrip(ledCount, gpioPin).use { leds ->
            println("Initialization successful!")

            // Example 1: First LED red
            println("\nExample 1: First LED RED")
            leds.clear()
            leds[0] = Color.RED
            leds.show()
            Thread.sleep(2000)

            // Example 2: Rainbow
            println("Example 2: Rainbow pattern")
            leds.clear()
            leds[0] = Color.RED
            leds[1] = Color(255u, 127u, 0u)
            leds[2] = Color.YELLOW
            leds[3] = Color.GREEN
            leds[4] = Color.CYAN
            leds[5] = Color.BLUE
            leds[6] = Color.MAGENTA
            leds.show()
            Thread.sleep(2000)

            // Example 3: Fill green
            println("Example 3: Fill all GREEN")
            leds.fill(Color.GREEN)
            leds.show()
            Thread.sleep(2000)

            // Example 4: Breathing
            println("Example 4: Breathing effect")
            leds.fill(Color.RED)
            for (brightness in 255 downTo 0 step 5) {
                leds.setBrightness(brightness.toUByte())
                leds.show()
                Thread.sleep(20)
            }
            for (brightness in 0..255 step 5) {
                leds.setBrightness(brightness.toUByte())
                leds.show()
                Thread.sleep(20)
            }

            // Example 5: Chase
            println("Example 5: Chase effect")
            leds.clear()
            leds.setBrightness(255u)
            repeat(3) {
                for (i in 0 until minOf(10, ledCount)) {
                    leds[i] = Color.BLUE
                    leds.show()
                    if (i > 0) leds[i - 1] = Color.BLACK
                    Thread.sleep(100)
                }
            }

            println("\nClearing all LEDs...")
            leds.clear()
            leds.show()
        }

        println("Done!")
    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
        exitProcess(1)
    }
}
