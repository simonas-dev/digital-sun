# SunsetLamp LED Pixel Circle — Technical Specification

## 1. Overview

A 150mm diameter circular LED pixel display built from horizontal rows of individually addressable WS2812B LED strip, mounted on a carrier structure with a vertical spine PCB handling all electrical connections. Each LED is one pixel on a uniform 6.25mm × 6.25mm grid. The circular boundary determines which grid positions are populated.

```
Display type:       Circular pixel grid
Diameter:           150.0 mm (15.0 cm)
Grid size:          24 × 24 cells (6.25mm pitch)
Pixel pitch:        6.25 mm horizontal, 6.25 mm vertical
Active pixels:      448 LEDs
Active rows:        24
Symmetry:           Perfect — widest row = tallest column = 24
```

---

## 2. LED Strip Specifications

Source: BTF-LIGHTING SMD 2020 WS2812B 5mm Ultra Narrow

```
Model:              HD-12V-2020-160L-W-IP30-SPI
LED package:        SMD 2020 (2.05mm measured)
LED density:        160 LEDs/m
Voltage:            DC 12V
Strip width:        5.0 mm
Strip thickness:    ~1.0 mm (flex PCB)
Waterproofing:      IP30 (none — bare PCB)
Max power:          6 W/m
Color order:        G R B (not RGB)

Cuttable segment:   6.25 mm (1000mm ÷ 160 LEDs)
Pads per cut point: 3 (VCC, DIN/DOUT, GND)
Pad width:          ~1.1 mm
Pad pitch:          ~1.45 mm center-to-center
LED-to-LED edge:    4.08 mm (diode edge to diode edge)
```

---

## 3. Pixel Circle Layout

### 3.1 Grid geometry

The pixel circle is defined by a 24×24 grid with 6.25mm cell size. A cell at grid position (col, row) is populated if its center falls within a circle of radius R = 75.0mm centered on the grid.

Cell center coordinates:
```
x = (col + 0.5 - 12) × 6.25 mm      for col = 0..23
y = (row + 0.5 - 12) × 6.25 mm      for row = 0..23

Inclusion test: x² + y² ≤ 75.0²
```

This produces perfect N/S and E/W symmetry: the widest row spans 24 pixels, and the tallest column spans 24 rows.

### 3.2 Row-by-row layout

Each row is one continuous strip segment. Mirrored rows (equal distance above/below center) have identical pixel counts.

```
Row  | Y position | Pixels | Strip length | Grid columns | Col offset
-----|------------|--------|-------------|-------------|----------
  1  |  -71.875mm |    6   |   37.50 mm  |    9 — 14   |     9
  2  |  -65.625mm |   12   |   75.00 mm  |    6 — 17   |     6
  3  |  -59.375mm |   14   |   87.50 mm  |    5 — 18   |     5
  4  |  -53.125mm |   16   |  100.00 mm  |    4 — 19   |     4
  5  |  -46.875mm |   18   |  112.50 mm  |    3 — 20   |     3
  6  |  -40.625mm |   20   |  125.00 mm  |    2 — 21   |     2
  7  |  -34.375mm |   22   |  137.50 mm  |    1 — 22   |     1
  8  |  -28.125mm |   22   |  137.50 mm  |    1 — 22   |     1
  9  |  -21.875mm |   22   |  137.50 mm  |    1 — 22   |     1
 10  |  -15.625mm |   24   |  150.00 mm  |    0 — 23   |     0
 11  |   -9.375mm |   24   |  150.00 mm  |    0 — 23   |     0
 12  |   -3.125mm |   24   |  150.00 mm  |    0 — 23   |     0
 13  |   +3.125mm |   24   |  150.00 mm  |    0 — 23   |     0
 14  |   +9.375mm |   24   |  150.00 mm  |    0 — 23   |     0
 15  |  +15.625mm |   24   |  150.00 mm  |    0 — 23   |     0
 16  |  +21.875mm |   22   |  137.50 mm  |    1 — 22   |     1
 17  |  +28.125mm |   22   |  137.50 mm  |    1 — 22   |     1
 18  |  +34.375mm |   22   |  137.50 mm  |    1 — 22   |     1
 19  |  +40.625mm |   20   |  125.00 mm  |    2 — 21   |     2
 20  |  +46.875mm |   18   |  112.50 mm  |    3 — 20   |     3
 21  |  +53.125mm |   16   |  100.00 mm  |    4 — 19   |     4
 22  |  +59.375mm |   14   |   87.50 mm  |    5 — 18   |     5
 23  |  +65.625mm |   12   |   75.00 mm  |    6 — 17   |     6
 24  |  +71.875mm |    6   |   37.50 mm  |    9 — 14   |     9
```

