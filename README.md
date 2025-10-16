# Digital Sun

A multi-platform LED visualization system with noise-based shader algorithms.

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

### sun-fastled
FastLED/Raspberry Pi LED strip rendering backend.

**Contains:**
- `fastled/render/` - LED strip renderer
- `fastled/serial/` - Serial communication with Arduino/FastLED
- `fastled/noise/` - Pure Kotlin noise implementation
- Main application entry point

**Dependencies:** sun-core + jSerialComm

**Purpose:** Production LED strip rendering on Raspberry Pi hardware.

## Building

Build all modules:
```bash
./gradlew build
```

Build specific module:
```bash
./gradlew :sun-core:build
./gradlew :sun-openrndr:build
./gradlew :sun-fastled:build
```

## Running

### OPENRNDR Desktop Application
```bash
./gradlew :sun-openrndr:run
```

### FastLED Application (Raspberry Pi)
```bash
./gradlew :sun-fastled:run
```

Or create a standalone jar:
```bash
./gradlew :sun-fastled:shadowJar
java -jar sun-fastled/build/libs/sun-fastled-1.0.0-all.jar
```

## Module Dependencies

```
        ┌─────────────────────────┐
        │   sun-fastled           │
        │  (FastLED rendering)    │
        │                         │
        │ depends on: sun-core    │
        └─────────────────────────┘

        ┌─────────────────────────┐
        │    sun-openrndr         │
        │  (OPENRNDR rendering)   │
        │                         │
        │ depends on: sun-core    │
        └─────────────────────────┘

              ↓         ↓
         ┌────────────────────────┐
         │     sun-core           │
         │  (Pure logic layer)    │
         │                        │
         │ No external rendering  │
         │ dependencies           │
         └────────────────────────┘
```

## Architecture

The core shader algorithm is platform-agnostic. Different rendering backends provide:
1. **Noise generator implementation** - Adapts noise functions to the core interface
2. **Color output** - Converts core ColorValue to platform-specific format
3. **Rendering loop** - Drives the shader with time and parameters

This allows the same shader logic to run on desktop (OPENRNDR) or embedded hardware (FastLED).
