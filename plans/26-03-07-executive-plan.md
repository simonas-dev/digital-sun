# Executive Plan — Digital Sun

> Agent: Founder (Product Strategy & Vision)
> Created: 2026-03-07
> Status: Draft — awaiting review
> Scope: Evaluation of all 9 specialist plans; critical path to first sale

---

## Where We Are

We have a fully specified hardware design and working firmware — on screen. Nothing physical exists yet. No panel, no enclosure, no customer, no public presence. The entire project sits at the threshold between "documented idea" and "real object in someone's hands."

The good news: the hardest intellectual work is done. The LED layout, wiring, address map, shader algorithms, deploy pipeline, and build system all exist and work. What remains is execution — soldering, testing, choosing materials, writing words, and selling.

---

## The Critical Path

Every plan was evaluated for one question: **does this task sit on the path between today and the first unit sold to a real buyer?**

### Phase 1 — Make It Real (parallel, start immediately)

These three workstreams have zero dependencies and must begin now:

| Task | Agent | Why Critical |
|------|-------|-------------|
| Update Stage.kt to 604 LEDs | Software Engineer | Firmware cannot address the hardware without this. Every downstream task — test pattern, shader tuning, QA — is blocked. |
| Add hardware test pattern | Software Engineer | Without a sweep test, we cannot verify the panel is wired correctly. Debugging a 604-LED chain with a full shader running is reckless. |
| Solve level shifter + order parts | Electronics Engineer | The 3.3V→5V signal problem is the single known electrical risk. Must be solved on breadboard before committing to panel assembly. Parts have lead time — order today. |
| Start build log | Marketing | The only marketing task that costs nothing and needs no physical product. Post shader screenshots, hardware spec diagrams, strip photos. Build a content library while the hardware catches up. |

**Phase 1 exit criteria**: Stage.kt produces 604 pixels with correct coordinates. Test pattern shader exists. Level shifter verified on breadboard with ≥100 LEDs. Parts for panel assembly are in hand.

### Phase 2 — First Light (sequential, ~2-4 weeks after Phase 1)

| Task | Agent | Why Critical |
|------|-------|-------------|
| Assemble the 604-LED panel | Electronics Engineer | The entire project converges here. Without a lit panel, the artist can't tune, the designer can't test diffusers, manufacturing can't time assembly, and marketing can't shoot video. |
| Validate signal + power integrity | Electronics Engineer | Confirms the 4-point injection design works at scale. Catches problems that only appear under load. |
| See the work on hardware | Artist | The artist must observe "Golden Dusk" on real LEDs in a dark room for 30+ minutes before any aesthetic decisions are made. Screen previews are not sufficient. |

**Phase 2 exit criteria**: All 604 LEDs light up in sequence with test pattern. No dead pixels, no flickering. "Golden Dusk" shader runs on physical panel. Artist has observation notes.

### Phase 3 — Make It a Product (~2-4 weeks after Phase 2)

| Task | Agent | Why Critical |
|------|-------|-------------|
| Test diffuser materials | Designer | The diffuser determines whether this looks like art or a circuit board. Test 3 candidates at 3 standoff distances. This is the most consequential design decision for v1. **Council**: `designer` + `artist` + `electronics-engineer` evaluate together — aesthetic × feasibility × thermal. |
| Tune "Golden Dusk" for hardware | Artist | Adjust hue range, noise scale, brightness floor, and temporal speed based on how light behaves through the chosen diffuser on real LEDs. The shader that looks right on screen will not look right on hardware. |
| Design the frame | Designer | Minimal enclosure: holds panel + diffuser + electronics, mounts on wall, hides cables from front view. For v1 prototype, 3D-printed matte black PLA is acceptable. |
| Add SHADER env var | Software Engineer | Eliminates redeploy cycle. Needed for artist tuning sessions and for QA test pattern. |

**Phase 3 exit criteria**: Complete prototype unit exists — panel + diffuser + frame, wall-mountable, running tuned "Golden Dusk." The artist approves it as exhibition-ready.

### Phase 4 — Price It and Prepare to Sell (~2-3 weeks after Phase 3)

