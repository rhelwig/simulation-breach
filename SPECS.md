# Simulation Breach Specification

Status: target design specification. The current Java repository may not implement every requirement yet.

Source basis: shared design discussion at https://chatgpt.com/share/69e38680-31f8-83ea-9d7c-f8c50aed3c1d, plus the current Fabric project metadata.

## 1. Product Identity

Simulation Breach is a Minecraft outbreak mod about entity identity being overwritten. A rare entity emerges from ordinary mobs and spreads by corrupting or replacing other mobs. The tone should be eerie, systemic, and simulation-themed.

Player-facing names, messages, assets, and metadata must avoid protected references to existing films, characters, or franchises. The shipped mod must use original language even when it evokes identity takeover, replication, and inevitability.

Recommended player-facing terminology:

- Mod name: `Simulation Breach`
- Primary spreading entity: `The Agent`
- Corrupted intermediate state: `Corrupted`
- System event: `Breach`

Internal identifiers should prefer neutral names such as `agent`, `corrupted`, `infection`, `originDisposition`, and `conversion`.

## 2. Edition Scope

The gameplay rules in this document are edition-neutral and must be expressible for both Minecraft Java Edition and Minecraft Bedrock Edition.

This repository is the Java Fabric implementation. A Bedrock implementation is not included here, but the same rules should be implementable through a Bedrock behavior pack, resource pack, and, where required, Script API logic.

Edition-specific requirements:

- Java Edition: implement through Fabric server-side entity hooks, custom entity registration, persistent entity data, resource assets, and optional config UI integration.
- Bedrock Edition: implement through custom entities, component groups, events, tags, dynamic properties or scoreboards, commands, and Script API where vanilla entity hit detection or replacement cannot be represented declaratively.
- Cross-edition parity means the same user-visible mechanics, names, probabilities, stages, and configuration concepts. It does not require identical internal code structure or exact tick timing.

## 3. Core Gameplay

### 3.1 Outbreak Start

Only naturally non-hostile mobs are eligible for random initial conversion into The Agent.

Eligible examples:

- Cows
- Sheep
- Pigs
- Chickens
- Rabbits
- Villagers, unless excluded by config

Default natural-outbreak exclusions:

- Players
- Bosses
- Tamed animals with owners
- Utility mobs, unless explicitly enabled
- Ambient mobs, unless explicitly enabled
- Any entity tagged or configured as immune

Initial outbreak eligibility must also be player-adjacent. A mob should only be eligible for natural conversion when it is within the configured distance of a non-spectator player and, by default, has a reasonable navigable route to that player. A mob sealed in an unreachable cave, pen, or room near a player should not naturally transform just because it is close by. Java implementations should use server-side mob navigation or pathfinding where practical. Bedrock implementations may approximate this with Script API path checks, line-of-sight plus navigation heuristics, or conservative exclusion rules when exact pathfinding is unavailable.

The initial conversion chance must be low enough that a world does not collapse immediately, but high enough that normal play can reveal the mechanic without multi-day waiting. The base default chance is `0.001` per eligible outbreak check before difficulty scaling. The implementation must define the check cadence clearly in code and config documentation.

Initial outbreak chance must scale by world difficulty:

- Peaceful: lowest chance.
- Easy: lower than Normal.
- Normal: baseline chance.
- Hard: higher than Normal.
- Hardcore: highest chance, where the edition exposes Hardcore as a world mode.

Difficulty scaling must be configurable. Bedrock implementations that cannot distinguish Hardcore from Hard should use the Hard multiplier unless the platform exposes a separate Hardcore flag.

When a natural random outbreak completes and produces an Agent, the game should send a configurable system-style chat notice. The notice should read like a computer alert, for example: `[SIMULATION BREACH] BREACH DETECTED :: ASSIGNING AGENT`. It should only be sent for natural random outbreaks, not for Agent-driven conversions or operator debug commands.

