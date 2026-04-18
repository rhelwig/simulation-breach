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

Default exclusions:

- Players
- Bosses
- Tamed animals with owners
- Utility mobs, unless explicitly enabled
- Ambient mobs, unless explicitly enabled
- Any entity tagged or configured as immune

The initial conversion chance must be low enough that a world does not collapse immediately. The base default chance is `0.00005` per eligible outbreak check before difficulty scaling. The implementation must define the check cadence clearly in code and config documentation.

Initial outbreak chance must scale by world difficulty:

- Peaceful: lowest chance.
- Easy: lower than Normal.
- Normal: baseline chance.
- Hard: higher than Normal.
- Hardcore: highest chance, where the edition exposes Hardcore as a world mode.

Difficulty scaling must be configurable. Bedrock implementations that cannot distinguish Hardcore from Hard should use the Hard multiplier unless the platform exposes a separate Hardcore flag.

### 3.2 Agent Combat Conversion

Conversion must be driven by successful melee hits, not passive collision. This avoids accidental conversion spam and keeps behavior easier to balance.

When The Agent lands a melee hit:

- Target is non-hostile: convert the target into a corrupted hostile mob.
- Target is hostile and originally hostile: convert the target into The Agent with a moderate configurable chance.
- Target is hostile but originally non-hostile: apply the configured passive-origin promotion rule.
- Target is a player: attack normally. Player conversion is out of scope for version 1.

The corrupted hostile result may be a vanilla hostile mob such as zombie, skeleton, husk, or stray. Version 1 may use vanilla hostile mobs. Later versions may add custom corrupted variants for clearer visuals and better control.

### 3.3 Agent Targeting and Movement

Agents must prioritize nearby players as their primary combat targets. When an Agent identifies a nearby player, it should move toward that player and attack using ordinary hostile-mob combat behavior.

Agents may briefly deviate from their path to attempt mob conversion, but the deviation must not make mob conversion look like the primary goal when a player is available. Conversion detours should be short, bounded, and configurable.

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

- Duration must be configurable and expected to test in the range of 20 to 100 ticks, or about 1 to 5 seconds.
- Default duration should start at 60 ticks, or about 3 seconds, until gameplay testing suggests a better value.
- The transforming mob should shake in a manner similar to a Creeper swelling before it explodes.
- The transforming mob should play an audible cue.
- Version 1 may reuse the Creeper priming sound as a placeholder.
- A later version should replace the placeholder with an original Simulation Breach transformation sound effect.

Transformation state must be server-authoritative. If a transforming entity unloads, dies, or becomes invalid before completion, the implementation must either persist and resume the pending transformation or cancel it safely without duplicating entities.

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
| `initialPassiveAgentChance` | `0.00005` | Base chance for an eligible non-hostile mob to become The Agent during an outbreak check before difficulty scaling. |
| `peacefulInitialAgentChanceMultiplier` | `0.10` | Multiplier for initial outbreaks in Peaceful worlds. |
| `easyInitialAgentChanceMultiplier` | `0.50` | Multiplier for initial outbreaks in Easy worlds. |
| `normalInitialAgentChanceMultiplier` | `1.00` | Baseline multiplier for initial outbreaks in Normal worlds. |
| `hardInitialAgentChanceMultiplier` | `2.00` | Multiplier for initial outbreaks in Hard worlds. |
| `hardcoreInitialAgentChanceMultiplier` | `3.00` | Multiplier for initial outbreaks in Hardcore worlds where available. |
| `agentConvertPassiveChance` | `1.0` | Chance that an Agent melee hit corrupts a non-hostile mob. |
| `agentConvertHostileToAgentChance` | `0.35` | Chance that an Agent melee hit converts an originally hostile mob into The Agent. |
| `agentConvertCorruptedPassiveToAgentChance` | `0.20` | Chance that an Agent melee hit promotes a corrupted passive-origin mob when promotion is enabled. |
| `agentConversionCooldownTicks` | `40` | Minimum ticks between conversion attempts per Agent on Java. Bedrock may represent this in ticks or seconds. |
| `transformationDurationTicks` | `60` | Time from conversion trigger to replacement. Expected tuning range is 20 to 100 ticks. |
| `agentConversionDetourRadius` | `4` | Maximum short detour radius for converting mobs while pursuing a player. |
| `maxAgentsPerChunk` | `3` | Soft cap for Agents in a local area. Bedrock may approximate by chunk-equivalent region. |
| `passivePromotionMode` | `PROMOTED_CORRUPTION` | Rule for passive-origin corrupted mobs. |
| `excludeVillagers` | `false` | Whether villagers are immune from initial and passive conversion. |
| `excludeTamedAnimals` | `true` | Whether owned tamed animals are immune. |
| `debugLogging` | `false` | Enables extra conversion and performance logs. |

Additional recommended config:

- `enableInitialOutbreaks`
- `enableAgentSpread`
- `eligiblePassiveEntityAllowlist`
- `eligiblePassiveEntityDenylist`
- `hostileConversionAllowlist`
- `corruptedHostilePool`
- `immuneEntityTypes`
- `outbreakCheckIntervalTicks`
- `maxAgentsPerDimension`
- `conversionRequiresLineOfSight`
- `enablePlaceholderCreeperTransformSound`
- `agentPlayerDetectionRange`
- `agentPlayerTargetPriority`

Config validation:

- Chances must be clamped to `0.0` through `1.0`.
- Difficulty multipliers must be non-negative and ordered so Peaceful is the lowest and Hardcore is the highest when Hardcore is available.
- Cooldowns and intervals must be positive.
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
- Delayed transformation with visible warning.
- Server-side eligibility checks.
- Immunity for players and bosses in version 1.
- No conversion from mere collision.

Recommended safeguards:

- Dimension cap.
- Night, biome, or difficulty modifiers.
- Grace period after a mob is converted.
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
- Natural conversions.
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

## 12. Version Milestones

### Version 1

- Rare passive-origin natural Agent emergence.
- Agent melee conversion rules.
- Difficulty-scaled initial outbreak chance.
- Player-priority Agent targeting with bounded conversion detours.
- Delayed shaking transformation before replacement.
- Placeholder Creeper priming sound for transformation.
- Corrupted hostile intermediate state using vanilla hostile mobs.
- Persistent origin and infection data.
- Config file with defaults and validation.
- Local cap and cooldown safeguards.
- Basic Java Fabric assets and metadata.
- World-specific config.

### Version 2

- Custom corrupted mob variants.
- Stronger visuals and audio.
- Original Simulation Breach transformation sound.
- Optional config screen.
- More granular entity allowlists and denylists.
- Admin commands for outbreak control.

### Later

- Bedrock behavior/resource pack implementation.
- Coordinated Agent behavior.
- Village-specific outbreak behavior.
- Additional configurable outbreak events.

## 13. Acceptance Criteria

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
