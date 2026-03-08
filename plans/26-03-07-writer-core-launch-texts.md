# Writer Plan — Digital Sun

> Agent: Writer (Storytelling & Copy)
> Created: 2026-03-07
> Status: Draft — awaiting review

---

## Current State Assessment

- **No public copy exists**: No artist statement, no product description, no press materials
- **Voice defined but unused**: "Precise, warm, never hype" — reference tone established in agent spec
- **Reference lines exist**: Three example sentences in agent doc ("The light changes because the math changes...")
- **Source material available**: Two shader descriptions ("Golden Dusk", "Ember"), design vocabulary (warmth, rhythm, texture, silence), technical specs
- **No physical experience**: Writer has not seen the lamp in person — copy risks being abstract

## Goal

Produce the four core texts needed for launch: artist statement, product page, packaging insert, and press pitch — all in final, ready-to-use form.

## Plan

### 1. Write the Artist Statement

The foundational text. Everything else derives from this.

- **Deliverable**: Artist statement, 150-250 words
- **Success Criteria**: Answers why light as medium, what "digital nature" means, what this work is about. Stands alone without product context. Reads as authored by a person, not a brand.
- **Dependencies**: `artist` provides creative intent and has seen the work on hardware; writer ideally sees the physical piece too
- **Priority**: Critical — feeds into product page, packaging, and press

**Structural approach**:
- Open with the medium (light, code, time)
- State the practice (generative art as lamp, not lamp as feature)
- Ground it in the specific (Perlin noise, WS2812B, 604 pixels — but in human language)
- Close with what the viewer/buyer experiences
- When self-critiquing drafts, be specific ("the second sentence buries the verb") — generic feedback ("make it better") degrades quality
- Have `software-engineer` verify any technical claims before finalizing

### 2. Write the Product Page Copy

Headline + 80 words that sell without selling.

- **Deliverable**: Headline (under 10 words) + product description (80 words) + technical specs section (human-readable)
- **Success Criteria**: A stranger reads it and understands what the object is, what it does, and why it costs what it costs. No jargon. No feature list. Ends with a quiet call to action.
- **Dependencies**: Artist statement (Task 1); price confirmed (`pricing`); product photography exists (`marketing`)
- **Priority**: Critical — the sales page needs this

**Constraints**:
- No "revolutionary", "game-changing", or superlatives
- Don't explain Perlin noise — describe what it looks like
- Technical specs should feel like they're confirming something, not impressing

### 3. Write the Packaging Insert

The first text the buyer reads after opening the box.

- **Deliverable**: Insert text, 50-100 words
- **Success Criteria**: Sets the tone for the buyer's relationship with the object. Feels personal. Acknowledges the buyer's taste. Includes setup instruction in one sentence ("Plug in. Mount. Watch.").
- **Dependencies**: Artist statement (Task 1); packaging format from `designer`
- **Priority**: High — needed before first shipment

**Tone**: Like a note from the maker, not an instruction manual.

### 4. Write the Press Pitch

Three sentences an editor reads at 8am on a phone.

- **Deliverable**: Press pitch paragraph (3 sentences, <50 words total)
- **Success Criteria**: An editor at Dezeen or Wallpaper* reads it and clicks "reply" to ask for images. Contains: what it is, why it's interesting, one specific detail that hooks.
- **Dependencies**: Artist statement (Task 1); positioning from `marketing`
- **Priority**: Medium — needed before press outreach

**Format**: Subject line + 3 sentences. That's it.

### 5. Edit All Agent Outputs for Voice

Review and tighten any public-facing text produced by other agents.

- **Deliverable**: Edited versions of marketing copy, social captions, product descriptions
- **Success Criteria**: All public text sounds like it comes from the same voice — precise, warm, never hype
- **Dependencies**: Other agents produce drafts
- **Priority**: Ongoing — not a one-time task

## Dependencies on Other Agents

| Agent | What I Need | When |
|-------|------------|------|
| artist | Creative intent, shader descriptions, artist's own words about the work | Before Task 1 |
| artist | Confirmation that statement matches their vision | After Task 1 |
| pricing | Retail price (for product page context) | Before Task 2 |
| designer | Packaging format and dimensions (for insert sizing) | Before Task 3 |
| marketing | Target audience description and positioning statement | Before Task 2 |
| marketing | Draft social captions and outreach messages for voice editing | Before Task 5 |

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Writing about a light you've never seen produces hollow copy | High | High | Wait for physical prototype; at minimum, watch extended video of the shader running on hardware |
| Artist statement and product description overlap too much | Medium | Medium | Write them for different contexts: statement for gallery wall, description for someone deciding to buy |
| Voice guidelines are too vague ("warm" means different things) | Low | Medium | Anchor to the 3 reference lines in the agent spec; when in doubt, shorter and more specific |