| Task | Agent | Why Critical |
|------|-------|-------------|
| Document electrical BOM | Electronics Engineer | Real part numbers, real prices, real suppliers. Manufacturing and pricing are blocked without this. |
| Build Unit Zero (timed) | Manufacturing | Assemble one complete unit end-to-end while tracking time per step, tools needed, and pain points. This is the source of truth for labor cost. |
| Validate and lock BOM | Manufacturing | Combine electronics BOM + enclosure materials + consumables. No "TBD" entries. |
| Build unit economics model | Pricing | Calculate true per-unit cost including parts, labor, packaging, processing, shipping, and returns. |
| Recommend a price | Pricing | One number, not a range. Must achieve >40% gross margin and feel credible for the "art object" category. **Council**: `pricing` + `manufacturing` + `founder` cross-validate COGS independently before debating price — 2 rounds minimum. |
| Write artist statement | Writer | The foundational text — everything else (product page, press pitch, packaging) derives from this. Must be written after the artist has seen the physical piece. |

**Phase 4 exit criteria**: Price is set. COGS is known to ±10%. Artist statement is written. Assembly process is documented.

### Phase 5 — Sell It (~2-3 weeks after Phase 4)

| Task | Agent | Why Critical |
|------|-------|-------------|
| Capture product video | Marketing | 30-60 second hero video in a dark room. This is the primary sales asset. No music, no narration — just the light. |
| Write product page copy | Writer | Headline + 80 words. A stranger reads it and understands what the object is, what it does, and why it costs what it costs. |
| Build product page | Marketing | One page: video, description, price, buy button. Shopify single-product or Big Cartel. Mobile-first. |
| Define QA process | Manufacturing | 10-point checklist, <15 minutes per unit. Every unit passes before shipping. |
| First sale | Founder | Sell one unit to a real buyer. Ship it. Get feedback. Validate the entire chain. **Council before launch**: `founder` + `marketing` + `artist` — is the product ready for a stranger to buy? |

**Phase 5 exit criteria**: First unit sold, shipped, received, and buyer provides feedback.

---

## Critical Tasks (31 total across all plans — 17 are critical)

These are tasks that, if delayed or failed, directly delay the first sale:

| # | Task | Agent | Blocks |
|---|------|-------|--------|
| 1 | Stage.kt → 604 LEDs | Software Eng | Everything on hardware |
| 2 | Hardware test pattern | Software Eng | Panel validation |
| 3 | Level shifter solution | Electronics Eng | Panel assembly |
| 4 | Order parts | Electronics Eng | Panel assembly (lead time) |
| 5 | Assemble 604-LED panel | Electronics Eng | All physical work |
| 6 | Signal + power validation | Electronics Eng | Confidence in design |
| 7 | See work on hardware | Artist | All aesthetic decisions |
| 8 | Test diffuser materials | Designer | Product appearance |
| 9 | Tune shader for hardware | Artist | v1 artwork approval |
| 10 | Design frame | Designer | Complete prototype |
| 11 | Build Unit Zero | Manufacturing | Cost model, assembly docs |
| 12 | Lock BOM | Manufacturing | Pricing |
| 13 | Unit economics model | Pricing | Price decision |
| 14 | Price recommendation | Pricing | Product page, launch |
| 15 | Artist statement | Writer | Product page, press, packaging |
| 16 | Product video | Marketing | Product page, sales |
| 17 | Product page + first sale | Marketing/Founder | Revenue |

---

## Nice-to-Haves (defer without guilt)

These are good ideas that every agent correctly identified — but none of them sit on the critical path to first sale. Do them later or skip them entirely for v1.

