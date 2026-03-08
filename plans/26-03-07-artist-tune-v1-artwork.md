# Artist Plan — Digital Sun

> Agent: Artist (Creative Direction & Aesthetic Vision)
> Created: 2026-03-07
> Status: Draft — awaiting review

---

## Current State Assessment

- **Two works exist**: "Golden Dusk" (warm shader) and "Ember" (red shader)
- **Neither seen on hardware**: Both shaders run on macOS OPENRNDR preview only; no physical LED panel assembled
- **Visual language drafted**: Warmth (280-60 hue), breath-like rhythm (0.1-0.3 Hz), FBM Perlin texture
- **No artist statement written**: The practice is undefined in words
- **Diffuser unknown**: How the light looks depends heavily on diffuser material — untested
- **Diamond form factor**: 604 LEDs in a ~21x21cm diamond, 22 rows — the canvas is defined but never painted on

## Goal

Prepare one shader ("Golden Dusk") as the definitive v1 artwork — tuned, named, and documented — and write the artist statement that frames Digital Sun as a practice.

## Plan

### 1. See the Work on Hardware

Before any creative decisions, observe "Golden Dusk" running on the actual 604-LED diamond panel.

- **Deliverable**: Observation notes — what works, what feels wrong, what surprises
- **Success Criteria**: Shader runs on physical hardware with correct 604-pixel addressing; artist has seen it in a dark room for at least 30 minutes
- **Dependencies**: `software-engineer` fixes Stage.kt; `electronics-engineer` assembles panel with level shifter
- **Priority**: Critical — all aesthetic tuning is meaningless without this

### 2. Tune "Golden Dusk" for Hardware

Adjust shader parameters based on how the light actually behaves through the diffuser on the physical panel.

- **Deliverable**: Updated `ShaderParameters` values for v1
- **Success Criteria**: The light feels like "the last 20 minutes of daylight" — warm, slow, alive, never repeating. No visible pixel grid through diffuser. No jarring brightness spikes.
- **Dependencies**: Diffuser material chosen (with `designer`); hardware assembled
- **Validation**: Before finalizing parameters, have `software-engineer` confirm ≥30fps on RPi Zero 2W. Aesthetic intent that can't run smoothly needs rethinking.
- **Priority**: Critical — this is the product

**Parameters to evaluate**:
- Hue range boundaries (currently 280-60) — may need narrowing on real LEDs vs. screen
- Noise scale — physical pixel pitch (6.25mm) vs. OPENRNDR simulation scale
- Brightness floor — LEDs at low PWM may flicker or show banding
- Temporal speed — perceived rhythm changes when you're in a room vs. watching a screen

### 3. Write the Artist Statement

Define what Digital Sun is as an art practice — not a product description, but a creative position.

- **Deliverable**: Artist statement (150-250 words)
- **Success Criteria**: Statement answers: why light as medium, what "digital nature" means, what this work is about. Reads as authored, not generated. Could hang on a gallery wall.
- **Dependencies**: Having seen the work on hardware (Task 1)
- **Validation**: Have `software-engineer` verify any technical claims (LED count, materials). Have `writer` do a voice pass.
- **Priority**: High — needed for product page, press, packaging

### 4. Name the v1 Edition

The first physical edition needs a name that is the work's title, not a product name.

- **Deliverable**: Edition title + one-line description
- **Success Criteria**: Name evokes the feeling without explaining it. Works in conversation: "I have a [name] in my living room."
- **Dependencies**: After tuning (Task 2) — the name should match what the light actually does
- **Priority**: Medium — needed before launch but not before hardware

### 5. Define the Palette Boundary

Document what Digital Sun will and won't do, aesthetically. This protects the work from feature-creep ("can you add blue?").

- **Deliverable**: Creative constraints document (1 page)
- **Success Criteria**: Clear rules: allowed hue range, forbidden behaviors (no strobing, no cool whites, no reactive modes in v1), what makes something "a Digital Sun work" vs. just an LED effect
- **Dependencies**: None — this is an artistic position
- **Priority**: Medium — important for long-term coherence

## Dependencies on Other Agents

| Agent | What I Need | When |
|-------|------------|------|
| software-engineer | Stage.kt updated to 604 LEDs; shader running on RPi | Before Task 1 |
| electronics-engineer | Physical panel assembled with level shifter | Before Task 1 |
| designer | Diffuser material selected and fitted | Before Task 2 |
| writer | Editing pass on artist statement for voice | After Task 3 |

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Shader looks bad on real LEDs (color rendering, banding) | Medium | High | Budget time for parameter iteration; may need gamma correction in firmware |
| Diffuser kills the texture (too blurry, no depth) | Medium | High | Test 3 diffuser samples before committing |
| Artist statement feels hollow without hardware experience | High | Medium | Do not write it until after 30+ minutes with the physical piece |
