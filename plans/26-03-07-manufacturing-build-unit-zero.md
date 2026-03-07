# Manufacturing Plan — Digital Sun

> Agent: Manufacturing (Production & Supply Chain)
> Created: 2026-03-07
> Status: Draft — awaiting review

---

## Current State Assessment

- **Prototype not yet assembled**: No unit has been built end-to-end
- **BOM estimated but not validated**: Rough COGS $60-100 excluding enclosure
- **Assembly process undefined**: No documented build steps, no time estimate per unit
- **No supplier relationships**: All parts sourced ad-hoc (AliExpress, local electronics shops)
- **No QA process**: No test jig, no acceptance criteria per unit
- **Production location**: Assumed home workshop in Lithuania
- **Target scale unknown**: Founder hasn't committed to edition size

## Goal

Build the first unit end-to-end, document the assembly process, and produce a validated cost model at 10-unit and 50-unit scale.

## Plan

### 1. Build Unit Zero

Assemble one complete unit from raw parts to finished product (with enclosure).

- **Deliverable**: 1 working Digital Sun unit, fully assembled, ready to plug in and mount
- **Success Criteria**: Unit powers on, displays "Golden Dusk" shader, survives 8-hour continuous run, wall-mounts securely
- **Dependencies**: `electronics-engineer` assembles panel (their Task 2); `designer` provides enclosure design; `software-engineer` provides firmware
- **Priority**: Critical — validates everything

**Track during build**:
- Time per step (cutting, soldering, mounting, wiring, assembly, testing)
- Tools required
- Steps that are fiddly or error-prone
- Steps that could be templated or jig'd

### 2. Document the Assembly Process

Write a step-by-step build guide that someone else could follow.

- **Deliverable**: Assembly guide (numbered steps with time estimates, tools, and photos)
- **Success Criteria**: A technically competent person who has never seen the project could build a unit from this guide + BOM. Each step has a clear "done" check.
- **Dependencies**: Task 1 complete (learned from the first build)
- **Priority**: High — needed before batch production

**Estimated assembly time target**: <4 hours per unit for a practiced builder

### 3. Validate and Lock the BOM

Create the final BOM with real prices from real suppliers.

- **Deliverable**: BOM table with: component, spec, supplier, part number, unit cost at qty 1 / qty 10 / qty 50, MOQ, lead time
- **Success Criteria**: Total COGS is known to +/- 10% at each volume tier; no "TBD" entries remain
- **Dependencies**: Enclosure materials finalized (`designer`); electronics BOM from `electronics-engineer` (their Task 4)
- **Priority**: High — `pricing` agent is blocked without this

### 4. Define QA Process

Every unit needs to pass acceptance testing before shipping.

- **Deliverable**: QA checklist (10-15 checks)
- **Success Criteria**: Checklist catches the most common failure modes; can be completed in <15 minutes per unit
- **Dependencies**: Task 1 (know what can go wrong from first build)
- **Priority**: Medium — needed before first sale

**Draft QA checks**:
1. All 604 LEDs light up (test pattern sweep)
2. No dead pixels or flickering
3. No color shift at strip ends (power injection working)
4. Shader runs smoothly for 10 minutes (no crashes, no glitches)
5. Wall mount holds with gentle pull test
6. Diffuser seated correctly (no gaps, no rattling)
7. Cable exits cleanly, no pinching
8. Power draw under full white <200W
9. No audible coil whine from PSU
10. Visual inspection: no visible solder, no scratches on diffuser

### 5. Model Costs at 10 and 50 Units

Produce a cost model that `pricing` can use to set the retail price.

- **Deliverable**: Cost model spreadsheet/table showing per-unit COGS, packaging, and shipping at 10 and 50 units
- **Success Criteria**: Model includes all variable costs (parts, consumables, packaging, shipping supplies); labor cost estimated at hourly rate; identifies the biggest cost drivers
- **Dependencies**: Validated BOM (Task 3); packaging concept from `designer`
- **Priority**: High — feeds directly into pricing decision

**Cost categories**:

| Category | Includes |
|----------|----------|
| Electronics | LED strips, RPi, level shifter, PSU, wire, connectors |
| Enclosure | Frame material, diffuser, mounting hardware, back panel |
| Consumables | Solder, adhesive, heat shrink, flux |
| Packaging | Box, foam/insert, printed materials |
| Labor | Assembly time x hourly rate |
| Shipping supplies | Outer box, padding, label |

## Dependencies on Other Agents

| Agent | What I Need | When |
|-------|------------|------|
| electronics-engineer | Assembled and tested LED panel | Before Task 1 |
| electronics-engineer | Validated electrical BOM with part numbers | Before Task 3 |
| designer | Enclosure design with materials and dimensions | Before Task 1 |
| designer | Packaging concept | Before Task 5 |
| software-engineer | Working firmware deployed to RPi | Before Task 1 |
| founder | Target edition size (10? 50?) | Before Task 5 |
| pricing | Nothing — I feed into pricing | — |

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| First build takes much longer than expected | High | Medium | Expected — this is learning. Budget 2x estimated time for unit zero. |
| Soldering 22 strip-to-strip joints is the bottleneck | High | Medium | Design a solder jig (alignment template); consider pre-tinning all pads |
| LED strip reel has inconsistent color temperature | Medium | High | Order from a single batch; test a sample strip before committing |
| Enclosure adds more to COGS than expected | Medium | High | Start with simplest possible enclosure; iterate on aesthetics in Edition 2 |
| Shipping damage to diffuser or panel | Medium | High | Design packaging with foam cutout; test with a drop test before shipping real units |