### 3.3 Symmetry verification

```
Widest row (rows 10-15):   24 pixels = 150.00 mm
Tallest column (cols 9-14): 24 rows  = 150.00 mm (including pitch)
Top/bottom mirror:          ✓ Row 1 = Row 24, Row 2 = Row 23, etc.
Left/right mirror:          ✓ All rows centered on grid midpoint
Parity:                     All rows have even pixel counts
```

---

## 4. Strip Cut List

### 4.1 Bill of materials

```
Length      | Pixels | Quantity | Total LEDs | Total strip
------------|--------|---------|-----------|------------
150.00 mm   |   24   |    6    |    144    |   900.0 mm
137.50 mm   |   22   |    6    |    132    |   825.0 mm
125.00 mm   |   20   |    2    |     40    |   250.0 mm
112.50 mm   |   18   |    2    |     36    |   225.0 mm
100.00 mm   |   16   |    2    |     32    |   200.0 mm
 87.50 mm   |   14   |    2    |     28    |   175.0 mm
 75.00 mm   |   12   |    2    |     24    |   150.0 mm
 37.50 mm   |    6   |    2    |     12    |    75.0 mm
------------|--------|---------|-----------|------------
TOTAL       |        |   24    |    448    |  2800.0 mm
```

### 4.2 Strip purchasing

```
Total strip needed:   2.80 m (448 LEDs × 6.25mm)
Waste allowance:      ~10% for cut alignment errors
Order quantity:       3.1 m minimum → order 4m or 5m roll
At 160 LEDs/m:        3.1m = 496 LEDs → 4m roll covers it with margin
```

### 4.3 Cutting instructions

Cut all strips at the marked cut lines on the flex PCB. Each cut exposes 3 solder pads on each side of the cut. Verify pad count before soldering.

Cutting order (recommended — cut longest first to minimize waste from a continuous roll):
1. Cut 6× 150.00mm segments (24 LEDs each) = 900mm used
2. Cut 6× 137.50mm segments (22 LEDs each) = 825mm used
3. Cut 2× 125.00mm segments (20 LEDs each) = 250mm used
4. Cut 2× 112.50mm segments (18 LEDs each) = 225mm used
5. Cut 2× 100.00mm segments (16 LEDs each) = 200mm used
6. Cut 2× 87.50mm segments  (14 LEDs each) = 175mm used
7. Cut 2× 75.00mm segments  (12 LEDs each) = 150mm used
8. Cut 2× 37.50mm segments  ( 6 LEDs each) =  75mm used

Running total: 2800mm = 2.80m

---

## 5. Power Budget

### 5.1 Per-LED power

```
Max power per LED (full white):  ~36 mW  (12V strip, 6W/m ÷ 160 LEDs/m)
Typical power (mixed colors):    ~18 mW  (50% average duty)
```

### 5.2 Total power

```
448 LEDs × 36 mW (max)  =  16.1 W  (1.34 A @ 12V)
448 LEDs × 18 mW (typ)  =   8.1 W  (0.67 A @ 12V)
```

### 5.3 Power supply recommendation

```
Supply voltage:     12V DC regulated
Minimum capacity:   2.0 A (24 W) — provides 50% headroom over max draw
Connector:          Barrel jack 5.5×2.1mm or screw terminal
```

### 5.4 Voltage drop consideration

At 1.34A total, the spine PCB VCC trace carries significant current. With 24 rows, the first row's trace carries current for all downstream rows.

```
VCC trace width:    1.0 mm minimum (1 oz copper: ~1.0A capacity per mm width)
Recommended:        1.5 mm width for the main VCC bus
GND trace:          same as VCC, or use ground plane pour
```

Consider injecting power at both ends of the spine (top and bottom) if brightness drops are visible on far rows.

---

## 6. Data Chain Wiring

### 6.1 Serpentine data path

The data signal (DIN/DOUT) chains through all 24 rows in a serpentine pattern. Each strip wraps around the circle — the start (DIN) exits from one side of the spine, and the end (DOUT) returns to the other side.

Alternating direction minimizes trace routing on the spine PCB:

```
Row  1: DIN ← LEFT side    strip wraps →    DOUT → RIGHT side
Row  2: DIN ← RIGHT side   strip wraps ←    DOUT → LEFT side
Row  3: DIN ← LEFT side    strip wraps →    DOUT → RIGHT side
Row  4: DIN ← RIGHT side   strip wraps ←    DOUT → LEFT side
  ...alternating through all 24 rows...
Row 23: DIN ← LEFT side    strip wraps →    DOUT → RIGHT side
Row 24: DIN ← RIGHT side   strip wraps ←    DOUT → LEFT side
```

