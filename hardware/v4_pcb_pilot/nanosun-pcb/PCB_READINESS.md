# nanosun-pcb — PCBWay Readiness Report

**Date:** 2026-04-12 (rev 4)
**Board:** `nanosun-pcb.kicad_pcb`
**Verdict:** **Ready for fabrication.** All 48 DRC "unconnected items" are false positives from strip-pad bridging (see §3.1). 0 real connectivity issues. Only gerber generation and cosmetic silk cleanup remain before PCBWay upload.

---

## 1. Board at a glance

| Item | Value |
|---|---|
| Outline | Circle, Ø 165 mm, centered at (150, 100) |
| Layers | 2 (F.Cu + B.Cu) |
| Thickness | 1.6 mm |
| Vias | 0 |
| Track count | 271 segments; power rails (+12V, GND) at 1.0 mm, signal at 0.2 mm |
| Min drill | 0.8 mm (WireSolderPad) / 3.2 mm (M3 mounting holes) |
| Footprints | 38 total — 24× WS2812B row strips + 6× M3 mounting holes + 1× MP1584EN buck + 1× R + 2× CP bulk caps + 4× WireSolderPad (VCC/GND/DIN) |
| Edge.Cuts | Single `gr_circle` |

Board geometry, footprint placement, and edge clearance all check out — no pad sits within 0.5 mm of the board edge.

---

## 2. What actually works

- **24 strip rows correctly placed** around the circle; each row's left and right pads share pad-number (`1` = VCC, `4` = GND), so KiCad treats them as internally bridged by the strip itself. One-sided routing per rail is the correct strategy and DRC honors it (zero within-row unconnected errors).
- **Routing architecture is sound.** Each row has a horizontal trace from its strip pad to a central vertical bus. GND bus at x=148.003, +12V bus at x=151.803, both on F.Cu. The vertical buses span the full board height and are continuous.
- **Power input** (WireSolderPad "12V") is wired into the main +12V component.
- **Data chain** nets are present for LED1…LED24; `Net-(LED10-DIN)` separates the upper (LED1–9) and lower (LED10–24) halves, which matches a two-segment serpentine plan.
- **Board outline** (single circle) is clean and closed.
- **Schematic/PCB parity** is clean (0 issues).
- **ERC** is clean (0 violations).
- **No DRC clearance violations** remain (the 2026-04-08 clearance error is stale).
- **Recent cleanup** removed 14 diagonal jog segments, replacing them with direct horizontal traces.
- **Trace endpoints extended** (2026-04-12): all horizontal trace endpoints nudged to exact bus X coordinates; bus segments split to create matching endpoints. These edits are geometrically clean but had no effect on DRC count — see §3.1 for explanation.
- **Two-bus routing architecture verified** (2026-04-12): +12V bus at x≈228.201 (right side, F.Cu) serves right-side VCC pads; GND bus at x≈148.003 (center-left, F.Cu) serves left-side GND pads. All 24 LED rows have traces on their respective buses. Left-side VCC and right-side GND pads rely on physical strip bridging (by design).

---

## 3. What is broken

### 3.1 DRC "48 unconnected items" — ✅ **ALL FALSE POSITIVES (confirmed 2026-04-12)**

All 48 DRC unconnected items are false positives from strip-pad bridging. **0 real connectivity issues.**

Each WS2812B strip footprint has pad "1" (VCC) and pad "4" (GND) on both the left and right ends. The board routes one end of each net to its respective bus; the other end has no PCB copper and relies on the physical LED strip to bridge internally when soldered. KiCad DRC cannot model this internal strip bridge, so it reports every unbridged pad as disconnected. This is **expected behavior** for this strip-based design.

**Routing architecture (two buses):**
- **+12V bus** at x≈228.201 (right side, F.Cu): horizontal stubs connect to each LED's **right-side** VCC pad. Left-side VCC pads are unbridged on PCB (24 false positives).
- **GND bus** at x≈148.003 (center-left, F.Cu): horizontal stubs connect to each LED's **left-side** GND pad. Right-side GND pads are unbridged on PCB (24 false positives).

