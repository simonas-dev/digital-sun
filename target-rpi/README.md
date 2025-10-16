# target-rpi

Kotlin/Native bindings for the `rpi_ws281x` library, providing a LedStrip-like API for controlling WS281x LED strips on Raspberry Pi.

## Features

- Simple, LedStrip-inspired API
- Support for WS2811, WS2812, WS2812B, SK6812 LED strips
- RGB and RGBW color support
- Built for Raspberry Pi (Linux ARM64)

## Prerequisites

### On Raspberry Pi

1. Install the rpi_ws281x library:

```bash
# Install dependencies
sudo apt-get update
sudo apt-get install cmake build-essential

# Clone and build rpi_ws281x
git clone https://github.com/jgarff/rpi_ws281x.git
cd rpi_ws281x
mkdir build
cd build
cmake -D BUILD_SHARED=ON ..
cmake --build .
sudo make install
sudo ldconfig
```

2. Ensure you have the proper permissions to access GPIO (run as root or add user to gpio group)

## Building

### Option 1: Build on Raspberry Pi (Recommended)

Since this module requires the `rpi_ws281x` C library headers, build directly on your Raspberry Pi:

```bash
# On Raspberry Pi
git clone <your-repo>
cd digital-sun
./gradlew :target-rpi:linkReleaseExecutableLinuxArm64
```

The compiled binary will be at:
```
target-rpi/build/bin/linuxArm64/releaseExecutable/target-rpi.kexe
```

### Option 2: Cross-compilation from macOS/Linux

To cross-compile from your development machine, you need to:

1. Download the `rpi_ws281x` library headers
2. Configure the cinterop def file with proper paths
3. Use a cross-compilation toolchain

For most use cases, **building directly on the Raspberry Pi is simpler**.

## Usage

### Basic Example

```kotlin
import dev.simonas.digitalsun.rpi.*

fun main() {
    // Create LED strip with 60 LEDs on GPIO 18
    LedStrip(ledCount = 60, gpioPin = 18).use { leds ->

        // Turn first LED red
        leds[0] = Color.RED
        leds.show()

        // Fill all LEDs green
        leds.fill(Color.GREEN)
        leds.show()

        // Custom color
        leds[5] = Color(r = 255u, g = 128u, b = 0u) // Orange
        leds.show()

        // Clear all LEDs
        leds.clear()
        leds.show()
    }
}
```

### Configuration Options

```kotlin
LedStrip(
    ledCount = 60,              // Number of LEDs
    gpioPin = 18,               // GPIO pin (default: 18 = PWM0)
    stripType = StripType.WS2811_STRIP_GRB,  // Color order
    brightness = 255u,          // Initial brightness (0-255)
    frequency = 800000u,        // Signal frequency in Hz
    dmaChannel = 10             // DMA channel to use
)
```

### Available GPIO Pins

- **GPIO 18** (PWM0) - Recommended
- **GPIO 19** (PWM1)
- **GPIO 21** (PCM)

### Strip Types

- `WS2811_STRIP_RGB`, `WS2811_STRIP_RBG`, `WS2811_STRIP_GRB`, etc.
- `SK6812_STRIP_RGBW`, `SK6812_STRIP_GRBW`, etc.

### Predefined Colors

- `Color.RED`, `Color.GREEN`, `Color.BLUE`
- `Color.WHITE`, `Color.BLACK`
- `Color.YELLOW`, `Color.CYAN`, `Color.MAGENTA`

## Running on Raspberry Pi

1. Transfer the compiled binary to your Raspberry Pi
2. Run with sudo (required for GPIO access):

```bash
sudo ./target-rpi.kexe
```

## API Reference

### LedStrip Class

- `operator fun set(index: Int, color: Color)` - Set LED color
- `operator fun get(index: Int): Color` - Get LED color
- `fun show()` - Update the LED strip
- `fun fill(color: Color)` - Fill all LEDs with color
- `fun clear()` - Turn off all LEDs
- `fun setBrightness(brightness: UByte)` - Set brightness (0-255)

### Color Class

- `Color(r: UByte, g: UByte, b: UByte, w: UByte = 0u)` - Create color
- Predefined colors available as constants

## Notes

- Requires root/sudo for GPIO access
- The library uses DMA for efficient LED control
- Default frequency is 800kHz (800000 Hz)
- For WS2811 strips, you may need 400kHz (400000 Hz)

## License

This wrapper is MIT licensed. The underlying rpi_ws281x library is BSD licensed.