Player actions should create local outbreak pressure instead of the world relying only on passive random checks. These pressure events must be specific, local, time-limited, and configurable. They increase the chance that eligible nearby mobs transform into The Agent, but they must not bypass player-adjacency, reachability, immunity, caps, or delayed transformation requirements.

Players lingering in one area should also create gradual local outbreak pressure. This pressure must be configurable, bounded by a maximum multiplier, and reset when the player moves outside the configured area or leaves the dimension. Linger pressure increases the natural outbreak chance for eligible mobs near that area, but it must not bypass player-adjacency, reachability, immunity, caps, or delayed transformation requirements.

Version 1 player action pressure should prioritize actions that affect villagers and villages:

- Highest pressure: breaking, placing, or changing blocks that are part of village buildings or villager life, such as beds, job-site blocks, bells, doors, or walls around inhabited village structures.
- High pressure: directly harming villagers, killing villagers, disrupting villager work or sleep locations, or triggering events that materially alter village safety.
- Moderate pressure: major block changes near villagers or within a village boundary, even when the block is not a villager point of interest.
- Low pressure: ordinary non-village block changes near eligible passive mobs.

The pressure system should record enough context to explain debug logs: player, action type, location, affected village or villager context when known, radius, duration, and multiplier. Pressure from repeated actions should have a cap or diminishing returns so a player cannot force immediate world collapse by spam-clicking blocks.

### 3.2 Agent Combat Conversion

Conversion must be driven by successful melee hits, not passive collision. This avoids accidental conversion spam and keeps behavior easier to balance.

When The Agent lands a melee hit:

- Target is non-hostile: convert the target into a corrupted hostile mob.
- Target is hostile and originally hostile: convert the target into The Agent with a moderate configurable chance.
- Target is hostile but originally non-hostile: apply the configured passive-origin promotion rule.
- Target is a player: attack normally. Player conversion is out of scope for version 1.

Owned tamed animals are protected from random natural outbreak selection by default, but they are not protected from direct Agent spread. If an Agent passes near a tamed animal during its conversion sweep, that animal can still begin the delayed transformation.

The corrupted hostile result may be a vanilla hostile mob such as zombie, skeleton, husk, or stray. Version 1 may use vanilla hostile mobs. Later versions may add custom corrupted variants for clearer visuals and better control.

Until the corrupted-hostile replacement path is implemented, version 1 Java builds may route Agent mob-conversion attempts through the delayed Agent transformation path so spread behavior remains testable. This fallback must still use the configured chances, local caps, delayed transformation, and player counterplay rules.

### 3.3 Agent Targeting and Movement

Agents must prioritize nearby players as their primary combat targets. When an Agent identifies a nearby player, it should move toward that player and attack using ordinary hostile-mob combat behavior.

Agents may briefly deviate from their path to attempt mob conversion, but the deviation must not make mob conversion look like the primary goal when a player is available. Conversion detours should be short, bounded, and configurable.

When an Agent is close enough to eligible non-player mobs during a conversion pass, it should attempt every eligible mob within its configured detour radius rather than choosing one random target. The chance for each attempt should be high by default, especially for mobs that are already hostile or corrupted. These conversion sweeps must be cooldown-limited and must respect local Agent caps.

If no player target is available, Agents may pursue eligible conversion targets according to the normal conversion rules and local caps.

### 3.4 Passive-Origin Promotion Rule

The mod must support a named configuration option for how corrupted mobs that began as non-hostile mobs can progress.

Supported modes:

- `STRICT_ORIGIN`: a mob that originally started non-hostile can never become The Agent through ordinary Agent melee conversion after corruption.
- `PROMOTED_CORRUPTION`: once a non-hostile mob has been corrupted into a hostile mob, a later Agent melee hit may promote it into The Agent using a separate configurable chance.

Default mode: `PROMOTED_CORRUPTION`.

The config UI must not expose this as vague "A/B" wording. Use "Passive-origin promotion rule" or equivalent.

### 3.5 Conversion Method

Minecraft implementations should not assume one entity can always morph into another in place. The portable conversion model is:

