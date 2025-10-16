package dev.simonas.digitalsun.rpi

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure

/**
 * JNA bindings for rpi_ws281x library
 */
interface WS2811Library : Library {
    companion object {
        val INSTANCE: WS2811Library = Native.load("ws2811", WS2811Library::class.java)

        const val RPI_PWM_CHANNELS = 2
        const val WS2811_TARGET_FREQ = 800000
        const val WS2811_SUCCESS = 0
    }

    fun ws2811_init(ws2811: WS2811T): Int
    fun ws2811_fini(ws2811: WS2811T)
    fun ws2811_render(ws2811: WS2811T): Int
    fun ws2811_wait(ws2811: WS2811T): Int
    fun ws2811_get_return_t_str(state: Int): String?

    @Structure.FieldOrder("gpionum", "invert", "count", "strip_type", "leds", "brightness",
                          "wshift", "rshift", "gshift", "bshift", "gamma")
    class WS2811ChannelT : Structure() {
        @JvmField var gpionum: Int = 0                // int
        @JvmField var invert: Int = 0                 // int
        @JvmField var count: Int = 0                  // int
        @JvmField var strip_type: Int = 0             // int
        @JvmField var leds: Pointer? = null           // ws2811_led_t*
        @JvmField var brightness: Byte = 0            // uint8_t
        @JvmField var wshift: Byte = 0                // uint8_t
        @JvmField var rshift: Byte = 0                // uint8_t
        @JvmField var gshift: Byte = 0                // uint8_t
        @JvmField var bshift: Byte = 0                // uint8_t
        @JvmField var gamma: Pointer? = null          // uint8_t*
    }

    @Structure.FieldOrder("render_wait_time", "device", "rpi_hw", "freq", "dmanum", "channel")
    class WS2811T : Structure() {
        @JvmField var render_wait_time: Long = 0      // uint64_t
        @JvmField var device: Pointer? = null         // struct ws2811_device*
        @JvmField var rpi_hw: Pointer? = null         // const rpi_hw_t*
        @JvmField var freq: Int = 0                   // uint32_t (int in JNA for 32-bit)
        @JvmField var dmanum: Int = 0                 // int
        @JvmField var channel: Array<WS2811ChannelT> = arrayOf(WS2811ChannelT(), WS2811ChannelT())
    }
}

/**
 * Strip type constants
 */
object StripType {
    const val WS2811_STRIP_RGB = 0x00100800
    const val WS2811_STRIP_RBG = 0x00100008
    const val WS2811_STRIP_GRB = 0x00081000
    const val WS2811_STRIP_GBR = 0x00080010
    const val WS2811_STRIP_BRG = 0x00001008
    const val WS2811_STRIP_BGR = 0x00000810

    const val SK6812_STRIP_RGBW = 0x18100800
    const val SK6812_STRIP_RBGW = 0x18100008
    const val SK6812_STRIP_GRBW = 0x18081000
    const val SK6812_STRIP_GBRW = 0x18080010
    const val SK6812_STRIP_BRGW = 0x18001008
    const val SK6812_STRIP_BGRW = 0x18000810
}

/**
 * RGB Color
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

    fun toInt(): Int = (w.toInt() shl 24) or (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt()
}

/**
 * Extension function to convert core ColorValue to RPI Color
 */
fun dev.simonas.digitalsun.core.ColorValue.toRpiColor(): Color {
    val r = (this.r * this.a * 255).toInt().coerceIn(0, 255).toUByte()
    val g = (this.g * this.a * 255).toInt().coerceIn(0, 255).toUByte()
    val b = (this.b * this.a * 255).toInt().coerceIn(0, 255).toUByte()
    return Color(r, g, b)
}

/**
 * LED Strip controller using rpi_ws281x
 */
class LedStrip(
    private val ledCount: Int,
    private val gpioPin: Int = 18,
    private val stripType: Int = StripType.WS2811_STRIP_GRB,
    brightness: UByte = 255u,
    private val frequency: Int = WS2811Library.WS2811_TARGET_FREQ,
    private val dmaChannel: Int = 10
) : AutoCloseable {

    private val ws2811 = WS2811Library.WS2811T()
    private val lib = WS2811Library.INSTANCE
    private val channel = 0

    init {
        // Configure
        ws2811.freq = frequency
        ws2811.dmanum = dmaChannel

        // Channel 0
        ws2811.channel[0].gpionum = gpioPin
        ws2811.channel[0].count = ledCount
        ws2811.channel[0].invert = 0
        ws2811.channel[0].brightness = brightness.toByte()
        ws2811.channel[0].strip_type = stripType

        // Channel 1 unused
        ws2811.channel[1].gpionum = 0
        ws2811.channel[1].count = 0

        // Initialize
        val result = lib.ws2811_init(ws2811)
        if (result != WS2811Library.WS2811_SUCCESS) {
            val errorMsg = lib.ws2811_get_return_t_str(result) ?: "Unknown error"
            throw RuntimeException("Failed to initialize WS2811: $errorMsg")
        }
    }

    operator fun set(index: Int, color: Color) {
        if (index < 0 || index >= ledCount) {
            throw IndexOutOfBoundsException("LED index $index out of bounds")
        }
        // ws2811_led_t is uint32_t, offset in bytes = index * 4
        val offset = (index * 4).toLong()
        ws2811.channel[channel].leds?.setInt(offset, color.toInt())
    }

    operator fun get(index: Int): Color {
        if (index < 0 || index >= ledCount) {
            throw IndexOutOfBoundsException("LED index $index out of bounds")
        }
        val offset = (index * 4).toLong()
        val value = ws2811.channel[channel].leds?.getInt(offset) ?: 0
        return Color(
            r = ((value shr 16) and 0xFF).toUByte(),
            g = ((value shr 8) and 0xFF).toUByte(),
            b = (value and 0xFF).toUByte(),
            w = ((value shr 24) and 0xFF).toUByte()
        )
    }

    fun show() {
        val result = lib.ws2811_render(ws2811)
        if (result != WS2811Library.WS2811_SUCCESS) {
            val errorMsg = lib.ws2811_get_return_t_str(result) ?: "Unknown error"
            throw RuntimeException("Failed to render: $errorMsg")
        }
    }

    fun fill(color: Color) {
        for (i in 0 until ledCount) {
            this[i] = color
        }
    }

    fun clear() = fill(Color.BLACK)

    fun setBrightness(brightness: UByte) {
        ws2811.channel[channel].brightness = brightness.toByte()
    }

    override fun close() {
        lib.ws2811_fini(ws2811)
    }
}
