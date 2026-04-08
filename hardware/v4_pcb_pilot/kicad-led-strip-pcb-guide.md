# Designing the LED Strip Carrier PCB in **KiCad** — Step-by-Step

> KiCad version of the EasyEDA Pro guide. Tested mental-model against KiCad 7/8.
> The strip specs, pad geometry, and electrical decisions are identical to
> `easyeda-pro-led-strip-pcb-guide.md` — only the tool workflow differs.

## What's already generated for you

Two files in this directory are ready to drop into a KiCad project:

```
footprints/WS2812B_5mm_12V_3pad_segment.kicad_mod   ← the strip cut-point footprint
generate_spine_pcb.py                                ← emits sunsetlamp_spine.kicad_pcb
```

You can skip Phase 1 entirely if you use the provided footprint as-is. You can
skip most of Phase 4 if you use the generator script.

---

## Strip specs (recap)

```
Strip:        BTF-LIGHTING HD-12V-2020-160L-W-IP30-SPI
Cut pitch:    6.25 mm        Strip width:   5.0 mm
Pads/cut:     3 (VCC/DIN/GND, verify silkscreen on your physical strip)
Pad pitch:    1.45 mm        Pad size on PCB: 1.3 × 2.5 mm SMD rect
```

---

## Phase 1 — Footprint (skip if using the provided .kicad_mod)

### 1.1 Add the footprint to a library

If you don't already have a personal footprint library:

1. **Preferences → Manage Footprint Libraries → Project Specific Libraries**
2. Click the folder-plus icon, point at `hardware/v4_pcb_pilot/footprints/`,
   nickname `digital_sun`.

The provided `WS2812B_5mm_12V_3pad_segment.kicad_mod` will appear under that
nickname in the Footprint Editor and the PCB Editor's "Add Footprint" dialog.

### 1.2 If you want to recreate it from scratch

1. Open KiCad → **Footprint Editor** → **File → New Footprint**
2. Name `WS2812B_5mm_12V_3pad_segment`, pick your library.
3. Place 3 SMD pads using **Place → Pad** (shortcut **A**):
   ```
   Pad 1:  number=1, shape=Rect, size=1.3×2.5,  pos=(-1.45, 0),  layer=F.Cu/F.Paste/F.Mask
   Pad 2:  number=2, same size,                  pos=( 0.00, 0)
   Pad 3:  number=3, same size,                  pos=(+1.45, 0)
   ```
4. Switch to **F.Silkscreen**, draw a 6.25 × 5.0 mm rectangle around the pads
   (`-3.125,-2.5` to `+3.125,+2.5`).
5. Add `VCC / DIN / GND` text labels above each pad.
6. **File → Save**.

---

## Phase 2 — Symbol

KiCad keeps symbols and footprints separate (unlike EasyEDA Pro's bundled
Component editor). You make a symbol once, then *link* it to the footprint.

1. Open **Symbol Editor** → **File → New Symbol** in your personal library.
2. Name `WS2812B_Strip_Segment`, reference designator prefix `LED`.
3. Place 3 pins (**P** key):
   ```
   Pin 1:  Name=VCC,  Number=1,  Electrical type=Power input
   Pin 2:  Name=DIN,  Number=2,  Electrical type=Input
   Pin 3:  Name=GND,  Number=3,  Electrical type=Power input
   ```
4. Draw a small rectangle body around the pins.
5. **File → Symbol Properties → Footprint** → click the library icon → pick
   `digital_sun:WS2812B_5mm_12V_3pad_segment`.
6. Save.

> **Pin number ↔ pad number is the only binding** between symbol and footprint.
> Match them or your netlist will be wrong.

---

## Phase 3 — Schematic

1. **KiCad project window → New Project** → name e.g. `sunsetlamp_spine`.
2. Open the schematic (`.kicad_sch`).
3. Place 48 instances of `WS2812B_Strip_Segment` (24 rows × 2 sides — LEFT and
   RIGHT). Don't try to place one symbol per LED; only place one per *solder
   joint to the spine PCB*.
4. Wire VCC and GND with **net labels** (shortcut **L**) — drag a `VCC` label
   onto every VCC pin and a `GND` label onto every GND pin. Cleaner than
   spaghetti wires.