1. Read the source entity state.
2. Put the source entity into a transforming state.
3. Play transformation visuals and sound cues for the configured duration.
4. Create the replacement entity when the transformation completes.
5. Transfer required breach data and selected gameplay state.
6. Spawn the replacement at the source position.
7. Remove the source entity without duplicate drops or duplicate XP.

State to preserve when practical:

- Position
- Rotation
- Dimension
- Custom name
- Health ratio, if compatible
- Relevant status or equipment state, if compatible
- Breach identity data

State that may be reset for balance:

- Active target
- Pathfinding state
- Vanilla AI memory
- Age or breeding state

### 3.6 Transformation Presentation

Conversion must not be instant. A mob that is being corrupted or converted into an Agent must visibly transform for a configurable duration before replacement.

Version 1 transformation behavior:

- Duration must be configurable and expected to test in the range of 120 to 200 ticks, or about 6 to 10 seconds.
- Default duration should start at 180 ticks, or about 9 seconds, until gameplay testing suggests a better value.
- The transforming mob should shake in a manner similar to a Creeper swelling before it explodes.
- The transforming mob should shake violently enough to be noticeable at normal play distance, including unnatural side-to-side motion and rotating or spinning offsets that do not look like expected vanilla mob behavior.
- The transforming mob should play the original Simulation Breach Agent transformation sound.
- Version 1 may retain the Creeper priming sound as an optional fallback while audio assets are iterated.

Transformation state must be server-authoritative. If a transforming entity unloads, dies, or becomes invalid before completion, the implementation must either persist and resume the pending transformation or cancel it safely without duplicating entities.

If a player sees a mob transforming and kills it before the transformation completes, the transformation must fail and The Agent must not spawn. This is required gameplay counterplay, not an optional balance detail.

### 3.7 Agent Rewards

Killing an Agent should provide enough experience to feel proportionate to the combat risk and outbreak-control value. The reward must be configurable. The Java default should start above ordinary zombies and similar common hostiles because Agents have higher health, pursue players, and can convert nearby mobs while chasing.

## 4. Entity Identity Data

Each affected entity must have explicit persisted breach data.

Required fields:

- `originDisposition`: `PASSIVE` or `HOSTILE`
- `originalEntityType`: namespaced entity identifier, such as `minecraft:cow`
- `infectionStage`: `NONE`, `CORRUPTED`, or `AGENT`
- `transformationState`: `NONE` or `TRANSFORMING`

Recommended optional fields:

- `lastConversionGameTime`
- `convertedByEntityUuid`
- `conversionGeneration`
- `immuneUntilGameTime`
- `transformationStartedGameTime`
- `transformationDurationTicks`
- `pendingReplacementEntityType`
- `pendingInfectionStage`
- `debugSource`

Data requirements:

- Data must survive saves and reloads.
- Data must transfer when a converted entity is replaced.
- Data must be server-authoritative.
- Client-only visuals must derive from synced state, not own the rules.

## 5. Configuration

All defaults must be defined in one authoritative place and exposed through a user-editable config file. A Java config screen is optional for version 1 but should be compatible with Mod Menu or a similar UI if added later.

Default values:

