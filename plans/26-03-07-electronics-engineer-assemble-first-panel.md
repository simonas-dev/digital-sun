# Electronics Engineer Plan — Digital Sun

> Agent: Electronics Engineer (Hardware Design & Electrical Systems)
> Created: 2026-03-07
> Status: Draft — awaiting review

---

## Current State Assessment

- **Hardware fully specified**: 604 LEDs, 22 rows, 12V WS2812B, 160 LEDs/m, diamond layout documented
- **Wiring designed**: Single data chain T1-L9, 4 power injection points, GPIO 10 (SPI MOSI)
- **Level shifter unresolved**: RPi GPIO outputs 3.3V; WS2812B threshold is ~3.5V at 5V VCC. No level shifter installed.
- **No physical panel assembled**: Strip cut list exists but strips not cut or soldered
- **RPi Zero 2W chosen**: Works but is a prototype component — supply-constrained, overkill for production
- **Power supply spec'd**: 12V/20A (240W) external brick required
- **No PCB designed**: All connections are hand-wired in prototype

## Goal

Assemble the first working 604-LED panel with reliable signal integrity, confirming the electrical design before any enclosure or production decisions.

## Plan

### 1. Solve the Level Shifter

Select, source, and test a 3.3V-to-5V level shifter for the data line.

- **Deliverable**: Working level shifter circuit on breadboard, tested with >=100 LEDs
- **Success Criteria**: Data signal clean at 800 kHz WS2812B protocol rate; no glitches over 1-hour continuous run; works at 20cm wire length from shifter to first LED
- **Dependencies**: None — can start immediately
- **Priority**: Critical — blocks panel assembly

**Recommended approach**: 74AHCT1G125 single-channel buffer
- Input threshold: 1.2V (easily triggered by 3.3V GPIO)
- Output: 5V logic level from VCC
- Cost: <$0.50
- Footprint: SOT-23-5, dead simple

**Alternative**: SN74HCT245N (8-channel, DIP package) — easier to prototype on breadboard, overkill but convenient.

### 2. Assemble the First Panel

Cut, mount, and wire 22 LED strips into the diamond layout on a flat substrate.

- **Deliverable**: Complete 604-LED panel on mounting board, all strips wired in data chain, 4 power injection points connected
- **Success Criteria**: All 604 LEDs light up in sequence with a test pattern; no dead LEDs; no flickering; power draw within spec (<181W at full white)
- **Dependencies**: Level shifter working (Task 1); LED strip reel in hand
- **Priority**: Critical — the entire project is waiting on this

**Assembly steps**:
1. Cut 22 strips per cut list in `hardware/README.md`
2. Mount strips on flat panel (aluminum composite or MDF) with adhesive backing
3. Solder data chain: DOUT of each strip to DIN of next, following T1→L9 order
4. Solder 4 power injection points (12V + GND at T1, U4, M1, L4)
5. Connect RPi GPIO 10 → level shifter → T1 DIN
6. Connect common GND: RPi + level shifter + PSU
7. Power on with low-brightness test pattern

### 3. Validate Signal and Power Integrity

Run the panel at full brightness and measure electrical behavior.

- **Deliverable**: Test report with voltage and current measurements
- **Success Criteria**: Voltage at furthest LED (L9) stays above 11V under full load; no color shifting at ends of long strips; data signal integrity confirmed (no random pixel flashing)
- **Dependencies**: Panel assembled (Task 2)
- **Priority**: High — validates the 4-point injection design

**Measurements needed** (check each independently — don't just eyeball the total system):
- Voltage at each injection point under load
- Total current draw at full white, at typical shader output
- Data signal waveform at T1 DIN and at M1 DIN (midpoint of chain)
- Temperature of strips after 30 minutes at full brightness
- Use code execution for voltage drop and current draw calculations — don't do power budget math mentally

### 4. Document Prototype Electrical BOM

Create a buyable BOM for the prototype — exact parts, quantities, suppliers.

- **Deliverable**: BOM table with part numbers, quantities, unit costs, supplier links
- **Success Criteria**: Another person could buy these parts and build the same panel
- **Dependencies**: Tasks 1-3 complete (validated choices)
- **Validation**: Have `manufacturing` independently price the same components. If costs agree, lock it. If they disagree, investigate — don't average.
- **Priority**: Medium — needed for `manufacturing` cost modeling

### 5. Evaluate Production MCU (Research Only)

Assess whether ESP32-S3 could replace RPi Zero 2W for production.

- **Deliverable**: Comparison document (RPi Zero 2W vs ESP32-S3 vs ESP32-C6)
- **Success Criteria**: Document covers: cost, availability, SPI/DMA capability for WS2812B, JVM support (or Kotlin/Native), GPIO count, power requirements
- **Dependencies**: None — research task, can run in parallel
- **Priority**: Low for v1 — important for Edition 2+

## Dependencies on Other Agents

| Agent | What I Need | When |
|-------|------------|------|
| software-engineer | Test pattern firmware (solid color, address sweep) | Before Task 2 |
| software-engineer | Stage.kt updated to 604 LEDs | Before Task 2 |
| designer | Substrate material decision (what to mount strips on) | Before Task 2 |
| manufacturing | Nothing yet — I feed into manufacturing | — |

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| LED strip batch has dead LEDs or inconsistent color | Medium | Medium | Order 10% extra strip length; test each strip segment before mounting |
| Solder joints fail under thermal cycling | Low | High | Use flux-core solder; strain-relief data wires at each joint |
| 3.3V signal works without level shifter (false confidence) | High | High | Always install level shifter — don't skip because "it seems to work" |
| RPi Zero 2W unavailable to purchase | Medium | Medium | Buy 2-3 units now as backup; start ESP32 evaluation early |

## Parts to Order Now

| Part | Qty | Est. Cost | Notes |
|------|-----|-----------|-------|
| WS2812B 12V strip, 160 LED/m, 5m reel | 1 | ~$25 | Need 3.8m, buy 5m for margin |
| SN74HCT245N (DIP-20) | 2 | ~$2 | Breadboard-friendly level shifter |
| 12V/20A PSU (240W) | 1 | ~$25 | Regulated, fan-cooled |
| 22 AWG silicone wire (red, black, green) | 1 set | ~$8 | Data + power injection |
| 14 AWG silicone wire (red, black) | 1 set | ~$6 | Main power feed |
| Aluminum composite panel 25x25cm | 1 | ~$10 | Mounting substrate |
| RPi Zero 2W | 1-2 | ~$15 ea | If not already in hand |