5. Wire DATA serpentine per spec section 6.1:
   - Row 1: external `DATA_IN` → row1 LEFT data pad
   - Row 1 RIGHT data → Row 2 RIGHT data (net `DATA_1_2`)
   - Row 2 LEFT data → Row 3 LEFT data (net `DATA_2_3`)
   - …alternating sides…
   - Row 24 outgoing → `DATA_OUT` (or leave unconnected if it's the chain end)
6. Place a `Conn_01x03` from the standard library for the controller input
   (VCC / GND / DATA_IN).
7. **Inspect → Electrical Rules Checker** → fix warnings.

> Tedious? Skip the schematic entirely and use the generator script (Phase 4).
> KiCad lets you have a PCB without a schematic; you just lose ERC and the
> "Update PCB from Schematic" workflow.

---

## Phase 4 — PCB Layout

### Option A: Use the generator (recommended)

```bash
cd hardware/v4_pcb_pilot
python3 generate_spine_pcb.py > sunsetlamp_spine.kicad_pcb
```

Open `sunsetlamp_spine.kicad_pcb` in KiCad's PCB Editor. You'll get:

- 15.0 × 148.75 mm board outline on `Edge.Cuts`
- 48 footprint instances at the exact (X, Y) from spec section 7.3
- Net assignments: VCC, GND, DATA_IN, DATA_OUT, DATA_1_2 … DATA_23_24
- 2× M2 mounting holes at top and bottom

What's still left for you:
- **Route the traces** (Phase 4.3 below) — the generator does not auto-route.
- **Run DRC** and fix any clearance warnings.
- Optionally add a ground plane fill on B.Cu (`Place → Add Filled Zone` → net
  `GND` → polygon over the whole board).

### Option B: Manual layout from a schematic

1. From the schematic, **Tools → Update PCB from Schematic** (F8).
2. KiCad imports all 48 footprints into a pile in the corner. Drag them into
   the spine layout, or use **Edit → Position Relative To** for exact coords:
   ```
   Row N, LEFT  side:  X =  3.50,  Y = 2.50 + (N-1) × 6.25
   Row N, RIGHT side:  X = 11.50,  Y = 2.50 + (N-1) × 6.25
   ```
3. Draw the board outline on `Edge.Cuts` as a 15.0 × 148.75 mm rectangle.

### 4.3 — Route traces

```
VCC bus:    1.5 mm trace, F.Cu, vertical down the left half of the board
GND bus:    Either matching 1.5 mm trace OR a B.Cu zone fill (recommended)
DATA bridges:  0.4 mm traces, alternating sides per row (see spec 6.2)
DATA_IN:    Routes to the controller input header at the bottom of the board
```

Tools: **Route → Route Single Track** (X), **Place → Add Filled Zone** for the
ground pour.

### 4.4 — DRC

**Inspect → Design Rules Checker**. Default rules are fine for JLCPCB. Confirm:
- Min trace width ≥ 0.15 mm
- Min clearance ≥ 0.15 mm
- No unconnected nets except `DATA_OUT` (expected — it's the chain terminus)

---

## Phase 5 — Manufacturing

```
Layers:           2
Thickness:        1.6 mm
Copper:           1 oz (2 oz nicer for the VCC bus current)
Surface finish:   HASL (NOT ENIG — pre-tinned pads make hand-soldering the strip easy)
Solder mask:      Black
Silkscreen:       White
Min trace/space:  0.15 / 0.15 mm
```

Generate fab files: **File → Fabrication Outputs → Gerbers** (and a separate
run for **Drill Files**). Zip the contents of the output folder and upload to
JLCPCB / PCBWay / OSH Park.

3D preview: **View → 3D Viewer** (Alt+3).

---

## EasyEDA Pro → KiCad cheat sheet

| Task                      | EasyEDA Pro                              | KiCad                                                  |
|---------------------------|------------------------------------------|--------------------------------------------------------|
| New footprint             | File → New → Footprint(Y)                | Footprint Editor → File → New Footprint               |
| New schematic symbol      | (bundled in Component editor)            | Symbol Editor → File → New Symbol                      |
| Bundle symbol + footprint | File → New → Component                   | Symbol Properties → set Footprint field                |
| New project               | File → New → Project(J)                  | KiCad project window → File → New Project              |
| Schematic → PCB           | Design → Update PCB                      | PCB Editor → Tools → Update PCB from Schematic (F8)    |
| Library panel             | "Library" / "Common Library"             | "Symbol Libraries" / "Footprint Libraries"             |
| Personal library setup    | Auto                                     | Preferences → Manage Symbol/Footprint Libraries        |
| Net label                 | Place → Net Label                        | Place → Net Label (L)                                  |
| Board outline layer       | Board Outline                            | Edge.Cuts                                              |
| Ground pour               | Place → Copper Area                      | Place → Add Filled Zone                                |
| DRC                       | Design → Design Rule Check               | Inspect → Design Rules Checker                         |
| Gerber export             | Fabrication → PCB Fabrication File       | File → Fabrication Outputs → Gerbers (+ Drill Files)   |

The conceptual model is the same. KiCad's main quirks vs EasyEDA Pro:
1. Symbols and footprints live in separate editors and libraries; you link them
   via the symbol's `Footprint` property field.
2. Library management is explicit — you must register libraries via
   Preferences before they appear in pickers.
3. There's no "one-click order" button — you generate Gerbers and upload them.

---

## Phase 6 — Soldering

Identical to the EasyEDA guide. See Phase 6 / sections 9.1–9.3 of
`easyeda-pro-led-strip-pcb-guide.md` and `sunsetlamp-pixel-circle-spec.md`.