| Key | Default | Meaning |
| --- | ---: | --- |
| `initialPassiveAgentChance` | `0.001` | Base chance for an eligible non-hostile mob to become The Agent during an outbreak check before difficulty scaling. |
| `peacefulInitialAgentChanceMultiplier` | `0.10` | Multiplier for initial outbreaks in Peaceful worlds. |
| `easyInitialAgentChanceMultiplier` | `0.50` | Multiplier for initial outbreaks in Easy worlds. |
| `normalInitialAgentChanceMultiplier` | `1.00` | Baseline multiplier for initial outbreaks in Normal worlds. |
| `hardInitialAgentChanceMultiplier` | `2.00` | Multiplier for initial outbreaks in Hard worlds. |
| `hardcoreInitialAgentChanceMultiplier` | `3.00` | Multiplier for initial outbreaks in Hardcore worlds where available. |
| `agentConvertPassiveChance` | `1.0` | Chance that an Agent melee hit corrupts a non-hostile mob. |
| `agentConvertHostileToAgentChance` | `0.75` | Chance that an Agent melee hit converts an originally hostile mob into The Agent. |
| `agentConvertCorruptedPassiveToAgentChance` | `0.75` | Chance that an Agent melee hit promotes a corrupted passive-origin mob when promotion is enabled. |
| `agentConversionCooldownTicks` | `20` | Minimum ticks between conversion sweeps per Agent on Java. Bedrock may represent this in ticks or seconds. |
| `transformationDurationTicks` | `180` | Time from conversion trigger to replacement. Expected tuning range is 120 to 200 ticks. |
| `agentExperienceReward` | `12` | Base XP reward for killing an Agent. |
| `agentConversionDetourRadius` | `4` | Maximum short detour radius for converting mobs while pursuing a player. |
| `maxAgentsPerChunk` | `8` | Soft cap for Agents in a local area. Bedrock may approximate by chunk-equivalent region. |
| `enableInitialOutbreaks` | `true` | Whether rare natural initial Agent outbreaks are enabled. |
| `outbreakCheckIntervalTicks` | `200` | Server ticks between scheduled initial outbreak checks. |
| `outbreakScanLimitPerLevel` | `768` | Maximum loaded entities inspected per level during one scheduled outbreak check. |
| `outbreakEligibleRollsPerLevel` | `64` | Maximum eligible non-hostile mobs rolled per level during one scheduled outbreak check. |
| `initialOutbreakPlayerSearchRadius` | `48.0` | Maximum distance from an eligible mob to a non-spectator player for natural outbreak checks. |
| `initialOutbreakRequiresReachablePlayer` | `true` | Whether natural outbreak candidates must have a navigable route to a nearby player. |
| `enableAgentTransformSound` | `true` | Whether transformations play the original Simulation Breach Agent transformation sound. |
| `enablePlaceholderCreeperTransformSound` | `true` | Whether transformations use the Creeper priming sound when the original Agent sound is disabled. |
| `enablePlayerLingerOutbreakPressure` | `true` | Whether lingering in one area gradually increases local natural outbreak chance. |
| `playerLingerPressureRadius` | `48.0` | Radius around a player's linger anchor where local pressure applies, and movement distance that resets the anchor. |
| `playerLingerPressureGraceTicks` | `1200` | Ticks a player can remain in one area before linger pressure starts increasing. |
| `playerLingerPressureRampTicks` | `4800` | Ticks after the grace period required to reach the maximum linger pressure multiplier. |
| `maxPlayerLingerPressureMultiplier` | `4.0` | Maximum multiplier applied to natural outbreak chance from player linger pressure. |
| `enableNaturalOutbreakChatNotice` | `true` | Whether completed natural random Agent outbreaks send a computer-styled chat notice. |
| `passivePromotionMode` | `PROMOTED_CORRUPTION` | Rule for passive-origin corrupted mobs. |
| `excludeVillagers` | `false` | Whether villagers are immune from initial and passive conversion. |
| `excludeTamedAnimals` | `true` | Whether owned tamed animals are excluded from random natural outbreak selection. Agent spread can still convert them. |
| `debugLogging` | `false` | Enables extra conversion and performance logs. |

Additional recommended config:

- `enableAgentSpread`
- `eligiblePassiveEntityAllowlist`
- `eligiblePassiveEntityDenylist`
- `hostileConversionAllowlist`
- `corruptedHostilePool`
- `immuneEntityTypes`
- `maxAgentsPerDimension`
- `conversionRequiresLineOfSight`
- `agentPlayerDetectionRange`
- `agentPlayerTargetPriority`
- `enablePlayerActionOutbreakPressure`
- `playerActionPressureRadius`
- `playerActionPressureDurationTicks`
- `villageModificationPressureMultiplier`
- `villagerAffectingPressureMultiplier`
- `genericBlockChangePressureMultiplier`
- `maxPlayerActionPressureMultiplier`