| Task | Agent | Why It Can Wait |
|------|-------|-----------------|
| Unify noise implementation | Software Eng | "Approximate" preview is fine for v1. The artist will tune on hardware anyway. Document the difference and move on. |
| Derive LED count from Stage | Software Eng | Good hygiene but the number is 604 and it's not changing soon. A magic number is fine for now. |
| Evaluate ESP32-S3 for production | Electronics Eng | RPi Zero 2W works. Buy 2-3 spares. This is an Edition 2 problem. |
| Cable management design | Designer | Matters for product photography. Route cable out the back or bottom. Don't over-engineer this for v1 — a clean cable exit is sufficient. |
| Visual identity / wordmark | Designer | A typed name in a clean font is enough for launch. Brand system can come with Edition 2. |
| Packaging concept | Designer | For the first 5-10 units, a clean box with foam and a printed insert is fine. Custom packaging is a scale problem. |
| Seed to tastemakers | Marketing | Only matters after the product page is live and the video is strong. Don't reach out with nothing to show. |
| DTC vs. Kickstarter decision | Marketing | Sell the first 3-5 units DTC. Use that signal to decide whether Kickstarter makes sense for a larger batch. Don't decide the channel before you have a product and a price. |
| Edition strategy modeling | Pricing | Start with a small numbered edition (10-25 units). The strategy will reveal itself from sell-through data, not from spreadsheet scenarios. |
| Break-even analysis | Pricing | Useful for the founder's peace of mind, but the decision to make v1 is already made. Calculate this after COGS is known — don't let it delay anything. |
| Press pitch | Writer | No press outreach until the video exists and at least 3 units have shipped successfully. Press before product is a waste of a first impression. |
| Packaging insert text | Writer | Write it after the first sale. You'll know the right tone better after someone actually buys one. |
| Competitive pricing analysis | Pricing | Useful context but we already know the range ($250-450). Real pricing comes from COGS + margin math, not from studying Nanoleaf's MSRP. |
| Assembly guide documentation | Manufacturing | Document it during Unit Zero build with photos. Don't create a separate task — capture it during the work. |
| Edit all agent outputs for voice | Writer | Ongoing polish. Not a blocker for anything. |
| Define palette boundary | Artist | The artist already knows this intuitively. Write it down when there's time, but it won't change the v1 product. |
| Name the v1 edition | Artist | Important but not urgent. Can be decided the week before the product page goes live. |

---

## Founder's Decisions Required

Before delegating, I need to make three calls:

1. **v1 scope is locked**: 604-LED diamond, "Golden Dusk" shader, wall-mount, external PSU, frosted diffuser, minimal frame, no app, no controls. Plug in and it runs. This is not negotiable — no scope additions.

2. **Edition 1 size: start with 10 units**. Build 1 prototype, then commit to a batch of 10. Small enough to build by hand, large enough to validate demand. Adjust for Edition 2.

3. **Launch channel: DTC first**. Simple product page, buy button. Sell 3-5 units before even considering Kickstarter. We need to prove someone will pay before we ask a crowd to fund us.

---

## Top Risks (project-level)

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Diffuser can't blur 6.25mm pixel pitch into a continuous wash | Medium | Critical | Test 3+ materials at multiple standoff distances. If nothing works at <20mm, accept a deeper profile. This is the single biggest product risk. |
| Shader looks bad on real LEDs vs. screen | Medium | High | Budget 2-3 tuning sessions with the artist. May need gamma correction, hue range adjustment, or FBM parameter changes. |
| Nobody wants to pay $299 for an unknown brand's LED panel | Medium | High | First 3 sales validate this. If they don't sell, lower the price or reposition before committing to a batch. |
| RPi Zero 2W becomes unavailable | Medium | Medium | Buy 5 units now. Evaluate ESP32 for Edition 2 but do not let this block v1. |
| Solo assembly takes >6 hours per unit | High | Medium | Acceptable for 10 units. Design a solder jig for strip alignment. This is a v1 reality, not a v1 problem. |

---

## Summary

**45 tasks across 9 plans. 17 are critical. 15+ are nice-to-haves that can wait.**

The critical path is linear and clear:

```
Fix firmware (Stage.kt + test pattern)
         +                              → Assemble panel → Artist sees it
Solve level shifter + order parts                              ↓
         +                                              Test diffusers
Start build log (marketing)                                    ↓
                                                    Tune shader + design frame
                                                               ↓
                                                    Build Unit Zero → Lock BOM
                                                               ↓
                                                    Price it → Write about it
                                                               ↓
                                                    Shoot video → Product page
                                                               ↓
                                                          First sale
```

The bottleneck is the physical panel. Everything before it can start today. Everything after it is blocked until it exists. Get the panel lit.
