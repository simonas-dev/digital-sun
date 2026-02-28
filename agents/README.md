# Digital Sun — Agent Team

> Mission: Create art in the medium of a night lamp.

Digital Sun is a sunset-simulating LED panel. A WS2812B diamond panel on a Raspberry Pi Zero 2W running generative Perlin noise shaders — warm colors bleeding through the dark like the last minutes of daylight.

---

## The Team

| Agent | Role | When to invoke |
|---|---|---|
| [founder](founder.md) | Strategy & Vision | Product decisions, roadmap, fundraising |
| [artist](artist.md) | Creative Direction | Aesthetic choices, new shaders, art language |
| [electronics-engineer](electronics-engineer.md) | Hardware | PCB, power, BOM, manufacturing |
| [software-engineer](software-engineer.md) | Firmware & Shaders | Code, algorithms, deployment |
| [designer](designer.md) | Industrial & Visual Design | Form factor, packaging, identity |
| [marketing](marketing.md) | Go-to-Market | Positioning, channels, copy direction |
| [writer](writer.md) | Storytelling & Copy | Product descriptions, artist statements |
| [manufacturing](manufacturing.md) | Production | DFM, supply chain, unit cost |
| [pricing](pricing.md) | Business Model | Pricing, unit economics, revenue |

---

## How to Run an Agent

Each agent is a Claude skill. Invoke any agent with a clear context block:

```
@agent <agent-name>

Context:
- Current state: [what exists today]
- Decision needed: [the specific question]
- Constraints: [time / budget / technical limits]

Deliver: [specific output format]
```

### Example

```
@agent artist

Context:
- Current state: WarmColorShader — yellow→red→magenta via FBM Perlin, 604 LEDs diamond panel
- Decision needed: Design a "golden hour" shader variant — what hue range, timing, and noise parameters?
- Constraints: Must run on RPi Zero 2W at ≥30fps

Deliver: ShaderParameters values + description of the visual effect in plain English
```

---

## How the Team Works Together

The team is **async and task-driven**, not hierarchical. Any agent can be invoked independently. For cross-cutting decisions, run a **council** — invoke multiple agents on the same question and synthesize.

### Common Workflows

**New shader design**
`artist` → defines visual intent → `software-engineer` → implements & tunes → `artist` → approves

**Manufacturing readiness**
`electronics-engineer` → finalizes BOM + schematic → `manufacturing` → DFM review → `pricing` → unit cost model

**Launch campaign**
`founder` → sets positioning → `marketing` → builds channel strategy → `writer` → produces copy → `designer` → art direction

**Product evolution**
`founder` calls council: `artist` + `designer` + `marketing` on the same brief → synthesize three perspectives into one decision

---

## Stack Context (give this to every agent)

- **Hardware**: Diamond LED panel, 604 pixels of WS2812B, 12V/20A, ~21×21cm
- **Controller**: Raspberry Pi Zero 2W, GPIO 10 (SPI MOSI), 3.3V → level shifter → data line
- **Firmware**: Kotlin/JVM, `target-rpi` module, dual-coroutine (shader + render)
- **Shaders**: Perlin/FBM noise, `WarmColorShaderAlgorithm` (yellow→red→magenta), `V1RedShaderAlgorithm`
- **Dev preview**: macOS + OPENRNDR, `sun-openrndr` module
- **Deploy**: `./deploy-rpi.sh`, systemd autostart via `setup-autostart.sh`
- **Prototype status**: Functional hardware, firmware running, not yet production-ready
