# Digital Sun - Build Guide

This project uses platform-specific module inclusion to support building on both macOS (for visualization) and Raspberry Pi (for hardware LED control).

## Platform-Specific Builds

The build system automatically detects your platform and includes the appropriate modules:

### macOS
```
Included modules:
- sun-core (shared logic)
- sun-openrndr (OPENRNDR visualization)

Excluded:
- target-rpi (requires Raspberry Pi hardware)
```

### Raspberry Pi (Linux ARM64)
```
Included modules:
- sun-core (shared logic)
- target-rpi (LED strip control via rpi_ws281x)

Excluded:
- sun-openrndr (visualization not needed on headless Pi)
```

### Linux x86
```
Included modules:
- sun-core (shared logic)
- sun-openrndr (for development/testing)
- target-rpi (for testing, though hardware won't work)
```

## Building on macOS

### Prerequisites
- JDK 17 or higher
- Gradle 8.14+ (included via wrapper)

### Build Commands

```bash
# Build all macOS-compatible modules
./gradlew build

# Run OPENRNDR visualization
./gradlew :sun-openrndr:run

# Create distributable package
./gradlew :sun-openrndr:shadowJar
```

The visualization will use sun-core for logic and OPENRNDR for rendering.

## Building on Raspberry Pi

### Prerequisites

1. Install JDK:
```bash
sudo apt-get update
sudo apt-get install openjdk-17-jdk
```

2. Install rpi_ws281x library:
```bash
# Install build dependencies
sudo apt-get install cmake build-essential

# Clone and build rpi_ws281x
git clone https://github.com/jgarff/rpi_ws281x.git
cd rpi_ws281x
mkdir build && cd build
cmake -D BUILD_SHARED=ON ..
cmake --build .
sudo make install
sudo ldconfig
```

### Build Commands

```bash
# Build all Raspberry Pi modules
./gradlew build

# Build only the rpi_ws281x executable (faster)
./gradlew :target-rpi:linkReleaseExecutableLinuxArm64

# Run the LED control program (requires sudo for GPIO access)
sudo ./target-rpi/build/bin/linuxArm64/releaseExecutable/target-rpi.kexe
```

## Project Structure

```
digital-sun/
├── sun-core/              # Pure Kotlin logic (platform-independent)
│   ├── Parameter system
│   ├── Module system
│   └── Pattern algorithms
│
├── sun-openrndr/          # OPENRNDR visualization (macOS/Linux x86)
│   ├── Real-time preview
│   ├── GUI controls
│   └── Visual effects
│
└── target-rpi/        # LED hardware control (Raspberry Pi only)
    ├── rpi_ws281x bindings
    ├── rpi_ws281x-like API
    └── Hardware rendering
```

## Module Dependencies

```
sun-openrndr  ──depends on──>  sun-core
target-rpi  ──depends on──>  sun-core
```

Both visualization and hardware modules share the same core logic, ensuring patterns look identical on screen and on physical LEDs.

## Forcing Module Inclusion

If you need to override the automatic platform detection, you can manually edit `settings.gradle.kts` and comment/uncomment the `include()` statements.

## Troubleshooting

### macOS: "target-rpi not found"
This is expected. The target-rpi module is excluded on macOS because it requires Raspberry Pi hardware libraries.

### Raspberry Pi: "sun-openrndr not found"
This is expected. The OPENRNDR visualization module is excluded on Raspberry Pi to reduce build time and dependencies.

### Build fails with "ws2811.h not found"
You're trying to build target-rpi without the rpi_ws281x library installed. Either:
1. Install rpi_ws281x (see prerequisites above)
2. Build on the correct platform (macOS shouldn't include target-rpi)

### IntelliJ/IDE shows "unresolved reference" for excluded modules
This is expected. Your IDE may cache the full project structure. Reimport the Gradle project or restart the IDE after switching platforms.

## CI/CD Considerations

For continuous integration, you may want to:
- Build macOS modules on macOS runners
- Build Raspberry Pi modules on ARM Linux runners
- Use Docker containers for cross-platform testing

Example GitHub Actions matrix:
```yaml
strategy:
  matrix:
    os: [macos-latest, ubuntu-latest]
    include:
      - os: macos-latest
        modules: sun-core,sun-openrndr
      - os: ubuntu-latest
        modules: sun-core,target-rpi
```
