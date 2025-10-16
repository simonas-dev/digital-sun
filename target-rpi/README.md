# target-rpi

Kotlin/JVM + JNA bindings for `rpi_ws281x` library. Controls WS281x LED strips on Raspberry Pi.

## Installation on Raspberry Pi

Run these commands **in order**:

```bash
# 1. Install Java
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk

# 2. Install build tools
sudo apt-get install -y cmake build-essential git

# 3. Build and install rpi_ws281x library
git clone https://github.com/jgarff/rpi_ws281x.git
cd rpi_ws281x
mkdir build && cd build
cmake -D BUILD_SHARED=ON ..
cmake --build .
sudo make install
sudo ldconfig

# Verify library installed
ldconfig -p | grep ws2811
# Should show: libws2811.so (libc6,hard-float) => /usr/local/lib/libws2811.so
```

## Build & Run

```bash
# Clone your repo if not already there
cd ~/digital-sun

# Build (this WILL work - it's just JVM code)
./gradlew :target-rpi:build

# Run (requires sudo for GPIO)
sudo java -jar target-rpi/build/libs/target-rpi-1.0.0.jar
```

## Usage

```kotlin
import dev.simonas.digitalsun.rpi.*

fun main() {
    LedStrip(ledCount = 60, gpioPin = 18).use { leds ->
        // Set first LED red
        leds[0] = Color.RED
        leds.show()

        // Fill all green
        leds.fill(Color.GREEN)
        leds.show()

        // Clear
        leds.clear()
        leds.show()
    }
}
```

## API

### LedStrip
- `LedStrip(ledCount, gpioPin = 18, stripType = StripType.WS2811_STRIP_GRB, brightness = 255u)`
- `operator fun set(index: Int, color: Color)`
- `operator fun get(index: Int): Color`
- `fun show()` - Update strip
- `fun fill(color: Color)`
- `fun clear()`
- `fun setBrightness(brightness: UByte)`

### Color
- `Color(r: UByte, g: UByte, b: UByte, w: UByte = 0u)`
- Predefined: `Color.RED`, `Color.GREEN`, `Color.BLUE`, `Color.WHITE`, `Color.BLACK`, etc.

### StripType
- `WS2811_STRIP_RGB`, `WS2811_STRIP_GRB`, etc.
- `SK6812_STRIP_RGBW`, `SK6812_STRIP_GRBW`, etc.

## GPIO Pins
- GPIO 18 (PWM0) - Recommended
- GPIO 19 (PWM1)
- GPIO 21 (PCM)
