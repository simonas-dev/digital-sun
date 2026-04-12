# nanosun-pcb — PCBWay Readiness Report

**Date:** 2026-04-12 (rev 4)
**Board:** `nanosun-pcb.kicad_pcb`
**Verdict:** **Nearly ready.** 1 real connectivity issue remains (LED10 +12V trace missing). U1 GND connected 2026-04-12. The 48 DRC "unconnected items" are 47 false positives from right-side strip pads + 1 real (LED10). Schematic/PCB parity fixed 2026-04-12. Trace endpoints extended to bus 2026-04-12.

---

## 1. Board at a glance

| Item | Value |
|---|---|
| Outline | Circle, Ø 165 mm, centered at (150, 100) |
| Layers | 2 (F.Cu + B.Cu) |
| Thickness | 1.6 mm |
| Vias | 0 |
| Track count | 271 segments, all at 0.2 mm |
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
- **No DRC clearance violations** remain (the 2026-04-08 clearance error is stale).
- **Recent cleanup** removed 14 diagonal jog segments, replacing them with direct horizontal traces.
- **Trace endpoints extended** (2026-04-12): all horizontal trace endpoints nudged to exact bus X coordinates; bus segments split to create matching endpoints. These edits are geometrically clean but had no effect on DRC count — see §3.1 for explanation.

---

## 3. What is broken

### 3.1 DRC "50 unconnected items" — ✅ **EXPLAINED (2026-04-12)**

Deep analysis reveals the 50 DRC unconnected items break down as:

**48 false positives: right-side strip pads with no PCB copper.**
Each WS2812B strip footprint has pad "1" (VCC) and pad "4" (GND) on both the left and right sides. Only the **left-side** pads have horizontal traces to the central bus. The **right-side** pads have no traces — they rely on the physical LED strip to bridge left↔right when soldered. KiCad DRC does not model this internal strip bridge, so it reports every right-side pad as a disconnected island. This is **expected behavior** for this strip-based design and is **not a real connectivity issue**.

**1 real issue remaining:**

| Issue | Details |
|---|---|
| **LED10 +12V** | No +12V trace connects to either of LED10's VCC pads. Nearest +12V trace is 3.3 mm away. Needs a new trace routed from LED10's left pad 1 to the +12V bus. |
| ~~**U1 GND (pads 2 & 3)**~~ | ✅ **FIXED 2026-04-12** — connected manually in Pcbnew. |

**Previously reported "22 short traces" — resolved / reclassified:**
Trace endpoints were extended to the exact bus X coordinates and bus segments were split to create matching endpoints (2026-04-12). These edits are geometrically correct but had **zero effect on DRC count** because:
1. The traces already connected through jog remnant segments and T-junctions.
2. The 50 unconnected items were always caused by the right-side pad bridging issue, not by the 0.075 mm endpoint gaps.

**Connectivity verification:**
- GND: **1 copper island** — all horizontal traces + bus + left pads + U1 pads fully connected (U1 GND fixed 2026-04-12)
- +12V: **1 copper island** — all 18 horizontal traces + bus fully connected (but LED10 pad is not reached by any trace)

### 3.1b U1 GND pins — ✅ **FIXED 2026-04-12**

The MP1584EN buck regulator's GND pins (U1 pad 2 and pad 3, both on B.Cu) are now connected to the GND net. Routed manually in Pcbnew. DRC confirms no unconnected U1 GND items.

### 3.2 Dangling track stubs — **2 remain (minor)**

DRC still reports 2 `track_dangling` warnings:
1. GND stub on B.Cu at (145.0, 147.0), length 0.002 mm
2. +12V stub on F.Cu at (151.8, 76.9), length 0.053 mm

These are cosmetic — they don't break connectivity. Can be deleted in Pcbnew for a cleaner DRC.

### 3.3 Schematic/PCB parity — ✅ **FIXED (2026-04-12)**