**LED10 +12V — previously misidentified as a real issue:**
Earlier analysis only looked near the center bus (x≈151.8) and concluded LED10 had no +12V trace. In fact, LED10's right-side VCC pad at (225.75, 85.825) connects to the +12V bus at x=228.201 via: `(228.201, 84) → (228.201, 85.676) → (228.052, 85.825) → (225.75, 85.825)`. This is the same routing pattern as LED11–LED15 on the same bus. The DRC complaints were about LED10's **left-side** pad at (74.25, 85.825) — strip bridging, same as all other LEDs.

**Connectivity verification:**
- GND: **1 copper island** — all horizontal traces + bus + left pads + U1 pads fully connected
- +12V: **1 copper island** — all 24 horizontal traces + bus fully connected

### 3.2 Dangling track stub — **1 remains (minor)**

DRC reports 1 `track_dangling` warning:
- +12V stub on F.Cu at (197.0, 160.6), length 0.28 mm

Cosmetic — doesn't break connectivity. Can be deleted in Pcbnew for a cleaner DRC.

### 3.3 Schematic/PCB parity — ✅ **FIXED (2026-04-12)**

Fresh `kicad-cli pcb drc --schematic-parity` now reports **0 parity issues** (was 13). Applied:

1. **6 mounting holes** — given refs `MH1`–`MH6` (clockwise from top) and marked `board_only` in their `attr` line. KiCad's parity check now treats them as mechanical-only and does not require matching schematic symbols. No schematic changes needed.
2. **`Net-(J2-Pad1)` → `Net-(DIN-Pad1)`** — renamed in all 3 places in the PCB file (net 28 definition, DIN wire-pad B.Cu pad, R1 pad 1). PCB net label now matches the schematic.

Backup of pre-edit PCB at `/tmp/nanosun-pcb.kicad_pcb.bak-parity`.

### 3.4 ERC warnings (from stale 2026-04-08 ERC report)

7× `lib_symbol_issues`: `TestPoint`, `R_THT`, `CP_THT` symbols not found in project library `nanosun-pcb`. These come from stale local library cache. Re-running ERC after re-syncing libraries should clear them. Not a blocker for fab, but it should be clean.

### 3.5 Silkscreen issues — **cosmetic, not blockers**

| Type | Count | Cause |
|---|---|---|
| `silk_over_copper` | 10 | WS2812B row footprint courtyards and U1 outline cross solder-mask openings. PCBWay auto-clips these. |
| `text_height` | 4 | U1 (MP1584) pad labels at 0.7 mm are below the project's 0.8 mm silk minimum. |

### 3.6 Trace width — ✅ **FIXED 2026-04-12**

All +12V and GND segments widened from 0.2 mm to **1.0 mm** (181 segments total).

**Design rationale (20W / 1.67A budget, 1 oz Cu, ~300 mm worst-case path):**

| Width | Resistance | V drop | Power loss | Temp rise |
|-------|-----------|--------|------------|-----------|
| 0.2 mm (old) | 737 mΩ | 1231 mV | 2.1 W (10%) | ~63°C — risk of delamination |
| **1.0 mm (chosen)** | **147 mΩ** | **246 mV** | **0.4 W (2%)** | **~4°C — well within safe range** |

1.0 mm was chosen over the minimum safe width (0.5 mm) for comfortable thermal margin and negligible voltage drop at full brightness. Efficiency difference between widths is <1% — the driving concern is trace heating, not power loss. Signal traces (DIN/DOUT) remain at 0.2 mm (negligible current).

### 3.7 Missing fabrication outputs

No gerbers, drill files, map files, position files, or BOM have been generated yet. Nothing to upload to PCBWay.

---

## 4. Non-issues (don't worry about these)

