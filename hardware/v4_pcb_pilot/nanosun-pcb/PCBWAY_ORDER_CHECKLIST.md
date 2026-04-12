# PCBWay Order Checklist — nanosun-pcb

Companion to [`PCB_READINESS.md`](./PCB_READINESS.md). Walk this top-to-bottom; do not skip the verification steps.

---

## 1. Design fixes (blocking)

### 1.1 Extend trace endpoints to bus — ✅ DONE 2026-04-12

Batch file edit applied: all 22 horizontal trace endpoints extended to exact bus X coordinates, 16 bus segments split to create matching endpoints, GND bus gap closed. No DRC count change — the 50 unconnected items are from right-side strip pad bridging (by design), not from trace endpoints. See `PCB_READINESS.md` §3.1.

### 1.1b Route LED10 +12V trace — ✅ NOT AN ISSUE (confirmed 2026-04-12)

- [x] Previously misidentified as missing. LED10's right-side VCC pad at (225.75, 85.825) connects to the +12V bus at x=228.201 via three segments. The left-side pad relies on strip bridging — same as all other LEDs. See `PCB_READINESS.md` §3.1.

### 1.1c Connect U1 GND pins — ✅ DONE 2026-04-12

- [x] **GND → U1 pad 2** (MP1584 GND @ (185, 109.5) B.Cu)
- [x] **GND → U1 pad 3** (MP1584 GND @ (185, 90.5) B.Cu)

Connected manually in Pcbnew. DRC confirms U1 GND pads no longer appear as unconnected.

### 1.2 Remove dangling stub (optional)

- [ ] Delete +12V F.Cu stub at (197.0, 160.6), length 0.28 mm

### 1.3 Sync schematic → PCB (clears the 13 parity errors) — ✅ DONE 2026-04-12

- [x] 6 mounting holes given refs `MH1`–`MH6` (clockwise from top) and marked `board_only` in `attr` line. No schematic changes needed — `board_only` tells KiCad's parity check to treat them as mechanical-only.
- [x] PCB net `Net-(J2-Pad1)` renamed to `Net-(DIN-Pad1)` in all 3 places (net 28 definition + 2 pad references) to match the schematic's post-rename naming.
- [x] DRC parity re-run: **0 parity issues** (was 13).
- Backup: `/tmp/nanosun-pcb.kicad_pcb.bak-parity`

### 1.4 Power netclass (strongly recommended)