### 6.2 Data bridge traces on spine PCB

Each DOUT→DIN bridge is a short vertical trace on the spine PCB:
```
Row  1 DOUT (right) → Row  2 DIN (right):  trace on right column, 6.25mm
Row  2 DOUT (left)  → Row  3 DIN (left):   trace on left column,  6.25mm
Row  3 DOUT (right) → Row  4 DIN (right):  trace on right column, 6.25mm
...and so on, alternating sides.
```

Data trace width: 0.3–0.5mm (low current signal)

### 6.3 Data input

```
DIN source:         Microcontroller (Raspberry Pi Zero 2W GPIO, via SPI)
Signal voltage:     3.3V logic → 12V strip may need level shifter
                    (WS2812B 12V variants often accept 3.3V — test first)
Data protocol:      WS2812B (800kHz single-wire, GRB color order)
Total pixels:       448 → refresh at 30fps: 448 × 24 bits × 800kHz = 13.4ms per frame ✓
```

---

## 7. Spine PCB Design

### 7.1 Board dimensions

```
Width:              15.0 mm (5mm left pads + 5mm trace channel + 5mm right pads)
Height:             148.75 mm  ((24-1) × 6.25 + 5.0)
Thickness:          1.6 mm (standard)
Layers:             2 (top copper + bottom copper)
```

### 7.2 Pad layout per row

Each row has two pad groups — LEFT and RIGHT — one for each end of the strip.

```
Left pad group (strip departure/return):
  Pad L1: VCC     X = 2.05 mm,  Y = row_y
  Pad L2: DATA    X = 3.50 mm,  Y = row_y
  Pad L3: GND     X = 4.95 mm,  Y = row_y

Right pad group (strip departure/return):
  Pad R1: VCC     X = 10.05 mm, Y = row_y
  Pad R2: DATA    X = 11.50 mm, Y = row_y
  Pad R3: GND     X = 12.95 mm, Y = row_y

Pad dimensions:     1.3 mm × 2.5 mm (SMD, rectangular)
Pad pitch:          1.45 mm center-to-center (within each group)
Pad clearance:      0.15 mm between adjacent pads
```

### 7.3 Row Y positions on spine PCB

Row 1 pads are at the top of the board. Row pitch = 6.25mm.

```
Row  1:  Y =   2.50 mm    (top edge + strip half-width)
Row  2:  Y =   8.75 mm
Row  3:  Y =  15.00 mm
Row  4:  Y =  21.25 mm
Row  5:  Y =  27.50 mm
Row  6:  Y =  33.75 mm
Row  7:  Y =  40.00 mm
Row  8:  Y =  46.25 mm
Row  9:  Y =  52.50 mm
Row 10:  Y =  58.75 mm
Row 11:  Y =  65.00 mm
Row 12:  Y =  71.25 mm
Row 13:  Y =  77.50 mm
Row 14:  Y =  83.75 mm
Row 15:  Y =  90.00 mm
Row 16:  Y =  96.25 mm
Row 17:  Y = 102.50 mm
Row 18:  Y = 108.75 mm
Row 19:  Y = 115.00 mm
Row 20:  Y = 121.25 mm
Row 21:  Y = 127.50 mm
Row 22:  Y = 133.75 mm
Row 23:  Y = 140.00 mm
Row 24:  Y = 146.25 mm
```

### 7.4 Trace routing

```
VCC bus:    1.5mm wide trace, runs vertically on top layer, left side
            From power input at bottom (or both ends) to all VCC pads
GND bus:    Ground plane pour on bottom layer, connected via vias
            Or: 1.5mm wide trace on top layer, right side
DIN chain:  0.4mm wide traces, serpentine between rows (see section 6.2)
            Alternates between right column and left column
```

### 7.5 PCB total pad count

```
Pad groups:         24 rows × 2 sides = 48 groups
Pads per group:     3 (VCC, DATA, GND)
Total solder pads:  144
```

### 7.6 Additional features

```
Power input:        3-pad or screw terminal footprint at board bottom
                    VCC, GND, DIN (from controller)
Mounting holes:     2× M2 holes at top and bottom board edges
                    Centers at (7.5, 0) and (7.5, 148.75)
Silkscreen:         Row numbers (1-24) next to each pad group
                    Data direction arrows (→ or ←) per row
                    "VCC" "GND" "DIN" labels on power input
Board outline:      Rounded corners, 1mm radius
```

