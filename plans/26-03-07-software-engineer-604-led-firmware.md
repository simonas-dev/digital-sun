# Software Engineer Plan — Digital Sun

> Agent: Software Engineer (Firmware, Shaders & Deployment)
> Created: 2026-03-07
> Status: Draft — awaiting review

---

## Current State Assessment

- **Firmware runs**: `target-rpi` builds and deploys via `deploy-rpi.sh`; dual-coroutine pipeline (shader + render) is functional
- **Stage.kt mismatch**: `create500Stage()` produces 292 logical pixels; hardware has 604 LEDs across 22 rows. Critical blocker.
- **Two shaders work on macOS**: `WarmColorShaderAlgorithm` and `V1RedShaderAlgorithm` run in OPENRNDR preview
- **Noise divergence**: OPENRNDR uses native simplex noise; RPi uses pure Kotlin Perlin. Visual output differs.
- **No test pattern**: No way to verify hardware wiring without running the full shader pipeline
- **Deploy works**: `deploy-rpi.sh` copies build to RPi, `setup-autostart.sh` configures systemd service
- **Build system healthy**: Gradle multi-module, platform-aware (macOS vs ARM), JDK 17+

## Goal

Get the firmware running correctly on 604 LEDs so the artist can see and tune "Golden Dusk" on physical hardware.

## Plan

### 1. Update Stage.kt to 604 LEDs

Replace `create500Stage()` with a `create604Stage()` that matches the hardware address map.

- **Deliverable**: New `create604Stage()` function in `Stage.kt` producing 604 pixel positions across 22 rows
- **Success Criteria**: Each pixel maps to the correct physical LED index (0-603); row widths match the hardware spec (T1=12, T2=18, ... L9=12); pixel x/y coordinates reflect the diamond layout geometry
- **Dependencies**: Hardware address map from `hardware/README.md` (already documented)
- **Priority**: Critical — blocks all hardware testing

**Implementation approach**:
- Define 22 rows with their LED counts: [12, 18, 22, 26, 28, 30, 32, 32, 34, 34, 34, 34, 34, 34, 32, 32, 30, 28, 26, 22, 18, 12]
- Calculate x-offset per row from the hardware spec's left-edge offset table
- Generate pixel positions with physical coordinates (mm-based) normalized to 0.0-1.0 range
- Maintain sequential LED index (0-603) for the data chain mapping

### 2. Add a Hardware Test Pattern

Create a simple test shader that sweeps through all 604 LEDs to verify wiring and addressing.

- **Deliverable**: `TestPatternShader` that lights LEDs sequentially (1 per frame) in solid green at low brightness
- **Success Criteria**: Running this shader on hardware lights exactly one LED at a time, in order from T1-LED0 through L9-LED11; any wiring error is immediately visible as a skip or wrong-order LED
- **Dependencies**: Task 1 (Stage.kt updated)
- **Priority**: Critical — needed before panel can be validated

**Additional test modes**:
- Row-by-row: light one full row at a time (verifies row boundaries)
- Full white at 10% brightness: verifies power injection (look for color shift at strip ends)

### 3. Add SHADER Environment Variable Support

Allow selecting shader at runtime without recompiling.

- **Deliverable**: `target-rpi/Main.kt` reads `SHADER` env var to select algorithm
- **Success Criteria**: `SHADER=warm`, `SHADER=red`, and `SHADER=test` all work; default is `warm`; invalid values print available options and exit
- **Dependencies**: Task 2 (test pattern exists)
- **Priority**: High — eliminates redeploy cycle for shader switching

### 4. Unify Noise Implementation

Document or resolve the noise divergence between OPENRNDR and RPi.

- **Deliverable**: Either (a) both platforms use the same pure-Kotlin Perlin implementation, or (b) documented comparison showing the visual difference is acceptable
- **Success Criteria**: Artist confirms that macOS preview is a reliable proxy for hardware output
- **Dependencies**: Hardware running (Tasks 1-2); `artist` evaluation
- **Priority**: Medium — important for development workflow but not blocking v1

**Options**:
- **Option A**: Replace OPENRNDR's native noise with `RpiNoiseGenerator` in `sun-openrndr` — guarantees parity
- **Option B**: Keep both; document that preview is "approximate" — simpler, may be good enough

### 5. Update LED Count Configuration

Remove hardcoded LED count; derive from Stage definition.

- **Deliverable**: `target-rpi/Main.kt` gets LED count from `Stage` rather than a magic number
- **Success Criteria**: Changing Stage definition automatically updates the LED count passed to `rpi_ws281x`; no separate constant to keep in sync
- **Dependencies**: Task 1
- **Priority**: Low — cleanup, prevents future mismatch bugs

## Dependencies on Other Agents

| Agent | What I Need | When |
|-------|------------|------|
| electronics-engineer | Physical panel assembled for testing | Before validating Tasks 1-2 on hardware |
| artist | Feedback on noise parity (Task 4) | After both platforms running |
| artist | ShaderParameters adjustments for hardware tuning | After Tasks 1-3 |

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Stage.kt coordinate mapping doesn't match physical layout | Medium | High | Print LED indices on OPENRNDR preview; verify visually against hardware spec diagram |
| Perlin noise too slow for 604 LEDs at 30fps on RPi Zero 2W | Low | High | Profile early; reduce FBM octaves from 8 to 4-5 if needed; 604 pixels is still small |
| OPENRNDR preview looks great but hardware looks bad | Medium | Medium | Task 2 (test pattern) catches addressing errors; Task 4 catches noise errors |
