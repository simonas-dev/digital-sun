# nanosun-pcb

Carrier PCB for the SunsetLamp 150 mm circular LED pixel display. Holds 24 rows of WS2812B strip, provides power distribution and data routing.

**Status:** Sent to PCBWay for fabrication (2026-04-12).

---

## Board summary

| Parameter | Value |
|---|---|
| Shape | Circle, 165 mm diameter |
| Layers | 2 (F.Cu + B.Cu) |
| Thickness | 1.6 mm FR-4 |
| Surface finish | HASL lead-free |
| Solder mask / silk | Black / White |
| Copper weight | 1 oz |

## Components on board

| Ref | Part | Purpose |
|---|---|---|
| LED1–LED24 | WS2812B strip row footprints | 24 horizontal strip mounting positions (6–24 pixels each) |
| U1 | MP1584EN module | 12V → 5V buck regulator (powers RPi logic) |
| R1 | 330 R (THT) | Data line series resistor |
| C1, C2 | 100 uF electrolytic (THT) | Bulk decoupling on 12V rail |
| MH1–MH6 | M3 mounting holes | Mechanical mounting |
| 12V, GND, DIN, 5V | Wire solder pads | External connections |

## Power architecture

- Two vertical buses on F.Cu: **+12V** (right side, x ~228 mm) and **GND** (center-left, x ~148 mm).
- Each LED row has horizontal stubs from one side's pads to its bus. The opposite side relies on the physical strip bridging internally when soldered.
- Power traces: 1.0 mm width. Signal traces (DIN/DOUT): 0.2 mm.
- Worst-case voltage drop at full load (1.67 A): ~246 mV, ~4 C temp rise.

## Data chain

Serpentine pattern: LED1 → LED2 → ... → LED24. Odd rows route left-to-right, even rows right-to-left. DIN enters at a wire solder pad, passes through R1, then into LED1.

## DRC status (at time of fab submission)

- **0 real connectivity issues.** 48 DRC "unconnected items" are all false positives from strip-pad bridging (KiCad can't model the internal strip connection).
- 15 warnings: 10 silk-over-copper (auto-clipped by fab), 4 text height (cosmetic), 1 dangling stub (0.28 mm, harmless).
- 0 ERC violations. 0 schematic parity issues.

## Fabrication outputs

Generated gerbers, drill files, and zip are in `fab/`. These are the exact files uploaded to PCBWay.

## PCBWay order parameters

| Parameter | Value |
|---|---|
| Board type | Single pieces |
| Bounding box | 165 x 165 mm |
| Layers | 2 |
| Material | FR-4 TG130 |
| Thickness | 1.6 mm |
| Min track/spacing | 6/6 mil (design uses 0.2 mm) |
| Min hole | 0.3 mm (design uses 0.8 mm) |
| Solder mask | Black |
| Silkscreen | White |
| Surface finish | HASL lead-free |
| Copper weight | 1 oz |

## Order reference

| Item | Cost (USD) |
|---|---|
| 5x PCBs (165x165 mm, 2-layer) | $51.11 |
| Shipping (DHL) | $31.59 |
| Bank fee | $4.57 |
| Discount | -$5.00 |
| VAT | $17.28 |
| **Total** | **$99.55** |

Order W1045739ASG1, PCBWay via Hong Kong Yanghui, 2026-04-12. Unit cost $10.22/board before shipping.

## Assembly notes

When boards arrive, follow the procedure in the parent directory's `design-spec.md`:

1. Tin all strip pads on the PCB.
2. Cut WS2812B strips to row lengths per the pixel circle spec (section 3.2).
3. Align strips on pads, tack one end, then solder remaining pads.
4. Continuity-test each row's VCC/GND before powering.
5. First power-on with a 0.5 A current limit to catch shorts.
