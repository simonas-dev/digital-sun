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

## Building for Raspberry Pi

**Important Limitation**: Due to Kotlin/Native constraints, you **cannot** build `target-rpi` directly on Raspberry Pi or cross-compile from macOS.

### Supported Build Platform

- **Linux x86-64 ONLY** - You must build on a Linux x86-64 machine (physical, VM, Docker, or CI/CD)

### Why This Limitation?

1. Kotlin/Native doesn't support Linux ARM64 (Raspberry Pi) as a **host** platform
2. Cross-compilation from macOS requires all library symbols at link time (not possible with rpi_ws281x)

### Recommended: GitHub Actions

The easiest approach is to use GitHub Actions to build automatically:

```yaml
name: Build Raspberry Pi Binary
on: [push]
jobs:
  build-rpi:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Install rpi_ws281x
        run: |
          sudo apt-get update && sudo apt-get install -y cmake build-essential
          git clone https://github.com/jgarff/rpi_ws281x.git
          cd rpi_ws281x && mkdir build && cd build
          cmake -D BUILD_SHARED=ON .. && cmake --build .
          sudo make install && sudo ldconfig
      - name: Build
        run: ./gradlew :target-rpi:linkReleaseExecutableLinuxArm64
      - uses: actions/upload-artifact@v3
        with:
          name: target-rpi-binary
          path: target-rpi/build/bin/linuxArm64/releaseExecutable/target-rpi.kexe
```

See [target-rpi/CROSS-COMPILE.md](target-rpi/CROSS-COMPILE.md) for more build options (Docker, Linux VM, etc.).

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

### Cannot build target-rpi on Raspberry Pi
This is a Kotlin/Native limitation. You must build on Linux x86-64. See [target-rpi/CROSS-COMPILE.md](target-rpi/CROSS-COMPILE.md) for solutions.

### Cannot cross-compile from macOS
This is expected. Use GitHub Actions or a Linux x86-64 machine to build. See [target-rpi/CROSS-COMPILE.md](target-rpi/CROSS-COMPILE.md).

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
