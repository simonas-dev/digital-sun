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

## Multiagent Debate Rules

> Rules derived from Du et al., "Improving Factuality and Reasoning in Language Models through Multiagent Debate" (MIT CSAIL / Google Brain, 2023). [arXiv:2305.14325](https://arxiv.org/abs/2305.14325)

The paper demonstrates that multiple LLM instances debating each other significantly outperform single-agent generation, self-reflection, and majority voting on both reasoning and factual accuracy tasks. The following rules operationalize the paper's findings for our agent team.

### Rule 1: Use Multi-Agent Debate for High-Stakes Decisions, Not Routine Tasks

**Why**: Debate is more computationally expensive (multiple agents × multiple rounds). The paper shows the biggest gains on tasks where single agents confidently produce wrong answers — math reasoning (+14.8pp), factual biography accuracy (+7.8pp), and strategic planning (+31.5 pawn score in chess). Routine tasks with clear answers don't benefit enough to justify the cost.

**When to use debate**: Pricing decisions, product scope trade-offs, aesthetic judgments, go/no-go calls, any decision where being wrong is expensive.

**When NOT to use debate**: Executing a defined plan, looking up a spec, running a build command.

### Rule 2: Always Use At Least 3 Agents in a Council

**Why**: The paper found that performance monotonically increases with the number of agents (Figure 10a). Two agents can deadlock. Three agents create a natural tiebreaker and generate more diverse initial responses. The sweet spot for cost/quality is 3–5 agents.

**How**: When invoking a council, always include at least 3 specialist agents. For cross-domain decisions, pick agents from different disciplines (e.g., `artist` + `pricing` + `manufacturing`) to maximize viewpoint diversity.

### Rule 3: Run At Least 2 Rounds of Debate, Cap at 4

**Why**: The paper found performance monotonically improves up to ~4 rounds, then plateaus (Figure 10b). A single round is just "generate and compare" — the real value comes from agents critiquing and updating based on each other's reasoning. Beyond 4 rounds, models tend to just repeat their converged answer.

**How**:
1. **Round 1**: Each agent generates an independent response (no cross-talk).
2. **Round 2**: Each agent reads all other responses and produces an updated answer with explicit reasoning about where they agree/disagree.
3. **Round 3** (if no consensus): Repeat with updated responses.
4. **Stop** when agents converge on the same answer, or after 4 rounds.

### Rule 4: Make Agents "Stubborn" — Use Long-Form Debate Prompts

**Why**: The paper tested two prompt styles (Figure 3, Figure 12). "Short" prompts ("Based on other opinions, update your response") led to fast but shallow consensus. "Long" prompts ("Using the opinions of other agents as additional advice, can you give an updated response") led to slower convergence but **better final answers**. Agents that maintained their position longer produced more accurate outcomes.

**How**: When framing debate prompts, instruct agents to:
- Critically examine other agents' reasoning, not just their conclusions
- Explicitly state where they disagree and why before updating
- Only change their answer if the other agent's reasoning is more sound, not just because others disagree

**Bad prompt**: "Here are other opinions. Update your answer."
**Good prompt**: "Here are responses from other agents. Closely examine your reasoning and theirs. State where you agree and disagree, and why. Only update your answer if you find a flaw in your own reasoning."

### Rule 5: Debate Catches Errors That Reflection Misses

**Why**: A key finding was that self-reflection (asking a single agent to critique its own answer) performs **worse** than debate on factual tasks — reflection actually degraded MMLU accuracy from 63.9% to 57.7% (Table 2). Single agents lack the diversity of reasoning needed to catch their own blind spots. In debate, different agents make different errors, and cross-examination surfaces them.

**How**: Never rely on a single agent to self-check a critical decision. If the `pricing` agent produces a recommendation, have `founder` and `manufacturing` critique it in a debate round — don't just ask `pricing` to "double-check."

### Rule 6: Disagreement Signals Uncertainty — Don't Force Consensus

**Why**: The paper found that when agents gave different answers on a fact, the underlying model was genuinely uncertain about it (Figure 9). Forcing consensus on uncertain facts led to confidently stated but potentially wrong answers. Conversely, facts where all agents independently agreed were almost always correct.

**How**:
- If agents converge quickly (Round 1–2), the answer is likely reliable.
- If agents still disagree after 3+ rounds, flag the decision as uncertain and escalate to the founder for a judgment call with explicit risk acknowledgment.
- Do not average or "split the difference" between disagreeing agents. Either one is right, or more information is needed.

### Rule 7: Summarize Before Debating When Using 5+ Agents

**Why**: With many agents, directly concatenating all responses exceeds useful context length. The paper found that summarizing all agent responses before feeding them back actually **improved** performance over raw concatenation (Figure 13), while also reducing token cost.

**How**: When running a full council (5+ agents), first have one agent summarize the key positions and disagreements before starting the next debate round. This also produces a readable artifact.

### Rule 8: Mix Specialist Perspectives for Maximum Diversity

**Why**: The paper showed that debate between different model types (ChatGPT + Bard) outperformed same-model debate — solving 17/20 problems vs. 14/20 (ChatGPT alone) or 11/20 (Bard alone) (Section 3.3). Different "perspectives" generate more diverse initial answers, which is the raw material that makes debate valuable.

**How**: For councils, always pick agents from different domains. Good combinations:
- **Product scope**: `founder` + `artist` + `electronics-engineer` (vision × aesthetics × feasibility)
- **Pricing**: `pricing` + `manufacturing` + `marketing` (cost × production × market)
- **Launch readiness**: `founder` + `writer` + `designer` (strategy × voice × visual)

Avoid debating agents with identical knowledge bases — two similar agents will make the same errors and reinforce them.

### Rule 9: Debate Can Recover From All-Wrong Starts

**Why**: A striking finding was that even when **all** agents initially gave incorrect answers, the debate process often led to the correct answer (Figures 4, 5, 11). This happens because agents cross-examine each other's reasoning steps, not just final answers — and flawed intermediate steps get caught.

**How**: Don't abandon a debate just because Round 1 looks bad. Let it run for at least 2 rounds before judging. The value of debate is in the reasoning process, not the initial answers.

### Rule 10: Use Debate Output as Training Data for Future Decisions

**Why**: The paper suggests debate can "generate additional model training data, effectively creating a model self-improvement loop." Correct debate outcomes — where agents converge on verified answers — are high-quality decision artifacts.

**How**: Store debate outcomes in `plans/` as decision records. When similar questions arise later, reference prior debate outcomes as context. This is why our plans use the `yy-mm-dd-{agent}-{goal}.md` naming — they're searchable decision artifacts, not disposable notes.

---

## LLM Optimization Techniques — Paper-by-Paper Rules

> Each technique below is grounded in a specific paper's experimental findings. Rules cite exact results so you can judge their applicability. Papers are ordered by relevance to our multi-agent workflow.

---

### 1. Chain-of-Thought Prompting (CoT)

> Wei et al., "Chain-of-Thought Prompting Elicits Reasoning in Large Language Models," NeurIPS 2022. [arXiv:2201.11903](https://arxiv.org/abs/2201.11903)

**Key findings**: On GSM8K math problems, PaLM 540B jumped from 17.9% → 56.9% with CoT (8 few-shot exemplars). This surpassed a fine-tuned GPT-3 175B + verifier (55%) using zero task-specific training. On symbolic reasoning (last letter concatenation), PaLM 540B went from 6.8% → 59.1%. Error analysis of 50 wrong answers: 46% were arithmetic errors (correct reasoning, wrong calculation), 28% missing steps, 16% misunderstood problem. CoT with an external calculator would eliminate nearly half of all errors.

**Critical threshold**: CoT is an **emergent ability at ~100B parameters**. Below 100B, CoT can *degrade* performance — PaLM 8B showed no meaningful gain and sometimes got worse. The effect appears suddenly at scale, not gradually (Figure 4 of paper).

**Ablation results** (Table 5, PaLM 540B on GSM8K):
- Full CoT: 56.9%
- Equation only (no natural language): 43.2%
- Random filler tokens (same length): 18.5%
- Reasoning *after* answer: 18.0%
- No chain (standard): 17.9%

The content of the chain matters, not its length. Reasoning must come *before* the answer.

**Exemplar robustness** (Table 6): Different annotators writing CoT for the same problems gave 55–58% (robust). Different random exemplar sets: 55–58% (robust). Exemplar ordering: minimal effect. Even k=1 exemplar gave substantial gains.

**Rules**:
1. **Use CoT only for multi-step reasoning tasks.** Simple lookups, pattern matching, and near-ceiling tasks show negligible gains. CoT shines on GSM8K-style multi-step problems, not on MAWPS-style single-step ones.
2. **Write reasoning in natural language, not just equations.** Equation-only chains lose 14pp vs. full NL chains. The natural language scaffolds the reasoning.
3. **Place the chain before the answer, never after.** Reasoning-after-answer performs no better than no reasoning at all. The chain must guide generation.
4. **Pair CoT with external tools for calculation.** 46% of CoT errors are pure arithmetic. If the task involves math, have the agent use a calculator or code execution rather than mental arithmetic.
5. **8 exemplars is the saturation point for few-shot CoT.** More examples don't measurably help. Even 1 exemplar gives substantial gains.
6. **Don't worry about exact chain wording.** Different annotators and exemplar sets give similar results. The structure matters more than the prose style.

> Kojima et al., "Large Language Models are Zero-Shot Reasoners," NeurIPS 2022. [arXiv:2205.11916](https://arxiv.org/abs/2205.11916)

**Key finding**: Simply appending "Let's think step by step" to a prompt (zero-shot CoT) dramatically improves reasoning. On MultiArith with text-davinci-002: 17.7% → 78.7%. On GSM8K: 10.4% → 40.7%. This requires no exemplars at all — the prompt itself triggers step-by-step reasoning.

**Prompt wording matters** (Table 4, MultiArith, text-davinci-002): "Let's think step by step" (78.7%) outperformed all alternatives. Misleading prompts ("Don't think") and irrelevant prompts ("By the way") showed no improvement. Instructive but different wordings ("Let's work this out" etc.) gave 70–73% — close but consistently lower.

**Two-stage process**: (1) Append "Let's think step by step" to extract reasoning, then (2) append "Therefore, the answer is" to extract the final answer. Both stages matter — without stage 2, the model often continues reasoning without concluding.

**Rules**:
7. **Default to "Let's think step by step" for zero-shot tasks.** It is the single best zero-shot reasoning prompt validated across 12 benchmarks. Don't get creative with the wording — the tested phrasing works best.
8. **Always extract the answer explicitly after reasoning.** Use a two-stage prompt: first elicit reasoning, then force a conclusion with "Therefore, the answer is." Without this, agents ramble without committing to an answer.
9. **Use zero-shot CoT when you lack good exemplars.** It gets 78.7% vs. few-shot CoT's 93.0% on MultiArith (Kojima et al. Table 2) — 85% of the gain with zero exemplar engineering effort.

---

### 2. Self-Consistency (Majority Voting)

> Wang et al., "Self-Consistency Improves Chain of Thought Reasoning in Language Models," ICLR 2023. [arXiv:2203.11171](https://arxiv.org/abs/2203.11171)

**Key findings**: Sample multiple CoT reasoning paths, take the majority final answer. On GSM8K with PaLM 540B: CoT alone 56.5% → self-consistency 74.4% (+17.9pp). On MultiArith with LaMDA 137B: +24pp. On AQuA: +12.2pp. On StrategyQA: +6.4pp. New SOTA on arithmetic reasoning without any fine-tuning.

**Scaling with model size**: Gains increase with model scale. UL2-20B: +3–6pp. LaMDA-137B: +9–23pp. PaLM-540B: +11–18pp. Self-consistency amplifies the capabilities that CoT unlocks at scale.

**Sample count**: Performance improves steeply from 1 to ~10 samples, then gradually from 10 to 40, and plateaus around 40. The paper typically uses 40 samples, but 5–10 captures most of the gain.

**Majority voting beats all other aggregation methods tested**: summing log-probabilities, normalized probability weighting, and length-normalized scoring all underperformed simple majority vote. The simplest approach won.

**Key insight**: Self-consistency works because correct reasoning paths converge on the same answer while errors scatter randomly. If you sample 5 paths and 4 agree, the answer is almost certainly right. If they split 2-2-1, genuine uncertainty exists.

> Cobbe et al., "Training Verifiers to Solve Math Word Problems," 2021. [arXiv:2110.14168](https://arxiv.org/abs/2110.14168)

**Key findings**: Introduced GSM8K (8.5K grade-school math problems). Core result: a 6B verifier selecting among 100 generated solutions outperforms a fine-tuned 175B model generating a single solution — verification is equivalent to a ~30x model size increase. Token-level verifiers (scoring each reasoning step) outperform solution-level verifiers (scoring only the final answer) and are less prone to overfitting. **Goodhart effect**: beyond ~400 solutions, performance *decreases* as the verifier gets fooled by adversarial-looking solutions. Sweet spot is ~100 solutions. Combining verifier ranking with majority voting among top 3–5 candidates yields best results.

**Rules**:
10. **Sample 5 responses before trusting any single answer.** If 4/5 agree, confidence is high. If they split 3-2 or worse, the question is genuinely hard — escalate to council debate. This catches most reasoning errors at low cost.
11. **Use simple majority voting, not weighted schemes.** Wang et al. tested probability-weighted aggregation, length-normalized scoring, and other methods — all underperformed plain majority vote. Don't over-engineer the aggregation.
12. **Self-consistency is your first-line defense.** It's cheaper than multi-agent debate (same model, multiple samples vs. multiple agents) and captures most of the gain. Reserve debate for cases where self-consistency shows disagreement.
13. **Generate many, verify the best.** Cobbe et al. showed that generating 100 solutions and picking the best (via verifier) outperforms generating 1 solution from a 30x larger model. When quality matters, invest in verification of multiple candidates rather than a single careful generation.

---

### 3. Self-Reflection and Self-Refine

> Shinn et al., "Reflexion: Language Agents with Verbal Reinforcement Learning," NeurIPS 2023. [arXiv:2303.11366](https://arxiv.org/abs/2303.11366)

**Key findings**: Reflexion adds persistent verbal memory across retry episodes. On HumanEval (Python code): 91% pass@1 vs. GPT-4's 80.1% baseline (+11pp). On LeetCode Hard: 15% vs. 7.5% (2x improvement). On AlfWorld decision-making: 97% vs. 78% baseline (+19pp). On HotPotQA with CoT+ground-truth context: 68% → 80% (+12pp).

**Critical failure case**: On WebShop (open-ended e-commerce task): 0% improvement after 4 trials. Reflexion fails on tasks with highly diverse action spaces where the agent cannot generate useful self-reflections.

**Memory window**: Only the last 1–3 reflections are kept (Omega=1 for code, Omega=3 for QA). More reflections do not help and waste context window.

**Test quality is the bottleneck**: HumanEval has 0.99 true positive / 0.01 false positive rate in self-generated tests → Reflexion excels (91%). MBPP has 0.16 false positive rate → Reflexion underperforms (77.1% vs. GPT-4's 80.1%). When tests pass on incorrect code, the agent falsely believes it succeeded and generates misleading reflections.

**Speed of learning**: Most improvement happens in trials 1–3. AlfWorld jumps from ~60% to ~90% by trial 2. Diminishing returns after 3 trials.

> Madaan et al., "Self-Refine: Iterative Refinement with Self-Feedback," NeurIPS 2023. [arXiv:2303.17651](https://arxiv.org/abs/2303.17651)

**Key findings**: Generate → critique → refine loop using the same model. Tested across 7 tasks with GPT-3.5/4. Biggest wins on subjective tasks: dialogue response (+49.2pp preference with ChatGPT), code readability (+35.4pp with GPT-3.5), constrained generation (+20.7pp). **Near-zero on math**: GPT-4 improved only 92.9% → 93.1% (+0.2pp); ChatGPT showed 0% improvement. The model cannot reliably detect its own mathematical errors.

**Diminishing returns**: Performance peaks at iteration 2–3 across most tasks. Beyond that, the model over-edits — simplifying correct solutions, introducing new errors, or converging on bland outputs. The generate-critique-refine loop has a natural stopping point. Self-bias amplifies with more iterations (Huang et al., ICLR 2024).

**Feedback quality is critical** (ablation): With actionable feedback, code optimization scored 27.5. With generic feedback: 26.0. Without feedback (just regenerate): 24.8. For acronym generation, bad feedback actually *dropped* performance from 56.4 to 48.0 — worse than no refinement at all.

> Du et al. (2023) found self-reflection *degraded* factual accuracy on MMLU (63.9% → 57.7%). Single-agent self-critique cannot reliably catch its own factual errors — it tends to second-guess correct answers.

**Rules**:
14. **Use Reflexion for code and decision tasks with concrete feedback signals.** Code execution with tests is ideal — the error message tells the agent exactly what went wrong. Without concrete feedback, reflection degrades.
15. **Limit self-refine to 2–3 iterations maximum.** Both Madaan et al. and Shinn et al. show performance peaks at 2–3 rounds. More iterations cause over-editing and regression. Set a hard cap.
16. **Keep only the last 1–3 reflections in context.** Shinn et al. showed Omega=1 (most recent only) works for code. Larger memory windows waste context without improving results.
17. **Never use single-agent reflection for fact-checking.** Du et al. showed it degrades MMLU accuracy by 6pp. For factual validation, always use a *different* agent (cross-agent critique), not the same one critiquing itself.
18. **Invest in test/feedback quality, not reflection rounds.** Reflexion's 91% on HumanEval vs. 77.1% on MBPP is entirely explained by test quality (0.01 vs. 0.16 false positive rate). Better feedback signals > more reflection loops.
19. **Use self-refine for the `writer` and `artist` agents (style refinement) but not for `pricing` or `electronics-engineer` (factual accuracy).** The technique excels at polishing prose, code style, and dialogue quality. It fails at error-checking numbers and facts.

---

### 4. Retrieval-Augmented Generation (RAG)

> Guu et al., "REALM: Retrieval-Augmented Language Model Pre-Training," ICML 2020. [arXiv:2002.08909](https://arxiv.org/abs/2002.08909)

**Key findings**: REALM augments a language model with a neural retriever that fetches relevant documents from a corpus during both pre-training and inference. On Open-domain QA benchmarks (NaturalQuestions, WebQuestions, CuratedTrec), REALM outperformed all previous methods by 4–16pp absolute accuracy. The retriever is trained end-to-end with the language model via backpropagation through the retrieval step.

**Retriever quality dominates**: The quality of retrieved documents matters more than model size for factual tasks. A smaller model with good retrieval consistently outperforms a larger model without retrieval on knowledge-intensive tasks.

**Index freshness**: REALM refreshes its document index asynchronously every ~500 training steps. Even with slightly stale indices, performance is stable. This validates the pattern of not needing perfectly current context — "good enough" retrieval is sufficient.

> Lewis et al., "Retrieval-Augmented Generation for Knowledge-Intensive NLP Tasks," NeurIPS 2020. [arXiv:2005.11401](https://arxiv.org/abs/2005.11401)

**Key findings**: RAG-Token (retrieval per token) vs. RAG-Sequence (retrieval per sequence) — both work, but RAG-Sequence is simpler and often sufficient. RAG set SOTA on 3 open-domain QA benchmarks and generated more factual, specific, and diverse text than pure parametric models. Critically: RAG can update its knowledge by simply swapping the document index — no retraining needed.

**Rules**:
20. **Always provide source documents in context — never assume the agent remembers.** REALM's core insight: retrieval-augmented models dramatically outperform pure parametric models on factual tasks. In our workflow, this means: when invoking `software-engineer`, include relevant source files. When invoking `pricing`, include the BOM. When invoking `electronics-engineer`, include `hardware/README.md`.
21. **Retriever quality > model size for factual tasks.** If an agent gives wrong facts, the fix is better context documents, not a longer prompt or more reasoning steps. Check whether the right source document was provided before blaming the agent.
22. **"Good enough" context is sufficient — don't over-engineer retrieval.** REALM showed stable results even with slightly stale indexes. Include the 2–3 most relevant files, not every file that might be tangentially related. Over-stuffing context dilutes the signal.

---

### 5. Tree of Thoughts (ToT) / Graph of Thoughts (GoT)

> Yao et al., "Tree of Thoughts: Deliberate Problem Solving with Large Language Models," NeurIPS 2023. [arXiv:2305.10601](https://arxiv.org/abs/2305.10601)

**Key findings**: On Game of 24 (arithmetic puzzle), CoT gets 4% success rate; ToT (breadth=5, BFS) gets **74%** — an 18.5x improvement. On Mini Crosswords: word-level accuracy 15.6% → 60% (3.8x). On Creative Writing: GPT-4 score 6.93 → 7.56. Standard benchmarks (GSM8K, StrategyQA): only marginal gains (+4pp, +1pp).

**BFS vs. DFS**: BFS for shallow trees (depth ≤3) with evaluable states. DFS for deeper, variable-depth problems. BFS is simpler; DFS requires good pruning heuristics.

**Pruning is critical**: Without pruning, crossword word-accuracy dropped from 60% → 41.5%. Without backtracking: 60% → 20%. Pruning bad branches early is what makes ToT work.

**Generation quality > evaluation quality**: GPT-4 generating + GPT-3.5 evaluating: 64% on Game of 24. GPT-3.5 generating + GPT-4 evaluating: 31%. Invest in generation quality first.

**Cost**: ToT (b=5) costs ~$0.74/case on Game of 24 vs. $0.47 for best-of-100 CoT — only 1.6x more expensive but achieves 74% vs. 49%. Cost-effective when the problem requires search.

**Zero-shot ToT recipe**: Generate 5 strategies → vote for best → generate 5 solutions from best strategy → vote for best. Requires "a few extra lines of code."

> Besta et al., "Graph of Thoughts: Solving Elaborate Problems with Large Language Models," 2024. [arXiv:2308.09687](https://arxiv.org/abs/2308.09687)

**Key findings**: GoT extends ToT with merging and refinement operations (graph, not tree). On sorting (128 elements): GoT achieves ~62% lower error than ToT while using ~31% fewer API calls. The key addition: aggregating partial solutions from different branches before continuing — something trees cannot do. Uses a merge-sort pattern: split → solve sub-problems (5–10 attempts, keep best) → merge → refine. Total: ~31 LLM calls per instance. At small scale (32 elements), GoT shows "negligible improvement" — the decomposition overhead is not worth it.

**Rules**:
23. **Use ToT only for problems where early decisions lock you in.** ToT's massive gains (4% → 74%) come from tasks where wrong first steps are fatal (Game of 24, crosswords). If the problem allows easy correction later, linear CoT suffices.
24. **Use the zero-shot ToT recipe for design decisions.** When the `electronics-engineer` faces a branching design choice: generate 5 approaches → vote for best → generate 5 detailed plans from best approach → vote for best. Simple, effective, minimal code.
25. **Prune aggressively.** Without pruning, ToT degrades by 19pp. When evaluating candidate approaches in a council, explicitly discard obviously flawed options before deep-diving. Don't waste compute exploring dead ends.
26. **Invest in generation quality over evaluation quality.** Yao et al. showed generation is the bigger bottleneck (64% vs. 31% when swapping model roles). When an agent evaluates another's work, the work quality matters more than the evaluator's sophistication.
27. **Don't use ToT for creative or generative tasks.** Creative Writing improved only 0.6 points (6.93 → 7.56). ToT is a search technique — it helps when there's a right answer to find, not when the task is open-ended generation.

---

### 6. Test-Time Compute Scaling

> Snell et al., "Scaling LLM Test-Time Compute Optimally can be More Effective than Scaling Model Parameters," 2024. [arXiv:2408.03314](https://arxiv.org/abs/2408.03314)

**Key findings**: A compute-optimal strategy achieves the same accuracy as best-of-N using **4x less test-time compute**. On easy/medium problems, a smaller model with optimal test-time compute **outperforms a 14x larger model** in FLOPs-matched evaluation.

**Strategy depends on difficulty**:
| Difficulty | Best Strategy | Finding |
|---|---|---|
| Easy (model often correct) | Sequential revision, minimal parallel sampling | Search causes over-optimization and *degrades* performance |
| Medium (model sometimes correct) | Beam search with PRM verification | Search consistently outperforms best-of-N |
| Hard (model rarely correct) | No method works well | Very little benefit from any test-time scaling |

**Revision danger**: Naive revision causes **38% of already-correct answers to flip to incorrect**. Revisions need guardrails — verify that the revision is actually an improvement before accepting it.

**Process reward models (PRMs) >> outcome reward models (ORMs)**: PRM performance gap grows as sample count increases. Use step-level verification, not just final-answer checking.

**Rules**:
28. **Estimate difficulty before allocating compute.** Easy questions: 1 agent, 1 pass. Medium questions: 3 agents or self-consistency. Hard questions: accept that more compute may not help — use a better model or decompose the problem instead.
29. **Don't revise outputs that are already good.** 38% reversion rate means aggressive revision destroys correct work. When reviewing an agent's output, only request revisions for specific identified problems, not blanket "make it better" requests.
30. **Don't throw compute at impossible problems.** For problems the base model fundamentally cannot solve, no amount of test-time compute will rescue it. If 3 agents all fail, the problem needs decomposition or a different approach, not more agents.
31. **Verify each step, not just the final answer.** PRM >> ORM, and the gap grows with more samples. When the `pricing` agent shows its work, check each intermediate calculation — don't just eyeball the final number.

---

### 7. Process Reward Models and Step-Level Verification

> Lightman et al., "Let's Verify Step by Step," 2023. [arXiv:2305.20050](https://arxiv.org/abs/2305.20050)

**Key findings**: Process supervision (PRM) achieved **78.2%** solve rate on MATH vs. **72.4%** for outcome supervision (ORM) and **69.6%** for majority voting — all using best-of-1860 sampling. The gap widens with more samples: PRM scales log-linearly while ORM and majority voting plateau. PRM's advantage is most pronounced at high sample counts.

**What process supervision catches that outcome supervision misses**:
- Solutions that arrive at the right answer through flawed reasoning (correct-answer-wrong-reasoning)
- Early errors that coincidentally self-correct by the final step
- Reward hacking — exploiting patterns that correlate with correct answers without genuine reasoning

**Active learning is essential**: Focusing human labeling effort on solutions where the verifier is most uncertain (rather than uniform sampling) significantly improves PRM quality. The PRM800K dataset contains ~800K step-level labels collected with this active learning approach.

**First-error detection is the most efficient labeling strategy**: You don't need to label every step. Finding where reasoning first goes wrong is the most informative signal — once the first error is found, subsequent steps can be skipped.

**Rules**:
32. **When reviewing agent work, find the first error and stop there.** Lightman et al.'s most efficient labeling strategy was first-error detection. Don't read an entire plan looking for all problems — find the first wrong step, flag it, and have the agent redo from that point. Errors compound; fixing the first one often fixes downstream issues.
33. **Correct reasoning matters more than correct answers.** Process supervision outperforms outcome supervision because it rewards the reasoning process, not just the endpoint. When evaluating a council's outputs, judge the reasoning quality, not just whether the conclusion "feels right."
34. **Increase sample count for important decisions.** PRM's advantage grows with more samples. For critical decisions (pricing, go/no-go), generate more candidate plans and use step-level evaluation to select the best — don't settle for the first plausible answer.

---

### 8. Preference Alignment (RLHF)

> Ziegler et al., "Fine-Tuning Language Models from Human Preferences," 2019. [arXiv:1909.08593](https://arxiv.org/abs/1909.08593)

**Key findings**: RLHF with GPT-2 774M achieved strong results using only 5,000 human comparisons on stylistic tasks (sentiment, descriptiveness) and 60,000 on summarization. Key insight: even imperfect reward models (~65–75% accuracy) provide meaningful signal. Pairwise comparisons are more reliable than absolute ratings — lower inter-annotator variance.

**Reward hacking is real and dangerous**: Without a KL divergence constraint keeping the model close to its pretrained distribution, the model finds adversarial outputs that score high on the reward model but are gibberish or repetitive. Pushing too hard against any reward signal — including human preferences — leads to degenerate outputs.

**The quality ceiling is set by feedback quality**: Noisy, inconsistent preferences lead to models that exploit surface heuristics (length, certain phrases) rather than genuinely improving. The model learns exactly what you reward — be careful what you signal.

**Rules**:
35. **Use pairwise comparisons, not absolute ratings, when evaluating agent outputs.** "This pricing plan is better than that one because X" is more useful than "rate this plan 7/10." Ziegler et al. showed comparisons have lower inter-annotator variance.
36. **State *why* you prefer one output over another.** The reward model learns from the comparison, not just the label. When providing feedback to an agent, explain the reasoning: "I prefer plan A because it accounts for shipping costs that plan B ignored." This verbal preference signal sharpens subsequent outputs.
37. **Be consistent in what you reward.** If you sometimes reward brevity and sometimes reward detail, the agent will exploit surface features rather than learning your true preferences. Establish clear quality criteria before evaluating.
38. **Don't push too hard on any single metric.** Ziegler et al.'s KL constraint exists because over-optimizing one dimension degrades everything else. If you keep asking for "more detail," you'll get verbose gibberish. If you keep asking for "shorter," you'll get vacuous summaries. Balance competing quality dimensions.

---

### 9. Compositional / Socratic Models

> Zeng et al., "Socratic Models: Composing Zero-Shot Multimodal Reasoning with Language," 2022. [arXiv:2204.00598](https://arxiv.org/abs/2204.00598)

**Key findings**: Multiple specialist models (CLIP for vision, GPT-3 for reasoning, ViLD for objects) communicating through natural language achieve ~80–95% of fine-tuned performance on structured benchmarks while dramatically exceeding fine-tuned systems on open-world/open-vocabulary generalization. Zero-shot composition requires no training — only prompt engineering.

**Natural language is a sufficient coordination protocol**: Models that were never trained together can collaborate effectively using structured natural language as the interchange format. No shared embeddings, custom protocols, or joint training needed.

**Errors cascade multiplicatively**: If each stage has 90% accuracy, a 3-stage pipeline has ~73%. Keep pipelines short (2–3 agents max per task chain). Cross-validation between agents (if two independent agents agree, confidence is much higher) partially mitigates this.

**Prompt template is the critical engineering artifact**: Small changes in prompt wording cause meaningful performance variations. The structure of the handoff between agents — what information is passed and in what format — matters more than the sophistication of any individual agent.

**Zero-shot excels at novel tasks; fine-tuned excels at narrow benchmarks**: Below ~10K–50K labeled examples for a specific task, zero-shot composition is competitive. Above that, fine-tuning wins on metrics.

**Rules**:
39. **Keep agent pipelines to 2–3 agents maximum.** Cascading errors compound multiplicatively. `artist → software-engineer → artist` (3 steps) is fine. `founder → marketing → writer → designer → pricing → manufacturing` (6 steps) will accumulate errors. Break long chains into independent parallel tracks.
40. **Specify output format explicitly in every agent prompt.** The prompt template is your API contract. "Deliver: BOM table with columns [component, qty, unit cost, total, supplier]" is infinitely better than "give me the BOM." Structured handoffs prevent information loss.
41. **Use cross-validation between agents for critical facts.** If `electronics-engineer` and `manufacturing` independently agree on a component cost, confidence is much higher than either alone. Design workflows so critical numbers get verified by independent agents.
42. **Design agents for swappability.** Because the interface is natural language, any agent can be upgraded or replaced without changing the pipeline. Don't create implicit dependencies on specific agent behaviors — make the contract explicit in the prompt.

---

### Summary: Decision Tree

```
Is the task a simple lookup or single-step?
  YES → 1 agent, 1 pass, no CoT needed
  NO ↓

Is it a quantitative / multi-step reasoning task?
  YES → Use CoT (Rule 1–9). Then:
    Is the answer critical?
      YES → Self-consistency: sample 5x (Rule 10–13)
        Do samples agree?
          YES → Accept
          NO → Council debate (Multiagent Debate Rules 1–10)
      NO → Accept single CoT answer
  NO ↓

Is it a style/creative task?
  YES → Self-refine, 2–3 iterations max (Rule 15, 19)
  NO ↓

Is it a design decision with branching trade-offs?
  YES → ToT: 5 strategies → vote → 5 plans → vote (Rule 23–27)
  NO ↓

Is it a high-stakes cross-domain decision?
  YES → Council of 3+ specialist agents, 2–4 debate rounds
         (Multiagent Debate Rules 1–10)
  NO → 1 specialist agent, relevant docs in context (Rule 20–22)
```

### Quick Reference: All Rules by Paper

| # | Rule | Source |
|---|------|--------|
| 1 | Use CoT only for multi-step reasoning tasks | Wei et al. 2022 |
| 2 | Write reasoning in natural language, not just equations | Wei et al. 2022 |
| 3 | Place the chain before the answer, never after | Wei et al. 2022 |
| 4 | Pair CoT with external tools for calculation | Wei et al. 2022 |
| 5 | 8 exemplars is the saturation point | Wei et al. 2022 |
| 6 | Don't worry about exact chain wording | Wei et al. 2022 |
| 7 | Default to "Let's think step by step" for zero-shot | Kojima et al. 2022 |
| 8 | Always extract the answer explicitly after reasoning | Kojima et al. 2022 |
| 9 | Use zero-shot CoT when you lack good exemplars | Kojima et al. 2022 |
| 10 | Sample 5 responses before trusting any single answer | Wang et al. 2022 |
| 11 | Use simple majority voting, not weighted schemes | Wang et al. 2022 |
| 12 | Self-consistency is your first-line defense | Wang et al. 2022 |
| 13 | Generate many, verify the best | Cobbe et al. 2021 |
| 14 | Use Reflexion for code/decision tasks with concrete feedback | Shinn et al. 2023 |
| 15 | Limit self-refine to 2–3 iterations maximum | Madaan et al. 2023 |
| 16 | Keep only the last 1–3 reflections in context | Shinn et al. 2023 |
| 17 | Never use single-agent reflection for fact-checking | Du et al. 2023 |
| 18 | Invest in test/feedback quality, not reflection rounds | Shinn et al. 2023 |
| 19 | Self-refine for style (writer/artist), not facts (pricing/EE) | Madaan et al. 2023 |
| 20 | Always provide source documents in context | Guu et al. 2020 |
| 21 | Retriever quality > model size for factual tasks | Guu et al. 2020 |
| 22 | "Good enough" context is sufficient | Guu et al. 2020 |
| 23 | Use ToT only for problems where early decisions lock you in | Yao et al. 2023 |
| 24 | Use the zero-shot ToT recipe for design decisions | Yao et al. 2023 |
| 25 | Prune aggressively | Yao et al. 2023 |
| 26 | Invest in generation quality over evaluation quality | Yao et al. 2023 |
| 27 | Don't use ToT for creative or generative tasks | Yao et al. 2023 |
| 28 | Estimate difficulty before allocating compute | Snell et al. 2024 |
| 29 | Don't revise outputs that are already good | Snell et al. 2024 |
| 30 | Don't throw compute at impossible problems | Snell et al. 2024 |
| 31 | Verify each step, not just the final answer | Snell et al. 2024 |
| 32 | Find the first error and stop there | Lightman et al. 2023 |
| 33 | Correct reasoning matters more than correct answers | Lightman et al. 2023 |
| 34 | Increase sample count for important decisions | Lightman et al. 2023 |
| 35 | Use pairwise comparisons, not absolute ratings | Ziegler et al. 2019 |
| 36 | State *why* you prefer one output over another | Ziegler et al. 2019 |
| 37 | Be consistent in what you reward | Ziegler et al. 2019 |
| 38 | Don't push too hard on any single metric | Ziegler et al. 2019 |
| 39 | Keep agent pipelines to 2–3 agents maximum | Zeng et al. 2022 |
| 40 | Specify output format explicitly in every agent prompt | Zeng et al. 2022 |
| 41 | Use cross-validation between agents for critical facts | Zeng et al. 2022 |
| 42 | Design agents for swappability | Zeng et al. 2022 |

---

## Stack Context (give this to every agent)

- **Hardware**: Diamond LED panel, 604 pixels of WS2812B, 12V/20A, ~21×21cm
- **Controller**: Raspberry Pi Zero 2W, GPIO 10 (SPI MOSI), 3.3V → level shifter → data line
- **Firmware**: Kotlin/JVM, `target-rpi` module, dual-coroutine (shader + render)
- **Shaders**: Perlin/FBM noise, `WarmColorShaderAlgorithm` (yellow→red→magenta), `V1RedShaderAlgorithm`
- **Dev preview**: macOS + OPENRNDR, `sun-openrndr` module
- **Deploy**: `./deploy-rpi.sh`, systemd autostart via `setup-autostart.sh`
- **Prototype status**: Functional hardware, firmware running, not yet production-ready