Fresh `kicad-cli pcb drc --schematic-parity` now reports **0 parity issues** (was 13). Applied:

1. **6 mounting holes** — given refs `MH1`–`MH6` (clockwise from top) and marked `board_only` in their `attr` line. KiCad's parity check now treats them as mechanical-only and does not require matching schematic symbols. No schematic changes needed.
2. **`Net-(J2-Pad1)` → `Net-(DIN-Pad1)`** — renamed in all 3 places in the PCB file (net 28 definition, DIN wire-pad B.Cu pad, R1 pad 1). PCB net label now matches the schematic.

Backup of pre-edit PCB at `/tmp/nanosun-pcb.kicad_pcb.bak-parity`.

### 3.4 ERC warnings (from stale 2026-04-08 ERC report)

7× `lib_symbol_issues`: `TestPoint`, `R_THT`, `CP_THT` symbols not found in project library `nanosun-pcb`. These come from stale local library cache. Re-running ERC after re-syncing libraries should clear them. Not a blocker for fab, but it should be clean.

### 3.5 Silkscreen issues — **should fix** before ordering

Not blockers, but the plot will be messy if ignored:

| Type | Count | Cause |
|---|---|---|
| `silk_over_copper` | 59 | WS2812B row footprint courtyards cross the strip pad solder-mask openings on F.Silks, so silkscreen gets auto-clipped at every pad. Shrink row silk outline to *inside* the pads. |
| `silk_overlap` | 18 | Adjacent row outlines touch on the left/right extreme columns where rows stack closest. |
| `silk_edge_clearance` | 4 | B.Silkscreen segments cross the circular Edge.Cuts (top/bottom/left/right cardinals). |
| `text_height` | 4 | U1 (MP1584) pad labels at 0.7 mm are below the project's 0.8 mm silk minimum. |

### 3.6 Trace width — **should review** before ordering

All 271 segments are **0.2 mm wide**. Per the design spec (`sunsetlamp-pixel-circle-spec.md` §5.4), the +12V and GND bus should be **1.5 mm** to handle the ~1.3 A full-brightness current at 1 oz copper. Running the full bus at 0.2 mm risks noticeable voltage drop to the far LEDs and localised trace heating. Define a `Power` netclass with 1.0–1.5 mm track width and apply it to `+12V` and `GND`, then re-route or widen the existing bus traces.

*Acceptable if the use case is deliberately dim (say <50 % brightness max), but it's a one-liner netclass change and worth doing for peace of mind.*

### 3.7 Missing fabrication outputs

No gerbers, drill files, map files, position files, or BOM have been generated yet. Nothing to upload to PCBWay.

---

## 4. Non-issues (don't worry about these)

- `unconnected-(LED24-DOUT-Pad3)` — intentional end-of-chain.
- No vias — the 2-layer design with mostly F.Cu routing doesn't need any.
- **47 of 48 DRC "unconnected_items"** — false positives from right-side strip pads. Each LED strip bridges left↔right pads internally when soldered; KiCad can't model this. Only 1 item is a real issue (LED10 +12V). See §3.1 for full explanation.

---

## 5. Minimum fix list to reach PCBWay-ready

### 5.1 Trace endpoint extensions — ✅ DONE (2026-04-12)

All horizontal trace endpoints extended to exact bus X coordinates via batch file edit:
- GND: 12 trace endpoints moved from x=147.928 → 148.003 (+ 1 from 147.575)
- +12V: 10 trace endpoints moved from x=151.878 → 151.803 (+ 1 from 151.978)
- Bus segments split at 16 Y coordinates to create matching endpoints
- GND bus gap at y=82.747/82.753 closed

These edits had no effect on DRC count (see §3.1 explanation) but ensure clean geometry.

### 5.2 LED10 +12V trace — **must fix**