- [ ] Board Setup → Net Classes: add `Power` class with:
  - `track_width`: 1.0 mm (bump to 1.5 mm if there's room)
  - `clearance`: 0.2 mm
- [ ] Assign `+12V` and `GND` nets to `Power`.
- [ ] Re-route or widen (Edit → Select → Same Net; Properties → 1.0 mm) the existing +12V / GND segments.

### 1.5 Silkscreen cleanup (optional but recommended)

- [ ] Shrink each `WS2812B_Row_*.kicad_mod` F.SilkS courtyard rectangle to sit *inside* the strip pads (offset inward by ~0.2 mm) so it stops crossing the solder-mask openings. Touch every footprint: `06px_37_5mm`, `12px_75mm`, `14px_87_5mm`, `16px_100mm`, `18px_112_5mm`, `20px_125mm`, `22px_137_5mm`, `24px_150mm`.
- [ ] Bump U1 (`MP1584EN_Module.kicad_mod`) pad-label text from 0.7 mm → 0.8 mm.
- [ ] Trim the 4× B.Silks cardinal segments clipped by Edge.Cuts (or accept them as clipped).

### 1.6 Re-verify

- [ ] ERC clean (0 errors; warnings acceptable):
  ```bash
  /Applications/KiCad/KiCad.app/Contents/MacOS/kicad-cli sch erc \
    --exit-code-violations nanosun-pcb.kicad_sch
  ```
- [ ] DRC with schematic parity:
  ```bash
  /Applications/KiCad/KiCad.app/Contents/MacOS/kicad-cli pcb drc \
    --schematic-parity --severity-all --output DRC.rpt nanosun-pcb.kicad_pcb
  ```
  **Expected:** 0 error-severity violations, ~15 warnings (10 silk_over_copper + 4 text_height + 1 track_dangling), **48 unconnected** (all strip-pad bridging false positives — by design), 0 parity issues.

---

## 2. Manufacturing output generation

### 2.1 Gerber files

KiCad CLI (from the project directory):

```bash
KCLI=/Applications/KiCad/KiCad.app/Contents/MacOS/kicad-cli
mkdir -p fab/gerbers

$KCLI pcb export gerbers \
  --output fab/gerbers/ \
  --layers "F.Cu,B.Cu,F.Paste,B.Paste,F.Silkscreen,B.Silkscreen,F.Mask,B.Mask,Edge.Cuts" \
  --subtract-soldermask \
  --no-x2 \
  --no-netlist \
  nanosun-pcb.kicad_pcb
```

(PCBWay's gerber importer prefers `--no-x2`; the file names KiCad produces are already acceptable.)

- [ ] Gerbers generated; 9 files present in `fab/gerbers/`.

### 2.2 Drill files (Excellon)

```bash
$KCLI pcb export drill \
  --output fab/gerbers/ \
  --format excellon \
  --drill-origin absolute \
  --excellon-units mm \
  --excellon-zeros-format decimal \
  --generate-map --map-format gerberx2 \
  nanosun-pcb.kicad_pcb
```

- [ ] `.drl` file + drill map generated.

### 2.3 Pack and verify

```bash
cd fab && zip -r nanosun-pcb-gerbers.zip gerbers/
```

- [ ] Open `nanosun-pcb-gerbers.zip` in **GerbView** (comes with KiCad) and visually confirm:
  - [ ] Edge.Cuts is a single closed circle
  - [ ] F.Cu and B.Cu show the expected routing (no breaks where the 6 bridges were added)
  - [ ] F.Mask has openings on every strip pad
  - [ ] F.Silkscreen is readable and not dashed across pads
  - [ ] Drill file shows 6× Ø3.2 mm mounting holes + 6× Ø0.8 mm WireSolderPad holes
- [ ] (optional) BOM:
  ```bash
  $KCLI sch export bom --output fab/bom.csv nanosun-pcb.kicad_sch
  ```

### 2.4 Pick-and-place (not needed for bare-board order)

Only generate if you're ordering assembled PCBs from PCBWay. Skip for a plain fab order.

---

## 3. PCBWay order parameters

Paste/select these on the PCBWay "Instant Quote" page:

| Parameter | Value | Notes |
|---|---|---|
| Board type | Single pieces | |
| Size | 165 × 165 mm (circular) | PCBWay uses bounding box |
| Quantity | 5 or 10 | Minimum order, cheapest tier |
| Layers | 2 | |
| Material | FR-4, TG130 | Standard |
| Thickness | 1.6 mm | |
| Min track/spacing | 6/6 mil (0.15 mm) | Our design uses 0.2 mm |
| Min hole size | 0.3 mm | Ours is 0.8 mm smallest |
| Solder mask | Black (or any) | Spec calls for black |
| Silkscreen | White | |
| Surface finish | **HASL with lead-free** | Critical for hand-soldering strips |
| Copper weight | **1 oz** (bump to 2 oz if widening power bus is inconvenient) | |
| Edge connector | No | |
| Castellated holes | No | |
| Gold fingers | No | |
| Different designs in one order | No | |
| Impedance control | No | |
| Flying probe test | Yes (included) | |

**Expected price (reference):** ~$5–15 for 5 pcs, plus ~$15–25 shipping depending on method. Assembly not included.

---

## 4. Before clicking "Add to cart"

- [ ] PCBWay's DFM/auto-check (runs after upload) reports 0 errors.
- [ ] Re-opened the generated gerbers one more time in GerbView to confirm the board you see matches the board you intended.
- [ ] Committed the final `.kicad_pcb`, `.kicad_sch`, and `fab/` directory to git with a tag like `v4-pilot-fab-r1` so you can reproduce the order if it needs a re-spin.
- [ ] Saved the PCBWay order number / file hash in `hardware/v4_pcb_pilot/README` or similar for traceability.

---

## 5. Post-order

- [ ] Mark `hardware/v4_pcb_pilot/` as frozen; any further changes go to `v5_...` or a `-r2` revision.
- [ ] When the boards arrive: follow the assembly and test procedure in `sunsetlamp-pixel-circle-spec.md` §9–§10 (tin pads, cut strips, tape, solder, continuity test, 0.5 A limited first power-on).