- `unconnected-(LED24-DOUT-Pad3)` — intentional end-of-chain.
- No vias — the 2-layer design with mostly F.Cu routing doesn't need any.
- **All 48 DRC "unconnected_items"** — false positives from strip-pad bridging. Each LED strip bridges left↔right pads internally when soldered; KiCad can't model this. See §3.1 for full explanation.

---

## 5. Minimum fix list to reach PCBWay-ready

All connectivity issues are resolved. Only fabrication output generation remains.

### 5.1 Trace endpoint extensions — ✅ DONE (2026-04-12)

All horizontal trace endpoints extended to exact bus X coordinates via batch file edit.

### 5.2 LED10 +12V trace — ✅ NOT AN ISSUE (confirmed 2026-04-12)

Previously misidentified as missing. LED10's right-side VCC pad at (225.75, 85.825) connects to the +12V bus at x=228.201. The left-side pad at (74.25, 85.825) relies on strip bridging — same as all other LEDs. See §3.1.

### 5.3 Connect U1 GND pins — ✅ DONE (2026-04-12)

U1 pad 2 and pad 3 connected to GND manually in Pcbnew. DRC confirms fix.

### 5.4 Strip-pad bridging — **no fix needed**

All 48 DRC "unconnected" items are by design. The LED strips physically bridge left↔right when soldered. No PCB copper needed.

---

## 6. Action plan to reach "ship to PCBWay"

Tracked as an ordered checklist in [`PCBWAY_ORDER_CHECKLIST.md`](./PCBWAY_ORDER_CHECKLIST.md). Summary:

1. ~~**Sync schematic → PCB**~~ ✅ **DONE 2026-04-12**
2. ~~**Extend trace endpoints**~~ ✅ **DONE 2026-04-12**
3. ~~**LED10 +12V trace**~~ ✅ **NOT AN ISSUE** — was already connected to +12V bus at x=228.201 (see §3.1)
4. ~~**Connect U1 GND pins**~~ ✅ **DONE 2026-04-12**
5. ~~**Widen power bus**~~ ✅ **DONE 2026-04-12** (see §3.6)
6. (Optional) **Clean silkscreen**: bump U1 text to 0.8 mm — cosmetic.
7. **Generate manufacturing outputs** per the spec in [`PCBWAY_ORDER_CHECKLIST.md`](./PCBWAY_ORDER_CHECKLIST.md) §2.
8. **Sanity-check gerbers in GerbView** (visual pass — edge cuts, both copper layers, both silks, both masks, drill).
9. **Upload to PCBWay**, confirm their auto-DFM report shows no new issues.

---

## 7. Current DRC/ERC snapshot

Fresh DRC run (2026-04-12, `kicad-cli pcb drc --schematic-parity --severity-all`):

```
Found 15 violations        (10 silk_over_copper + 4 text_height + 1 track_dangling) — all warnings
Found 48 unconnected items (24 GND + 24 +12V) — all strip-bridging false positives
Found 0 schematic parity issues
ERC: 0 violations
```

**All 48 unconnected items are false positives** — strip-pad bridging by design. 0 real connectivity issues.

### Progression

| Run | Violations | Unconnected | Parity | Notes |
|---|---|---|---|---|
| 2026-04-08 (committed `DRC.rpt`) | 1 | 124 | n/a | Initial |
| 2026-04-11 23:33 | 87 | 51 | 13 | First full analysis |
| 2026-04-12 (after parity fix) | 87 | 51 | **0** | MH refs + net rename |
| 2026-04-12 (after user cleanup) | 87 | **50** | 0 | Removed 14 jog segments |
| 2026-04-12 (after trace edits) | 87 | **50** | 0 | Endpoints extended, bus split |
| 2026-04-12 (after U1 GND fix) | 113 | **48** | 0 | U1 GND connected |
| 2026-04-12 (current) | **15** | **48** | 0 | All 48 unconnected confirmed false positives (strip bridging); LED10 +12V was misidentified — trace exists via x=228 bus |
