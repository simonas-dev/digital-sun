# Pricing Plan — Digital Sun

> Agent: Pricing Strategist (Business Model & Unit Economics)
> Created: 2026-03-07
> Updated: 2026-03-07 — real component costs; corrected converter/cap/wire quantities
> Status: Draft — awaiting review

---

## Current State Assessment

- **COGS now grounded in real parts**: Key components priced from actual suppliers
- **LED strips are the dominant cost**: $115/3m for ultra-narrow 5mm WS2812B 160LED/m DC12V — roughly 57% of total COGS
- **Previous estimate was wrong**: Old range of $60–100 for electronics was based on generic strip pricing. Real COGS is 2–3x higher.
- **Price range must shift upward**: $250–350 is not viable. The floor is higher.

---

## 1. Bill of Materials (Per Unit)

All prices include Lithuanian VAT (21%) unless noted. EUR used throughout.

### Electronics

| Component | Spec | Qty | Unit Price | Line Total | Notes |
|-----------|------|-----|------------|------------|-------|
| LED strips | WS2812B 5mm 160LED/m DC12V, 3m reel | see below | $115 (~€106)/3m | **€138** | Need 3.8m; at 10 units = 13 reels for 10 units |
| Raspberry Pi Zero 2W | ARM Cortex-A53, WiFi/BT | 1 | €18 | €18 | Supply-constrained; buy spares |
| PSU | MEAN WELL GST60A12-P1J, 12V/5A 60W | 1 | €21.40 | €21.40 | Desktop adapter, 60W. See power note below |
| Step-down converter | DFR0571, 12V→5V 3A | 1 | €6.29 | €6.29 | Powers RPi from 12V rail |
| Bulk capacitors | EEUFR1E102B, 1000µF 25V low-ESR | 4 | €1.29 | €5.16 | 1 per power injection point |
| Level shifter | SN74HCT245N or 74AHCT1G125 | 1 | €2.00 | €2.00 | 3.3V→5V data line |
| MicroSD card | 16GB Class 10 | 1 | €8.00 | €8.00 | |
| Wire | 18 AWG (injection), 22 AWG (data) | ~2m total | — | €5.00 | Short runs on 21cm panel |
| Connectors + terminals | Barrel jack, JST, solder tabs | set | — | €5.00 | |
| **Electronics subtotal** | | | | **€208.85** | |

### Enclosure & Optics

| Component | Spec | Qty | Unit Price | Line Total | Notes |
|-----------|------|-----|------------|------------|-------|
| Plexiglass diffuser | Frosted/opal cast acrylic, 3mm, ~25×25cm | 1 | €10.00 | €10.00 | Cut from larger sheet |
| Plexiglass backplate | Clear or opaque, 3mm, ~25×25cm | 1 | €8.00 | €8.00 | Structural backing |
| 3D printed frame | PLA/PETG, matte black, ~250g | 1 | €8.00 | €8.00 | Material + electricity |
| Mounting hardware | French cleat, screws, standoffs | set | — | €5.00 | |
| **Enclosure subtotal** | | | | **€31.00** | |

### Consumables (Per Unit Share)

| Item | Line Total |
|------|------------|
| Solder, flux, heat shrink, adhesive tape | €5.00 |
| **Consumables subtotal** | **€5.00** |

### BOM Total

| | Per Unit |
|---|---------|
| Electronics | €208.85 |
| Enclosure | €31.00 |
| Consumables | €5.00 |
| **COGS (parts only)** | **€244.85** |

> **Validation needed**: These costs must be independently confirmed by `manufacturing` before locking. If both agents arrive at the same numbers, lock the BOM. If they disagree, investigate — don't average.

---

## Power Supply Note

The MEAN WELL GST60A12-P1J is rated **60W (12V/5A)**. The hardware spec shows **181W max** at full white on all 604 LEDs.