---

## 8. Manufacturing Specifications

### 8.1 PCB order parameters

```
Layers:             2
Board thickness:    1.6 mm
Copper weight:      1 oz (2 oz if budget allows — better current handling)
Surface finish:     HASL (leaded or lead-free) — critical for hand soldering
Solder mask:        Black (or any color)
Silkscreen:         White
Min trace/space:    0.15 mm / 0.15 mm
Min drill:          0.3 mm (for vias)
Board dimensions:   15.0 mm × 148.75 mm
Panelization:       Not needed (single board)
```

### 8.2 Estimated cost (JLCPCB)

```
5× boards:          ~$2-5 USD (minimum order)
Shipping:           ~$5-15 USD depending on speed
Surface finish:     HASL = no extra charge (ENIG adds ~$7)
```

---

## 9. Assembly — Strip Cutting and Soldering

### 9.1 Tools required

```
Soldering iron:     Fine tip (chisel or conical, ≤2mm)
Solder:             0.5mm or 0.6mm diameter, leaded or lead-free
Flux:               Liquid or gel paste — essential for bridging pads
Solder wick:        For fixing bridges between adjacent pads
Calipers:           Digital, for verifying strip pad alignment
Kapton tape:        For holding strips in position during soldering
Magnification:      Loupe or magnifying lamp (pads are 1.3mm wide)
Sharp scissors:     For cutting strip at cut marks
Multimeter:         For continuity testing after soldering
```

### 9.2 Soldering procedure per row

1. **Tin spine PCB pads**: Apply flux, touch iron to each pad, add small solder dome
2. **Cut strip to length**: Count LEDs, cut exactly at cut-mark center line
3. **Identify strip direction**: Check data arrow — DIN end goes to the correct side per the serpentine pattern (odd rows: DIN left; even rows: DIN right)
4. **Align strip**: Place strip with pads over spine PCB pads, using silkscreen outline as guide
5. **Tape down**: Kapton tape one side, leaving pads exposed
6. **Solder first pad**: Touch iron to bridge strip pad to PCB pad dome
7. **Remove tape, solder remaining**: Complete all 3 pads on each end (6 solder joints per row)
8. **Inspect**: Shiny domes, no bridges between pads, no cold joints

### 9.3 Total solder joints

```
Per row:            6 joints (3 pads × 2 ends)
Total:              24 rows × 6 = 144 solder joints
Estimated time:     ~30 seconds per joint = ~72 minutes soldering time
```

---

## 10. Electrical Test Procedure

### 10.1 Before powering on

1. Check continuity: VCC to VCC across all rows (should be 0Ω through bus trace)
2. Check continuity: GND to GND across all rows
3. Check for shorts: VCC to GND should be open (or very high resistance)
4. Check data chain: probe DIN of Row 1 to DOUT of Row 24 — should show continuity through all strips

### 10.2 First power-on

1. Connect 12V supply with current limit set to 0.5A
2. All LEDs should remain off (no data signal yet)
3. Measure current draw — should be <50mA (idle, no LEDs lit)
4. If current is high → short circuit somewhere, power off immediately

### 10.3 First data test

1. Send a single-pixel test pattern: light pixel 0 red, then shift through all 448 pixels
2. Verify each row lights in the correct serpentine order
3. Check for skipped or stuck pixels — indicates a cold solder joint on that row's data connection
4. Send all-white at 50% brightness, measure total current (~0.7A expected)

---

## 11. Reference Dimensions Summary

```
CIRCLE:
  Diameter:         150.0 mm
  Radius:           75.0 mm
  Grid:             24 × 24 cells
  Cell size:        6.25 × 6.25 mm
  Active pixels:    448
  Active rows:      24

STRIP:
  Width:            5.0 mm
  Segment length:   6.25 mm
  Pads per cut:     3
  Pad width:        1.1 mm
  Pad pitch:        1.45 mm
  Total strip:      2.80 m

PCB PADS:
  Pad size:         1.3 × 2.5 mm
  Pad pitch:        1.45 mm
  Pad clearance:    0.15 mm
  Type:             SMD, rectangular, top layer

SPINE PCB:
  Width:            15.0 mm
  Height:           148.75 mm
  Total pads:       144 (48 groups × 3)
  Row pitch:        6.25 mm
  Trace widths:     VCC/GND 1.5mm, DATA 0.4mm
  Surface finish:   HASL

POWER:
  Voltage:          12V DC
  Max current:      1.34 A (all white)
  Typical current:  0.67 A (50% average)
  Supply rating:    2.0 A minimum (24W)
```
