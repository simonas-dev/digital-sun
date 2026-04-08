# Microsun – Hardware Specification

Handoff document for hardware manufacturing. Covers physical layout, components, wiring, and assembly of the Microsun LED display panel.

---

## Strip Specification

| Parameter | Value |
|-----------|-------|
| IC type | WS2812B |
| Supply voltage | **DC 12 V** |
| LED density | **160 LEDs/m** (6.25 mm pitch) |
| ICs per metre | 160 (1 IC drives 1 LED chip) |
| Cut point | Every 1 LED (every 6.25 mm) |
| Power per LED | 0.3 W (max, full white) |
| PCB width | **2.7 mm** |
| LED package | 0807 size |
| PCB colour | White |
| Protection | IP30 — Non-waterproof |
| Grey scale | 256 levels per channel |
| Colour depth | 8 bits/channel, RGB |

> ⚠ **12V supply required.** Do not use a 5V supply. The Raspberry Pi's own 5V rail must not be used for the strips.

> ⚠ **Signal level.** The RPi GPIO outputs 3.3V logic. WS2812B data threshold at 5V internal VCC is ~3.5V, so **a 3.3V→5V level-shifter on the DATA line is recommended** for reliable operation.

---

## Overview

The Microsun display is a **diamond-shaped LED panel** built from 22 rows of horizontal WS2812B LED strips. The strips are arranged in a staircase pattern that creates a diamond outline (≈ 21 × 21 cm bounding box). Driven by a Raspberry Pi over SPI.

**Total LED count: 604** (160 LEDs/m × 22 strips of varying lengths)

> **Firmware note**: `Stage.kt` currently addresses 292 logical pixels. This must be updated to 604 before first use with this hardware.

---

## Physical Layout

See `strip-layout.svg` for a dimensioned diagram.

### Shape

- **Overall bounding box**: 21.25 cm × 21.25 cm
- **Diamond form**: widest in the center equatorial band (21.25 cm), tapering to 7.5 cm at top and bottom
- **Total rows**: 22 horizontal LED strip sections

### Row Specifications

Rows are listed **top to bottom** as they appear on the display face. The display is split into two physical sections with a connection zone in the middle.

At 160 LEDs/m (6.25 mm pitch), each 12.5 mm design cell contains **2 LEDs**.

#### Upper Half (9 rows — pads face inward / toward center gap)

| Row | Length   | LEDs | Left-edge X offset    | Notes                     |
|-----|----------|------|-----------------------|---------------------------|
| T1  | 7.5 cm   | 12   | 68.75 mm from center  | Topmost strip             |
| T2  | 11.25 cm | 18   | 50.00 mm              |                           |
| T3  | 13.75 cm | 22   | 37.50 mm              |                           |
| U1  | 16.25 cm | 26   | 25.00 mm              |                           |
| U2  | 17.5 cm  | 28   | 18.75 mm              |                           |
| U3  | 18.75 cm | 30   | 12.50 mm              |                           |
| U4  | 20 cm    | 32   |  6.25 mm              |                           |
| U5  | 20 cm    | 32   |  6.25 mm              | Same length & X as U4     |
| U6  | 21.25 cm | 34   |  0 mm (left edge)     | Widest, closest to center |

**"Left-edge X offset"** = how far the strip's left end is from the leftmost point of the full display (X = 0 is the left edge of the widest rows).

#### Connection Zone (~8 mm center-to-center extra gap)

This gap is where all inter-section wiring meets. Provide at least 8 mm clear space for solder bridging between the upper and lower halves, plus power injection cables.

#### Lower Half (13 rows — pads face inward / toward center gap)

| Row | Length   | LEDs | Left-edge X offset | Notes                    |
|-----|----------|------|--------------------|--------------------------|
| M1  | 21.25 cm | 34   | 0 mm               | Equatorial band          |
| M2  | 21.25 cm | 34   | 0 mm               |                          |
| M3  | 21.25 cm | 34   | 0 mm               |                          |
| M4  | 21.25 cm | 34   | 0 mm               | Last of equatorial band  |
| L1  | 21.25 cm | 34   | 0 mm               | Start of lower taper     |
| L2  | 20 cm    | 32   | 6.25 mm            |                          |
| L3  | 20 cm    | 32   | 6.25 mm            |                          |
| L4  | 18.75 cm | 30   | 12.50 mm           |                          |
| L5  | 17.5 cm  | 28   | 18.75 mm           |                          |
| L6  | 16.25 cm | 26   | 25.00 mm           |                          |
| L7  | 13.75 cm | 22   | 37.50 mm           |                          |
| L8  | 11.25 cm | 18   | 50.00 mm           |                          |
| L9  | 7.5 cm   | 12   | 68.75 mm           | Bottommost strip         |

### Summary Counts

| Section      | Strips | LEDs | Strip lengths  |
|--------------|--------|------|----------------|
| Upper half   | 9      | 234  | 7.5–21.25 cm   |
| Lower half   | 13     | 370  | 21.25–7.5 cm   |
| **Total**    | **22** | **604** |             |

