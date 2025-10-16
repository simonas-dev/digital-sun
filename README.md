# Digital Sun

A cross-platform LED pattern system with real-time visualization and hardware control for Raspberry Pi.

## Overview

Digital Sun is a modular system for creating and displaying LED patterns:
- **Develop on macOS** with real-time OPENRNDR visualization
- **Deploy to Raspberry Pi** for physical LED strip control
- **Share core logic** between visualization and hardware

The build system automatically detects your platform and includes only relevant modules.

## Quick Start

### On macOS (Development & Visualization)
```bash
./gradlew build
./gradlew :sun-openrndr:run
```

### On Raspberry Pi (Hardware Control)
```bash
# Install rpi_ws281x library first (see BUILD.md)
./gradlew build
sudo ./target-rpi/build/bin/linuxArm64/releaseExecutable/target-rpi.kexe
```

## Project Structure

This project is organized into three modules:

### sun-core
Pure Kotlin/Java logic with no platform-specific dependencies.

**Contains:**
- `core/data/` - Data structures (Pixel, Stage)
- `core/shader/` - Shader algorithms and interfaces
- `core/noise/` - Noise generation interfaces

**Dependencies:** Kotlin stdlib, Kotlinx serialization, coroutines

**Purpose:** Reusable algorithms that can be used by any rendering backend.

### sun-openrndr
OPENRNDR visualization layer for desktop development and preview.

**Contains:**
- `openrndr/render/` - OPENRNDR-specific rendering
- `openrndr/noise/` - OPENRNDR noise adapter
- `openrndr/ui/` - GUI parameter controls
- Main application entry point

**Dependencies:** sun-core + OPENRNDR libraries

**Purpose:** Desktop application for developing and previewing shaders with a live GUI.

### target-rpi
Kotlin/Native LED strip control for Raspberry Pi via rpi_ws281x.

**Contains:**
- `rpi/` - Kotlin wrapper API for rpi_ws281x
- `nativeInterop/cinterop/` - C bindings for rpi_ws281x
- Example programs and animations

**Dependencies:** sun-core + rpi_ws281x (C library)

**Purpose:** Direct hardware control of WS281x LED strips on Raspberry Pi.

## Platform-Specific Builds

The build system automatically detects your platform:
- **macOS**: Builds `sun-core` + `sun-openrndr` (visualization)
- **Raspberry Pi** (Linux ARM): Builds `sun-core` + `target-rpi` (hardware)
- **Linux x86**: Builds all modules for testing

See [BUILD.md](BUILD.md) for detailed build instructions.

## Building

### macOS
```bash
./gradlew build                    # Builds sun-core + sun-openrndr
./gradlew :sun-openrndr:run        # Run visualization
./gradlew :sun-openrndr:shadowJar  # Create distributable
```

### Raspberry Pi
```bash
# Prerequisites: Install rpi_ws281x library (see BUILD.md)
./gradlew build                                        # Builds sun-core + target-rpi
./gradlew :target-rpi:linkReleaseExecutableLinuxArm64 # Build LED executable
sudo ./target-rpi/build/bin/linuxArm64/releaseExecutable/target-rpi.kexe
```

## Module Dependencies

```
┌──────────────────────┐          ┌──────────────────────┐
│    target-rpi        │          │    sun-openrndr      │
│  (Raspberry Pi HW)   │          │  (macOS/Linux viz)   │
│  Kotlin/Native       │          │  Kotlin/JVM          │
└──────────┬───────────┘          └──────────┬───────────┘
           │                                 │
           │         depends on              │
           └────────────┬────────────────────┘
                        │
                ┌───────▼────────┐
                │    sun-core    │
                │ (Shared logic) │
                │   Kotlin/JVM   │
                └────────────────┘
```

## Architecture

The core pattern logic is platform-agnostic. Different rendering backends provide:
1. **Noise generator implementation** - Adapts noise functions to the core interface
2. **Color output** - Converts core ColorValue to platform-specific format
3. **Rendering loop** - Drives patterns with time and parameters

This allows the same pattern logic to run on:
- **Desktop** (OPENRNDR) - Real-time preview with GUI controls
- **Raspberry Pi** (rpi_ws281x) - Physical LED strips via GPIO

## Documentation

- [BUILD.md](BUILD.md) - Detailed build instructions for all platforms
- [target-rpi/README.md](target-rpi/README.md) - LED hardware setup and API

## Requirements

- **All platforms**: JDK 17+, Gradle 8.14+ (included via wrapper)
- **Raspberry Pi only**: rpi_ws281x library (see BUILD.md for installation)

## License

MIT