Version 1 Java must provide an operator-only debug command for forcing a nearby mob into Agent transformation, such as `/simulationbreach transform_nearest [radius]`, so transformation presentation and replacement can be tested deterministically.

Config validation:

- Chances must be clamped to `0.0` through `1.0`.
- Difficulty multipliers must be non-negative and ordered so Peaceful is the lowest and Hardcore is the highest when Hardcore is available.
- Cooldowns and intervals must be positive.
- Player search radii and pressure radii must be positive.
- Linger pressure grace ticks must be non-negative, ramp ticks must be positive, and the maximum multiplier must be at least `1.0`.
- Transformation duration must be positive.
- Caps must allow `0` to disable spread where useful.
- Invalid entity identifiers must be logged and ignored.

World-specific config is preferred long term so one save can be an outbreak world while another remains normal.

## 6. Balance and Safeguards

The spread can become exponential. The implementation must include safeguards before enabling release builds.

Required safeguards:

- Per-Agent conversion cooldown.
- Local Agent cap.
- Configurable conversion chances.
- Difficulty-scaled initial outbreak chance.
- Player-adjacent initial outbreak checks with reachable-route validation where practical.
- Tamed animal exclusion from random natural outbreak selection.
- Delayed transformation with visible warning.
- Server-side eligibility checks.
- Immunity for players and bosses in version 1.
- No conversion from mere collision.

Recommended safeguards:

- Dimension cap.
- Night, biome, or difficulty modifiers.
- Grace period after a mob is converted.
- Caps or diminishing returns for repeated player action pressure.
- Spawn or conversion particles that make outbreaks discoverable.
- Admin command or config option to disable spread in an existing world.

## 7. Performance Requirements

Performance must be tracked during implementation.

Rules:

- Do not scan every loaded entity every tick.
- Use scheduled sampling, event hooks, or bounded local checks.
- Run conversion logic on the server side.
- Avoid repeated allocation-heavy searches during common mob ticks.
- Keep debug logging off by default.

Metrics to log during development:

- Outbreak checks per interval.
- Eligible mobs sampled.
- Eligible mobs rejected because no nearby reachable player exists.
- Natural conversions.
- Player action pressure events by type.
- Active player linger pressure records.
- Natural outbreak rolls affected by player linger pressure.
- Active player action pressure records.
- Melee conversion attempts.
- Successful conversions by target type.
- Conversion denial reasons when debug logging is enabled.
- Approximate time spent in outbreak and conversion routines.

## 8. Architecture

Recommended modules:

- `ModConfig`: config defaults, load, save, validation, and active values.
- `InfectionRules`: pure rule decisions for eligibility, chance checks, and promotion mode.
- `InfectionData`: persisted per-entity breach identity.
- `ConversionManager`: replacement entity creation, data transfer, source removal, and logging.
- `TransformationManager`: pending transformation state, timing, shaking visuals, sound triggers, completion, and cancellation.
- `OutbreakManager`: scheduled natural outbreak checks.
- `AgentEntity`: custom entity behavior, player-priority targeting, and melee conversion trigger.
- `PlatformAdapters`: edition-specific services for entity lookup, persistent data, spawning, and config.

Architecture requirements:

- Keep gameplay rules separate from rendering.
- Keep pure rule functions testable without a running client.
- Keep edition-specific API calls behind adapter boundaries where practical.
- Do not scatter config defaults across unrelated classes.

## 9. Java Fabric Implementation Notes

Target metadata from the current repository:

- Mod id: `simulation-breach`
- Minecraft version: `26.1.2`
- Fabric Loader: `0.19.2` or newer
- Fabric API: `0.146.1+26.1.2`
- Java: `25` or newer

Java implementation should:

