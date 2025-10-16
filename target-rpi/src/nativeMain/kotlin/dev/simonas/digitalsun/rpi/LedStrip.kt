package dev.simonas.digitalsun.rpi

import dev.simonas.digitalsun.rpi.native.*
import kotlinx.cinterop.*

/**
 * Strip type constants matching ws2811.h definitions
 */
object StripType {
    const val WS2811_STRIP_RGB: Int = 0x00100800
    const val WS2811_STRIP_RBG: Int = 0x00100008
    const val WS2811_STRIP_GRB: Int = 0x00081000
    const val WS2811_STRIP_GBR: Int = 0x00080010
    const val WS2811_STRIP_BRG: Int = 0x00001008
    const val WS2811_STRIP_BGR: Int = 0x00000810

    const val SK6812_STRIP_RGBW: Int = 0x18100800
    const val SK6812_STRIP_RBGW: Int = 0x18100008
    const val SK6812_STRIP_GRBW: Int = 0x18081000
    const val SK6812_STRIP_GBRW: Int = 0x18080010
    const val SK6812_STRIP_BRGW: Int = 0x18001008
    const val SK6812_STRIP_BGRW: Int = 0x18000810
}

/**
 * RGB Color representation
 */
data class Color(val r: UByte, val g: UByte, val b: UByte, val w: UByte = 0u) {
    companion object {
        val BLACK = Color(0u, 0u, 0u)
        val RED = Color(255u, 0u, 0u)
        val GREEN = Color(0u, 255u, 0u)
        val BLUE = Color(0u, 0u, 255u)
        val WHITE = Color(255u, 255u, 255u)
        val YELLOW = Color(255u, 255u, 0u)
        val CYAN = Color(0u, 255u, 255u)
        val MAGENTA = Color(255u, 0u, 255u)
    }

    internal fun toNative(): UInt {
        return ws2811_color_rgbw(r.toByte(), g.toByte(), b.toByte(), w.toByte())
    }
}

/**
 * Kotlin wrapper for rpi_ws281x library
 */
class LedStrip(
    private val ledCount: Int,
    private val gpioPin: Int = 18,
    private val stripType: Int = StripType.WS2811_STRIP_GRB,
    private val brightness: UByte = 255u,
    private val frequency: UInt = 800000u,
    private val dmaChannel: Int = 10
) : AutoCloseable {

    private val ws2811: CPointer<ws2811_t>
    private val channel: Int = 0

    init {
        ws2811 = nativeHeap.alloc<ws2811_t>().ptr

        // Initialize the structure
        memScoped {
            ws2811.pointed.freq = frequency
            ws2811.pointed.dmanum = dmaChannel

            // Configure channel 0
            ws2811.pointed.channel[0].gpionum = gpioPin
            ws2811.pointed.channel[0].count = ledCount
            ws2811.pointed.channel[0].invert = 0
            ws2811.pointed.channel[0].brightness = brightness.toByte()
            ws2811.pointed.channel[0].strip_type = stripType

            // Channel 1 unused
            ws2811.pointed.channel[1].gpionum = 0
            ws2811.pointed.channel[1].count = 0
            ws2811.pointed.channel[1].invert = 0
            ws2811.pointed.channel[1].brightness = 0
        }

        // Initialize the library
        val result = ws2811_init(ws2811)
        if (result != WS2811_SUCCESS) {
            val errorMsg = ws2811_get_return_t_str(result)?.toKString() ?: "Unknown error"
            throw RuntimeException("Failed to initialize WS2811: $errorMsg")
        }
    }

    /**
     * Set the color of a specific LED
     */
    operator fun set(index: Int, color: Color) {
        if (index < 0 || index >= ledCount) {
            throw IndexOutOfBoundsException("LED index $index out of bounds (0..$ledCount)")
        }
        ws2811_set_led_color(ws2811, channel, index, color.toNative())
    }

    /**
     * Get the color of a specific LED
     */
    operator fun get(index: Int): Color {
        if (index < 0 || index >= ledCount) {
            throw IndexOutOfBoundsException("LED index $index out of bounds (0..$ledCount)")
        }
        val native = ws2811_get_led_color(ws2811, channel, index)
        return Color(
            r = ((native shr 16) and 0xFFu).toUByte(),
            g = ((native shr 8) and 0xFFu).toUByte(),
            b = (native and 0xFFu).toUByte(),
            w = ((native shr 24) and 0xFFu).toUByte()
        )
    }

    /**
     * Update the LED strip with current color values
     */
    fun show() {
        val result = ws2811_render(ws2811)
        if (result != WS2811_SUCCESS) {
            val errorMsg = ws2811_get_return_t_str(result)?.toKString() ?: "Unknown error"
            throw RuntimeException("Failed to render: $errorMsg")
        }
    }

    /**
     * Fill all LEDs with the same color
     */
    fun fill(color: Color) {
        for (i in 0 until ledCount) {
            this[i] = color
        }
    }

    /**
     * Clear all LEDs (set to black)
     */
    fun clear() {
        fill(Color.BLACK)
    }

    /**
     * Set brightness (0-255)
     */
    fun setBrightness(brightness: UByte) {
        ws2811.pointed.channel[channel].brightness = brightness.toByte()
    }

    override fun close() {
        ws2811_fini(ws2811)
        nativeHeap.free(ws2811)
    }
}
