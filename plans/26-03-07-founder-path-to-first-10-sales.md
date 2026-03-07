# Founder Plan — Digital Sun

> Agent: Founder (Product Strategy & Vision)
> Created: 2026-03-07
> Status: Draft — awaiting review

---

## Current State Assessment

- **Working prototype**: Firmware runs on RPi Zero 2W, 2 shaders functional on macOS preview
- **Hardware spec complete**: 604-LED diamond panel fully documented (layout, wiring, address map)
- **Hardware not assembled**: Stage.kt still addresses 292 pixels; no physical panel built yet
- **No enclosure**: Form factor undefined — raw LED strips only
- **No pricing model validated**: Draft estimates ($250–350 range) but no COGS confirmed at volume
- **No customer validation**: Zero sales, zero waitlist, zero public presence
- **Team**: Solo founder + AI agent team

## Goal

Define the v1 product scope and path to first 10 sales within 6 months, validating that Digital Sun has a market as an art object before scaling.

## Plan

### 1. Define v1 Product Scope

Lock down what ships as v1 — no feature creep.

- **Deliverable**: v1 product definition document (1 page)
- **Success Criteria**: Document answers: what's included, what's excluded, what the buyer receives
- **Constraints**: Must be buildable by 1 person in a home workshop at <10 unit scale
- **Dependencies**: Enclosure concept from `designer`, COGS estimate from `manufacturing`
- **Priority**: Critical — blocks all downstream work

**Proposed v1 scope**:
- 604-LED diamond panel, single shader ("Golden Dusk"), wall-mount
- External 12V PSU (hidden behind panel)
- Frosted diffuser, minimal frame
- No app, no WiFi, no user controls — plug in and it runs
- Numbered edition (Edition 1, qty TBD after pricing)

### 2. Validate Pricing and Edition Strategy

Decide retail price and edition size based on confirmed COGS.

- **Deliverable**: Pricing decision with rationale
- **Success Criteria**: Unit economics show >40% gross margin; price feels credible for the category
- **Dependencies**: Final COGS from `manufacturing` + `pricing`; positioning review from `marketing`
- **Priority**: High — blocks launch planning

### 3. Choose Go-to-Market Channel

Pick one primary channel for first 10 sales. Don't spread thin.

- **Deliverable**: Channel decision (DTC site / Kickstarter / gallery consignment / local market)
- **Success Criteria**: Channel chosen with <$500 setup cost and <4 weeks to first listing
- **Dependencies**: Product photography/video (needs physical unit), copy from `writer`
- **Priority**: High — but sequential after pricing

### 4. Build a Pre-Launch Audience

Start showing the work before the product is ready.

- **Deliverable**: One active channel (Instagram or personal site) with build process content
- **Success Criteria**: 10+ posts documenting the build; at least 50 engaged followers before launch
- **Dependencies**: Physical prototype exists (even rough); content direction from `marketing`
- **Priority**: Medium — can start in parallel with hardware assembly

### 5. First Sale Milestone

Sell the first unit to a real buyer (not a friend).

- **Deliverable**: Revenue from 1 shipped unit
- **Success Criteria**: Buyer receives product, provides feedback, unit survives shipping
- **Dependencies**: All of the above; QA process from `manufacturing`
- **Priority**: The north star

## Dependencies on Other Agents

| Agent | What I Need | When |
|-------|------------|------|
| designer | Enclosure concept (materials, form, rough cost) | Before Task 1 |
| manufacturing | COGS at 10-unit scale | Before Task 2 |
| pricing | Price recommendation with margin analysis | Before Task 2 |
| marketing | Channel recommendation and content plan | Before Task 3 |
| writer | Product description and artist statement | Before Task 3 |
| software-engineer | Stage.kt fixed to 604 LEDs | Before physical build |
| electronics-engineer | Level shifter solution confirmed | Before physical build |
| artist | v1 shader approved as "exhibition ready" | Before Task 3 |

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| COGS too high for viable pricing | Medium | High | Simplify enclosure; accept lower margin on Edition 1 as market validation |
| No demand at chosen price | Medium | High | Start with 5-unit edition; gauge interest before committing to 50 |
| RPi Zero 2W supply issues | High | Medium | Buy 10 units now while available; evaluate ESP32 for Edition 2 |
| Solo assembly doesn't scale | Low (at 10 units) | Low | Acceptable at validation scale |