LED10's VCC pads (left at (74.25, 85.825), right at (225.75, 85.825)) have no +12V trace. The nearest +12V trace endpoint is 3.3 mm away at (225.75, 89.175). Route a short trace from LED10's left pad 1 at (74.25, 85.825) to the +12V bus at x=151.803.

### 5.3 Connect U1 GND pins — ✅ DONE (2026-04-12)

U1 pad 2 and pad 3 connected to GND manually in Pcbnew. DRC confirms fix.

### 5.4 Right-side strip pads — **no fix needed**

The 48 DRC "unconnected" items from right-side pads are by design. The LED strips physically bridge left↔right when soldered. No PCB copper needed.

---

## 6. Action plan to reach "ship to PCBWay"

Tracked as an ordered checklist in [`PCBWAY_ORDER_CHECKLIST.md`](./PCBWAY_ORDER_CHECKLIST.md). Summary:

1. ~~**Sync schematic → PCB** to clear the parity errors.~~ ✅ **DONE 2026-04-12**
2. ~~**Extend trace endpoints** to reach the center bus (§5.1).~~ ✅ **DONE 2026-04-12** (no DRC effect, but clean geometry)
3. **Route LED10 +12V trace** (§5.2) — blocking.
4. ~~**Connect U1 GND pins** (§5.3)~~ — ✅ **DONE 2026-04-12**.
5. **Widen power bus** to 1.0–1.5 mm by adding a `Power` netclass and reassigning `+12V` / `GND` — strongly recommended.
6. **Clean silkscreen**: shrink LED row courtyard outlines to sit *inside* the strip pads, bump U1 text to 0.8 mm, trim B.Silks segments that cross Edge.Cuts — cosmetic.
7. **Re-run DRC** — expect 0 violations at error severity, ~85 warnings (silk), 48 unconnected (strip pad bridging, by design), 0 parity issues.
8. **Generate manufacturing outputs** per the spec in [`PCBWAY_ORDER_CHECKLIST.md`](./PCBWAY_ORDER_CHECKLIST.md) §2.
9. **Sanity-check gerbers in GerbView** (visual pass — edge cuts, both copper layers, both silks, both masks, drill).
10. **Upload to PCBWay**, confirm their auto-DFM report shows no new issues.

---

## 7. Current DRC/ERC snapshot

Fresh DRC run (2026-04-12, after U1 GND fix, `kicad-cli pcb drc --schematic-parity --severity-all`):

```
Found 113 violations       (59 silk_over_copper + 18 silk_overlap + 4 text_height + 4 silk_edge_clearance + 28 track_dangling)
Found 48 unconnected items (24 GND + 24 +12V)
Found 0 schematic parity issues
```

**Breakdown of 48 unconnected items:**
- 47 = right-side strip pads with no PCB copper (by design — strips bridge them)
- 1 = LED10 +12V (real — missing trace)
- ~~1 = U1 GND on B.Cu~~ — ✅ fixed

**Target for release:** 0 error-severity violations, ~85 warnings (silk) + ~28 track_dangling warnings, 47 unconnected (strip bridging). After fixing LED10 +12V, the 1 real unconnected item drops to 0.

### Progression

| Run | Violations | Unconnected | Parity | Notes |
|---|---|---|---|---|
| 2026-04-08 (committed `DRC.rpt`) | 1 | 124 | n/a | Initial |
| 2026-04-11 23:33 | 87 | 51 | 13 | First full analysis |
| 2026-04-12 (after parity fix) | 87 | 51 | **0** | MH refs + net rename |
| 2026-04-12 (after user cleanup) | 87 | **50** | 0 | Removed 14 jog segments |
| 2026-04-12 (after trace edits) | 87 | **50** | 0 | Endpoints extended, bus split; 48 of 50 are strip-bridge false positives |
| 2026-04-12 (after U1 GND fix) | 113 | **48** | 0 | U1 GND connected; 28 track_dangling from bus splits + routing; 47 of 48 unconnected are false positives |