This is acceptable because:
- "Golden Dusk" shader uses warm colors at moderate brightness — typical draw is **30–50W**
- Full-white all-on is a test condition, not an operating condition
- The PSU has overcurrent protection — it won't fail, it will shut down

**Risk**: If a future shader pushes brightness higher, the PSU trips. **Mitigation**: Cap max brightness in firmware to 50% (~90W headroom). Document that this PSU is rated for the v1 shader profile. If a brighter shader is needed later, upgrade to a 12V/10A (120W) unit (~€35).

---

## 2. Unit Economics Model

### At 1 Unit (Prototype)

| Line Item | Cost |
|-----------|------|
| Parts COGS | €244.85 |
| LED strip waste (buy 2×3m, use 3.8m, waste 2.2m) | +€74 waste |
| **Effective prototype COGS** | **€319** |

The prototype is expensive because of strip waste. This cost drops at batch scale.

### At 10 Units

| Line Item | Per Unit | Notes |
|-----------|----------|-------|
| LED strips | €138 | 13 reels ÷ 10 units (1m waste total) |
| RPi Zero 2W | €16 | Slight volume discount |
| PSU MEAN WELL | €20 | |
| Step-down DFR0571 | €5.50 | |
| Capacitors (4 per unit) | €4.40 | 40 units @ €1.10 |
| Level shifter | €1.50 | |
| MicroSD | €7 | |
| Wire | €4 | Bulk |
| Connectors | €4 | |
| Enclosure (plexiglass + frame + mount) | €28 | Cut from sheets; print in batch |
| Consumables | €4 | |
| **Parts COGS** | **€232** | |
| Assembly labor (4 hrs × €15/hr) | €60 | Founder's time; see note |
| Packaging (box, foam, printed insert) | €12 | |
| Payment processing (3.5%) | ~€16 | At €449 retail |
| Shipping (EU, parcel) | €15 | Estimated; varies by country |
| **Variable cost (excl. labor)** | **€275** | |
| **Variable cost (incl. labor)** | **€335** | |

**Labor note**: At 10-unit scale the founder is the assembler. Labor is real cost in time, but it doesn't leave the bank account. Both models are shown — "excl. labor" for cash flow, "incl. labor" for true economics.

### At 50 Units

| Line Item | Per Unit | Notes |
|-----------|----------|-------|
| LED strips | €130 | 64 reels ÷ 50 units; possible 5–10% bulk discount |
| Other electronics | €62 | Volume pricing on all components |
| Enclosure | €25 | |
| Consumables | €3 | |
| **Parts COGS** | **€220** | |
| Assembly labor (3 hrs × €15/hr) | €45 | Faster with practice + jig |
| Packaging | €10 | Bulk box order |
| Processing (3.5%) | ~€16 | |
| Shipping (EU) | €15 | |
| **Variable cost (excl. labor)** | **€261** | |
| **Variable cost (incl. labor)** | **€306** | |

---

## 3. The Strip Problem

LED strips are **57% of total COGS**. This is the single biggest lever.

$115/3m = **€35.33/m**. At 3.8m per unit, that's **€134–138 in strips alone**.

This price is high because the strips are **ultra-narrow (5mm), 160 LED/m, individually addressable, DC12V** — a specialty product. Standard 10mm-wide WS2812B strips at 60 LED/m cost €5–10/m.

**Cost reduction options (for Edition 2+)**:
- Negotiate bulk pricing with the strip supplier (10+ reels → ask for 10–15% discount)
- Source directly from Shenzhen manufacturers (MOQ typically 50–100m, price could drop 30–40%)
- Test whether 8mm-wide strips work in the diamond layout (2x cheaper, but changes aesthetics)
- Evaluate 5V strips (cheaper, but requires redesign of power injection)

**For Edition 1**: Accept the strip cost. It's a premium component that defines the product quality. Don't compromise the form factor to save €40.

---

## 4. Price Recommendation

### The Math

