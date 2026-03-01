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

## Three-Phase Workflow (for software tasks)

> Adapted from [How I Use Claude Code](https://boristane.com/blog/how-i-use-claude-code/) by Boris Tane.

**Never allow code generation until approving a written plan.**

### Phase 1 — Research

Ask the software-engineer to read deeply before touching anything:

```
@agent software-engineer

Read [file/module/area] deeply and in great detail — understand every intricacy.
Document your findings in research.md. Do not implement anything yet.
```

The `research.md` artifact is a reviewable surface. Read it. Verify the agent understands
the codebase before planning begins. Prevents implementations that ignore existing caching,
duplicate business logic, or violate ORM conventions.

### Phase 2 — Plan

Once research is verified, ask for a concrete plan:

```
@agent software-engineer

Based on research.md, write a detailed implementation plan in plan.md.
Include: explanatory text, code snippets, exact file paths to modify, and trade-off analysis.
Do not implement yet.
```

**Annotate the plan directly** — add inline notes into `plan.md`:
- Reject approaches: "remove this section, we don't need it"
- Add constraints: "use Gradle task, not raw shell"
- Redirect structure: "move this to sun-core, not target-rpi"

Repeat the annotation loop 1–6 times with the explicit guard: **"do not implement yet."**
Without it, implementation begins prematurely.

Once the plan is right, ask for a breakdown:

```
Add a detailed todo list at the bottom of plan.md — all phases and individual tasks.
Do not implement yet.
```

### Phase 3 — Implement

Only after the annotated plan is approved:

```
Implement it all. When you finish a task or phase, mark it as completed in plan.md.
Do not stop until all tasks and phases are completed.
Do not add unnecessary comments. Do not use unknown or unresolved types.
Continuously run typecheck to catch new issues as you go.
```

**All creative decisions happen in Phase 2. Phase 3 should be boring.**

### During Implementation

- **Terse corrections work** — full context lives in `plan.md` and session history.
  Single sentences are enough: "move that to sun-core", "don't change the function signature."
- **Protect existing interfaces** with hard constraints: "these function signatures must not change."
- **When direction proves wrong** — discard git changes and restart with reduced scope.
  Don't patch incrementally; revert and replan.
- **Run in a single long session** — research, annotation, and implementation in one conversation.
  `plan.md` persists with full fidelity across auto-compaction.

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