---

## Strip Dimensions

| Parameter                       | Value    | Notes                               |
|---------------------------------|----------|-------------------------------------|
| Strip body width                | 2.7 mm   | WS2812B flexible PCB strip          |
| LED pitch                       | 6.25 mm  | 160 LEDs/m                          |
| Row center-to-center spacing    | 9.34 mm  | Uniform across all rows             |
| Edge-to-edge gap (strip bodies) | 6.64 mm  |                                     |
| Full PCB row height (incl. pads)| 9.6 mm   | Includes solder pad overhang        |
| Total display height            | ~212 mm  | 21.25 cm                            |
| Total display width             | ~212 mm  | 21.25 cm at widest                  |

---

## Staircase Offset Pattern

Each row is offset horizontally. Going from the widest rows outward (toward top or bottom), the left edge shifts **+6.25 mm** per row for most steps:

```
← Left edge offset from widest row →

Top/Bottom:   +68.75 mm  (7.5 cm,   12 LEDs)
              +50.00 mm  (11.25 cm, 18 LEDs)
              +37.50 mm  (13.75 cm, 22 LEDs)
              +25.00 mm  (16.25 cm, 26 LEDs)
              +18.75 mm  (17.5 cm,  28 LEDs)
              +12.50 mm  (18.75 cm, 30 LEDs)
              + 6.25 mm  (20 cm,    32 LEDs)
              + 6.25 mm  (20 cm,    32 LEDs)
              + 0.00 mm  (21.25 cm, 34 LEDs — widest)
```

The staircase step (6.25 mm horizontal, 9.34 mm vertical) produces a straight 33.9° diagonal edge on each side of the diamond.

---

## LED Data Chain

See `wiring.svg` for a full wiring diagram.

The 22 strips form a **single continuous data chain**: data enters at T1 (top strip) and exits at L9 (bottom strip).

```
GPIO 10 (SPI MOSI) ─── DIN ─[T1]─ DOUT ─[T2]─ ... ─[T3]─[U1]─ ... ─ DOUT ─[U6]
                                                                              │
                                               (solder bridge across ~8 mm gap)
                                                                              │
                                 DIN ─[M1]─ DOUT ─[M2]─ ... ─[M4]─[L1]─ ... ─[L9]─ DOUT (end)
```

### LED Address Map (sequential, 0-indexed)

| Row | Start | End | Count | Power (max) |
|-----|-------|-----|-------|-------------|
| T1  | 0     | 11  | 12    | 3.6 W       |
| T2  | 12    | 29  | 18    | 5.4 W       |
| T3  | 30    | 51  | 22    | 6.6 W       |
| U1  | 52    | 77  | 26    | 7.8 W       |
| U2  | 78    | 105 | 28    | 8.4 W       |
| U3  | 106   | 135 | 30    | 9.0 W       |
| U4  | 136   | 167 | 32    | 9.6 W       |
| U5  | 168   | 199 | 32    | 9.6 W       |
| U6  | 200   | 233 | 34    | 10.2 W      |
| M1  | 234   | 267 | 34    | 10.2 W      |
| M2  | 268   | 301 | 34    | 10.2 W      |
| M3  | 302   | 335 | 34    | 10.2 W      |
| M4  | 336   | 369 | 34    | 10.2 W      |
| L1  | 370   | 403 | 34    | 10.2 W      |
| L2  | 404   | 435 | 32    | 9.6 W       |
| L3  | 436   | 467 | 32    | 9.6 W       |
| L4  | 468   | 497 | 30    | 9.0 W       |
| L5  | 498   | 525 | 28    | 8.4 W       |
| L6  | 526   | 551 | 26    | 7.8 W       |
| L7  | 552   | 573 | 22    | 6.6 W       |
| L8  | 574   | 591 | 18    | 5.4 W       |
| L9  | 592   | 603 | 12    | 3.6 W       |
| **Total** | | | **604** | **181.2 W** |

---

## Wiring

### Interface

| Signal | Raspberry Pi pin | GPIO | Notes                                     |
|--------|-----------------|------|-------------------------------------------|
| DATA   | Pin 19          | 10   | SPI0 MOSI — used in firmware              |
| GND    | Pin 6 (or any GND) | — | Common ground with PSU                    |
| 12V    | External PSU only | —  | Do **not** connect to any RPi pin         |

**Level shifter required on DATA line.** The RPi outputs 3.3V; WS2812B input threshold (at 5V internal VCC) is ~3.5V. Use a 74HCT245, TXS0101, or equivalent 3.3V→5V single-channel level shifter. Alternatively, a 33Ω–100Ω series resistor on the data line combined with a short wire run may work in practice but is not guaranteed.

### Power Supply Requirements

| Parameter | Value |
|-----------|-------|
| Voltage | **12 V DC** (±5%) |
| Power — maximum (0.3 W/LED, full white) | **181 W** (604 × 0.3 W) |
| Current — maximum | **15.1 A** (181 W ÷ 12 V) |
| Power — typical display operation | ~40–60 W |
| Recommended PSU | **12 V / 20 A** (240 W) |
| Wire gauge — main feed | ≥ **14 AWG** |
| Wire gauge — injection taps | ≥ **18 AWG** |