- Register a custom Agent entity type.
- Persist breach data in a stable server-side format.
- Use Fabric events or mixins only where a cleaner event hook is unavailable.
- Register config loading during mod initialization.
- Keep client code limited to renderer, model, texture, particles, sounds, and screens.
- Avoid using the template example mixins as gameplay architecture.

## 10. Bedrock Implementation Notes

Bedrock implementation should:

- Provide a behavior pack and resource pack.
- Define `simulation_breach:agent` as the primary custom entity.
- Use tags, dynamic properties, scoreboards, or component groups to represent `originDisposition`, `originalEntityType`, and `infectionStage`.
- Use Script API if needed for reliable hit detection, chance rolls, persistent custom data, delayed transformation, shaking presentation, and entity replacement.
- Use commands or script spawning to replace entities while preserving position and required state.
- Approximate Java chunk caps with an equivalent local-area cap if chunk APIs are unavailable.

Bedrock limitations must be documented in the Bedrock pack if exact parity is not possible.

## 11. User-Facing Text Guidelines

Use original simulation and replacement language. Avoid direct references to protected media.

Acceptable tone examples:

- "A breach has entered the world."
- "Another entity has been rewritten."
- "The pattern is spreading."
- "The Agent has corrected a deviation."

Do not use protected character names, franchise names, or direct catchphrases from existing media.

## 12. Visual and Audio Assets

The Agent should be visually readable as a distinct humanoid entity. Version 1 may use a placeholder humanoid model and skin while core mechanics are implemented, but the final asset direction should feel systematic, uncanny, and original.

Required version 1 asset planning:

- Agent skin or texture.
- Placeholder humanoid Agent model if a custom model is not ready.
- Agent ambient voice sound.
- Transformation visual treatment driven by shaking and optional particles.
- Original transformation sound with optional Creeper fallback.

Later asset goals:

- Final Agent texture or model.
- Corrupted mob visual treatment.
- Transformation particles or shader-like effects that remain readable in normal survival play.

The Agent skin and the transformation presentation should be designed separately. The transformation sells the active conversion process; the Agent skin sells the completed identity.

## 13. Version Milestones

### Version 1

- Rare passive-origin natural Agent emergence.
- Agent melee conversion rules.
- Difficulty-scaled initial outbreak chance.
- Player-priority Agent targeting with bounded conversion detours.
- Delayed shaking transformation before replacement.
- Original Agent transformation sound with optional Creeper fallback.
- Agent ambient voice sound.
- Computer-styled chat notice when a natural random outbreak creates an Agent.
- Gradual local natural outbreak pressure when players linger in one area.
- Placeholder Agent skin or texture.
- Corrupted hostile intermediate state using vanilla hostile mobs.
- Persistent origin and infection data.
- Config file with defaults and validation.
- Local cap and cooldown safeguards.
- Basic Java Fabric assets and metadata.
- World-specific config.

### Version 2

- Custom corrupted mob variants.
- Stronger visuals and audio.
- Additional transformation and corrupted-mob audio.
- Final Agent skin or model.
- Optional config screen.
- More granular entity allowlists and denylists.
- Admin commands for outbreak control.

### Later

- Bedrock behavior/resource pack implementation.
- Coordinated Agent behavior.
- Village-specific outbreak behavior.
- Additional configurable outbreak events.

## 14. Acceptance Criteria

A Java or Bedrock implementation satisfies this spec when:

- A rare outbreak can start from eligible non-hostile mobs.
- Initial outbreak chance scales by difficulty, with Peaceful lowest and Hardcore highest where available.
- Agent melee hits apply the specified conversion rules.
- Agents prioritize identified nearby players while allowing only short, bounded conversion detours.
- Conversions use a visible delayed transformation instead of instant replacement.
- Converted mobs persist origin identity and infection stage across replacement and save reload.
- Config defaults match this document or a documented later spec revision.
- Spread is bounded by cooldowns and local caps.
- Player-facing names and text avoid protected references.
- Debug and performance logging can be enabled without changing code.
- The README clearly states edition support, loader requirements, and project status.