| Scenario | Retail Price | Variable Cost (excl. labor) | Gross Margin | Margin % |
|----------|-------------|---------------------------|-------------|----------|
| A | €349 | €280 | €69 | 20% |
| B | €399 | €282 | €117 | 29% |
| C | **€449** | **€284** | **€165** | **37%** |
| D | €499 | €285 | €214 | 43% |
| E | €549 | €287 | €262 | 48% |

Variable cost = COGS (€232) + packaging (€12) + processing (3.5% of price) + shipping (€15). Returns reserve excluded from table for clarity (add 5% buffer mentally).

Including labor (€60/unit):

| Scenario | Retail Price | Full Variable Cost | True Margin | Margin % |
|----------|-------------|-------------------|-------------|----------|
| C | **€449** | **€344** | **€105** | **23%** |
| D | €499 | €345 | €154 | 31% |
| E | €549 | €347 | €202 | 37% |

### Recommendation: **€449**

> **Validation needed**: This recommendation should be debated in a council (`pricing` + `manufacturing` + `founder`) before locking. `manufacturing` verifies COGS, `founder` evaluates strategic positioning. 2 rounds minimum.

**Why €449 and not higher or lower:**

- **Below €400 is not viable.** At €349–€399 the margin is too thin to absorb any surprises (strip price increase, shipping damage, returns). One bad unit wipes out the profit from three good ones.
- **€449 hits the sweet spot.** 37% gross margin (excl. labor) is solid for Edition 1. Close to the 40% target. The founder's labor is the investment — it pays back in learning, not in margin.
- **€499–€549 is defensible but risky for an unknown brand.** At this price, buyers expect a polished product and brand recognition. Edition 1 is handmade and new — €449 is confident without being presumptuous.
- **€449 signals "art object."** It's above Nanoleaf (~€200–300), in line with limited-edition prints and small-batch design lamps, and below high-end design lighting (Flos, Muuto at €600+). It says "this is considered" — not cheap, not luxury.
- **€449 is a round number in the right psychological bracket.** Under €450 feels meaningfully different from €500+.

### Market Context

| Comparable | Price | Why relevant |
|------------|-------|-------------|
| Nanoleaf Lines 9-pack | €250 | Mass-produced LED art panel; Digital Sun is handmade, more artistic, higher quality strips |
| Limited-edition generative art print (fxhash/Artblocks) | €200–600 | Same buyer, same aesthetic sensibility; Digital Sun moves + glows |
| Teenage Engineering OD-11 | €400 | Cult design object; proves this buyer pays €400+ for objects with story |
| Hay/Muuto table lamp | €300–500 | Design-forward home object at similar price point |
| Small-batch art lamp (Etsy/IG) | €200–500 | Direct comps; handmade, limited, story-driven |

Digital Sun at €449 sits comfortably in the "designed, handmade, limited" category. It is not competing with Govee or Philips Hue.

---

## 5. Edition Strategy

**Recommendation: Numbered Edition 1, qty 10.**

| Factor | Limited (10 units) | Open Edition |
|--------|-------------------|--------------|
| Price justification | Scarcity supports €449 | Must justify on quality alone |
| Risk | If 10 don't sell, signal is clear | Slow sales are ambiguous |
| Assembly | Manageable by 1 person | Open-ended commitment |
| Brand signal | "Art edition" | "Product" |
| Cash required upfront | ~€2,320 in parts | Unbounded |
| Learning | Build 10, learn everything | Build indefinitely, learn slowly |

**Edition 1 = 10 units at €449.** Numbered. Not discounted. If they sell out, Edition 2 can be 25 units at the same or higher price. If they don't sell, the loss is contained (~€2,320 in parts + labor time).

---

## 6. Revenue Scenarios

### Edition 1 (10 units at €449)

| | Excl. Labor | Incl. Labor |
|---|------------|------------|
| Revenue | €4,490 | €4,490 |
| Variable costs | €2,750 | €3,350 |
| **Gross profit** | **€1,740** | **€1,140** |
| Margin | 39% | 25% |