### Power Injection Points

To prevent voltage drop, inject power at **4 points** (see `wiring.svg`). At 12V the resistance drop is proportionally lower than at 5V, but with 15A total the long equatorial strips still benefit from mid-point injection.

1. **T1 left end** — feeds upper taper T1–T3 (52 LEDs, 15.6 W)
2. **U4 left end** — feeds wide upper strips U4–U6 (98 LEDs, 29.4 W)
3. **M1 left end** — feeds equatorial band M1–M4 (136 LEDs, 40.8 W)
4. **L4 left end** — feeds lower taper L4–L9 (116 LEDs, 34.8 W)

Connect **both 12V and GND** at each injection point. All GND rails must be tied together including the RPi GND.

---

## Components

| Component | Specification | Qty |
|-----------|---------------|-----|
| WS2812B LED strip | **12V DC**, 2.7 mm wide, **160 LEDs/m**, IP30, white PCB | See cut list |
| Raspberry Pi | Any model with GPIO header | 1 |
| 12V DC power supply | 20 A rated (240 W), regulated | 1 |
| Level shifter | 3.3V → 5V single-channel (e.g. 74HCT245, TXS0101) | 1 |
| Main power wire | 14 AWG, red/black, silicone insulation | ~1.5 m |
| Injection tap wire | 18 AWG, red/black | ~1 m |
| Data wire | 22 AWG, shielded | ~30 cm |
| Mounting substrate | Flat panel, ≥ 22 × 22 cm | 1 |
| Standoffs / frame | For RPi mounting | as needed |

### Strip Cut List

Cut from a continuous 12V WS2812B reel (160 LEDs/m). Cut at the designated cut marks every 6.25 mm (1 LED per cut). Allow 2–3 mm extra at each end for solder tab clearance.

| Cut # | Length  | LED count | Used for |
|-------|---------|-----------|----------|
| 1     | 7.5 cm  | 12        | T1       |
| 2     | 7.5 cm  | 12        | L9       |
| 3     | 11.25 cm | 18       | T2       |
| 4     | 11.25 cm | 18       | L8       |
| 5     | 13.75 cm | 22       | T3       |
| 6     | 13.75 cm | 22       | L7       |
| 7     | 16.25 cm | 26       | U1       |
| 8     | 16.25 cm | 26       | L6       |
| 9     | 17.5 cm  | 28       | U2       |
| 10    | 17.5 cm  | 28       | L5       |
| 11    | 18.75 cm | 30       | U3       |
| 12    | 18.75 cm | 30       | L4       |
| 13    | 20 cm    | 32       | U4       |
| 14    | 20 cm    | 32       | U5       |
| 15    | 20 cm    | 32       | L2       |
| 16    | 20 cm    | 32       | L3       |
| 17    | 21.25 cm | 34       | U6       |
| 18    | 21.25 cm | 34       | M1       |
| 19    | 21.25 cm | 34       | M2       |
| 20    | 21.25 cm | 34       | M3       |
| 21    | 21.25 cm | 34       | M4       |
| 22    | 21.25 cm | 34       | L1       |
| **Total** | **~380 cm** | **604** | |

---

## Assembly Notes

1. **Upper half orientation**: Upper half strips (T1–U6) are mounted with their **LED face toward the viewer** and **solder pads accessible from the center gap side** (pointing inward). This matches the `scale(1, -1)` representation in the design SVG.

2. **Lower half orientation**: Lower half strips (M1–L9) are mounted conventionally with solder pads accessible from the center gap side (pointing inward / upward).

3. **Center gap**: Leave 8–10 mm clear between the bottom of U6 and the top of M1. This is the working space for all cross-section solder joints and data/power bridging.

4. **Data direction**: All strips carry data **left to right** (as viewed from the front). DATA IN is always on the **left end** of each strip.

5. **Level shifter placement**: Place the level shifter close to the RPi, before the first strip (T1 DIN). The output side runs at 5V logic; keep the data run from the shifter to T1 short (<30 cm).

6. **Grounding**: Tie the RPi GND, the level-shifter GND, and the PSU GND together at a single point before powering on.

7. **Firmware update**: Update `Stage.kt` `create500Stage()` to produce **604 pixel positions** matching the address map above before first use.

8. **First boot check**: Power on with a test pattern (solid low-brightness green, ~1 A draw) before full operation to verify chain continuity and LED count.

---

## Related Files

| File | Description |
|------|-------------|
| `strip-layout.svg` | Physical layout diagram with dimensions (scale 1:1, 2 px/mm) |
| `wiring.svg` | Electrical wiring diagram — data chain and power injection |
| `../target-rpi/src/main/kotlin/.../Main.kt` | Firmware — GPIO pin, LED count config |
| `../sun-core/src/main/kotlin/.../Stage.kt` | Logical pixel layout — **needs update to 604 LEDs** |
