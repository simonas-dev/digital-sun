# Designer Plan — Digital Sun

> Agent: Designer (Industrial Design & Visual Identity)
> Created: 2026-03-07
> Status: Draft — awaiting review

---

## Current State Assessment

- **No enclosure exists**: LED panel is raw strips on a flat substrate — no frame, no diffuser, no housing
- **Diamond form factor defined**: 21.25 x 21.25 cm face, 22 rows, wall-mount primary
- **No diffuser tested**: Individual LEDs will be visible without diffusion — unacceptable for a finished product
- **Electronics need housing**: RPi + level shifter + wiring must be enclosed behind the panel
- **Power brick is external**: 12V/20A PSU is a chunky brick with a barrel connector — needs cable management
- **No visual identity**: No wordmark, no typography, no brand color palette
- **No packaging concept**: Undefined

## Goal

Design and prototype a v1 enclosure that makes the panel look like an art object when off and a living painting when on — buildable by hand at 10-unit scale.

## Plan

### 1. Test Diffuser Materials

The diffuser is the most critical design decision — it determines how the light looks.

- **Deliverable**: 3 diffuser samples tested against the lit LED panel; photo documentation of each
- **Success Criteria**: At least 1 material blurs individual pixels into a continuous wash while preserving visible texture/movement from the shader. No hot spots.
- **Dependencies**: Physical LED panel assembled (`electronics-engineer` Task 2)
- **Validation**: `artist` must evaluate each candidate against shader output. `electronics-engineer` must confirm thermal safety. If all three agents agree on a material, lock it.
- **Priority**: Critical — the product doesn't exist without a good diffuser

**Candidates to test**:
- Frosted polycarbonate (2mm) — durable, cheap, consistent
- Frosted acrylic (3mm) — better optical quality, brittle
- Japanese washi paper (kozo fiber) — organic texture, fragile, artisanal signal
- Double-layer: thin paper over polycarbonate for texture + durability

**Test method**: Mount each material at 5mm, 10mm, and 15mm standoff from LED surface. Photograph in dark room. Evaluate: pixel blending, brightness loss, color shift, edge glow.

### 2. Design the Frame

A minimal frame that holds the panel, diffuser, and electronics as a single wall-mountable unit.

- **Deliverable**: Frame design with dimensions, material, mounting method
- **Success Criteria**: Frame is rigid, hides all electronics and wiring from front view, allows wall mounting with a single hook/cleat, power cable exits cleanly from bottom or back
- **Dependencies**: Diffuser standoff distance determined (Task 1); electronics depth known (`electronics-engineer`)
- **Validation**: Confirm with `electronics-engineer` that dimensions accommodate PCB, level shifter, cable routing, and heat dissipation before finalizing.
- **Priority**: High — needed for a complete prototype

**Design direction**:
- Thin profile: target <35mm total depth (panel + standoff + diffuser + back cover)
- Material: powder-coated aluminum angle or bent sheet — clean, minimal, matches "object" positioning
- Alternative for v1 prototype: 3D-printed frame in matte black PLA — faster iteration
- French cleat for wall mounting — invisible, strong, forgiving of wall surface

### 3. Design Cable Management

The power cable is the ugliest part of any lamp. Minimize its visual impact.

- **Deliverable**: Cable routing solution (where cable exits, how it reaches the wall outlet)
- **Success Criteria**: In the "hero view" (front, wall-mounted, eye level), no cable is visible. Cable exits downward or from the back.
- **Dependencies**: Frame design (Task 2)
- **Priority**: Medium — refinement, but matters for product photography and perception

### 4. Define Visual Identity (Minimal)

Enough brand identity for a product page and packaging — not a full brand system.

- **Deliverable**: Wordmark treatment, 2-3 brand colors, type choice
- **Success Criteria**: Identity feels like it belongs to an art/design object, not a tech product. Works at small scale (packaging label) and large (website header).
- **Dependencies**: Positioning confirmed by `founder`; tone from `writer`
- **Priority**: Medium — needed before launch but not before prototype

**Direction**: Typographic, not logomark-heavy. Think gallery label aesthetic. Likely: sans-serif, lowercase, generous spacing.

### 5. Packaging Concept (Sketch Only)

What the buyer experiences when the box arrives.

- **Deliverable**: Packaging concept description — materials, structure, insert text placement
- **Success Criteria**: Concept protects the panel during shipping; creates an unboxing moment that signals "art object" not "electronics"; includes space for artist statement insert
- **Dependencies**: Final product dimensions (Tasks 1-2); artist statement from `writer`
- **Priority**: Low for now — needed before first sale, not before prototype

## Dependencies on Other Agents

| Agent | What I Need | When |
|-------|------------|------|
| electronics-engineer | Assembled LED panel for diffuser testing | Before Task 1 |
| electronics-engineer | Electronics depth (RPi + shifter + wiring clearance) | Before Task 2 |
| founder | Price point and positioning confirmation | Before Task 4 |
| artist | Aesthetic direction (mood, warmth, gallery vs. home feel) | Before Task 2 |
| writer | Artist statement text for packaging insert | Before Task 5 |

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| No diffuser achieves good pixel blending at 6.25mm pitch | Medium | Critical | Test at wider standoff (15-20mm); accept slightly deeper profile |
| 3D-printed frame looks cheap | Medium | Medium | Use matte black PLA with sanding; or switch to aluminum for v1 if budget allows |
| Internal heat damages diffuser material (especially paper) | Low | High | Test thermal behavior at full brightness for 1 hour; add ventilation holes in back panel if needed |