### Edition 2 (25 units at €449, if Edition 1 sells out)

| | Excl. Labor | Incl. Labor |
|---|------------|------------|
| Revenue | €11,225 | €11,225 |
| Variable costs | €6,625 | €7,750 |
| **Gross profit** | **€4,600** | **€3,475** |
| Margin | 41% | 31% |

### First 50 Units Cumulative (Edition 1 + 2 + 3)

| | Excl. Labor | Incl. Labor |
|---|------------|------------|
| Revenue | €22,450 | €22,450 |
| Variable costs | €13,050 | €15,300 |
| **Gross profit** | **€9,400** | **€7,150** |
| Margin | 42% | 32% |

---

## 7. Break-Even

### Fixed Costs (Estimated)

| Item | Cost |
|------|------|
| Prototype parts + iteration | €350 |
| Photography / video equipment or hire | €150 |
| Website / shop setup (Shopify/Big Cartel) | €100 |
| Domain, hosting, email | €50 |
| Soldering station, tools (if not owned) | €200 |
| **Total fixed costs** | **~€850** |

### Break-Even Point

At €449 retail, €165 gross margin per unit (excl. labor):

**Break-even at 6 units.**

At €105 true margin per unit (incl. labor):

**Break-even at 9 units.**

Edition 1 (10 units) breaks even on both a cash and full-cost basis, with €1,140–€1,740 profit depending on how you value labor.

---

## 8. Cost Reduction Roadmap (Edition 2+)

| Lever | Potential Saving | Effort |
|-------|-----------------|--------|
| Bulk LED strip negotiation (10+ reels) | €10–15/unit | Low — email the supplier |
| Direct Shenzhen sourcing (100m MOQ) | €30–50/unit | Medium — longer lead time, QC risk |
| Move from RPi to ESP32-S3 | €12–15/unit | High — firmware rewrite |
| Injection-molded frame (at 100+ units) | €3–5/unit | High — tooling cost ~€2,000 |
| Larger PSU to support brighter shaders | +€15/unit | Low — swap component |

At 100-unit scale with bulk strips and ESP32, COGS could drop to **€160–180/unit**, pushing margin to **55%+ at €449**.

---

## Risks (Updated)

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| LED strip price increases (single source) | Medium | High | Buy 15 reels upfront for Edition 1 + spares. Identify backup supplier. |
| 60W PSU trips during certain shaders | Low | Medium | Cap brightness in firmware. Document PSU limitation. Upgrade path is simple. |
| €449 feels too high for unknown brand | Medium | Medium | Lean into "Edition 1 of 10, handmade in Lithuania, numbered." The story justifies the price. |
| COGS higher than modeled (hidden costs) | Medium | Medium | Budget €15/unit contingency. At €449 this still leaves positive margin. |
| RPi Zero 2W unavailable | Medium | High | Buy 12 units now (10 + 2 spares). ~€216 investment. |
| Strip color inconsistency between reels | Medium | Medium | Order all reels from same batch. Test before assembly. |

---

## Summary

| | Value |
|---|-------|
| **Parts COGS (10-unit scale)** | **€232/unit** |
| **Full variable cost (incl. labor, packaging, shipping)** | **€335/unit** |
| **Recommended retail price** | **€449** |
| **Gross margin (excl. labor)** | **37%** |
| **True margin (incl. labor)** | **23%** |
| **Break-even** | **6–9 units** |
| **Edition 1** | **10 units, numbered** |
| **Edition 1 revenue** | **€4,490** |
| **Edition 1 profit** | **€1,140–€1,740** |
| **#1 cost driver** | **LED strips (57% of COGS)** |
| **#1 cost reduction lever** | **Bulk strip sourcing** |

The old $60–100 COGS estimate was wrong. Real COGS is **€232** at 10-unit scale. The product is viable at **€449** with healthy margins. Edition 1 breaks even at 6–9 units and profits on all 10. LED strips dominate cost — bulk sourcing for Edition 2 is the biggest lever.
