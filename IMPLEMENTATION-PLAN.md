# Implementation Plan

## 2026-04-18 Documentation Baseline

- [x] Read project instructions and repository metadata.
- [x] Retrieve and extract the shared design thread.
- [x] Inspect current README, Gradle metadata, Fabric mod metadata, and source layout.
- [x] Create `SPECS.md` from the shared design thread with Java and Bedrock edition guidance.
- [x] Update `README.md` with user-facing information for the Java Fabric mod.
- [x] Run a lightweight verification pass and record results.

## Performance Log

- 2026-04-23: Agent death/reversion/block-pressure slice repository searches completed in under 1 second each using `find`/`grep` because `rg` is not installed.
- 2026-04-23: Agent death/reversion/block-pressure slice first `./gradlew compileJava` completed successfully in 5 seconds.
- 2026-04-23: Agent death/reversion/block-pressure slice `./gradlew compileClientJava` completed successfully in 1 second with the existing Fabric renderer deprecation note.
- 2026-04-23: Agent death/reversion/block-pressure slice final `./gradlew compileJava` completed successfully in 885 ms.
- 2026-04-23: Agent death/reversion/block-pressure slice final `./gradlew build` completed successfully in 865 ms; Gradle reported existing deprecation warnings from build tooling/plugins.
- 2026-04-23: Agent death/reversion/block-pressure slice jar resource check confirmed the Agent lang file, custom damage type, block-placement mixin, and damage type class are packaged.
- 2026-04-18: Direct shared-thread fetch via `curl -sL` completed in about 1 second after sandbox network approval.
- 2026-04-18: Repository inspection commands completed in under 1 second each.
- 2026-04-18: `./gradlew compileJava` completed successfully in 9 seconds.
- 2026-04-18: Terminology grep and `git diff --check` completed in under 1 second each.
- 2026-04-18: First code slice `./gradlew compileJava` completed successfully in 1 second.
- 2026-04-18: First code slice `./gradlew compileClientJava` completed successfully in 780 ms with tasks up to date.
- 2026-04-18: Config loading slice `./gradlew compileJava` completed successfully in 881 ms.
- 2026-04-18: Config loading slice `./gradlew compileClientJava` completed successfully in 608 ms with tasks up to date.
- 2026-04-18: Identity data slice `./gradlew compileJava` completed successfully in 748 ms.
- 2026-04-18: Identity data slice `./gradlew compileClientJava` completed successfully in 598 ms with tasks up to date.
- 2026-04-18: Agent entity shell slice `./gradlew compileJava` completed successfully in 666 ms.
- 2026-04-18: Agent entity shell slice `./gradlew compileClientJava` completed successfully in 564 ms with tasks up to date.
- 2026-04-18: Transformation logic slice `./gradlew compileJava` completed successfully in 758 ms.
- 2026-04-18: Transformation logic slice `./gradlew compileClientJava` completed successfully in 557 ms with tasks up to date.
- 2026-04-18: Entity persistence slice `./gradlew compileJava` completed successfully in 662 ms.
- 2026-04-18: Entity persistence slice `./gradlew compileClientJava` completed successfully in 563 ms with tasks up to date.
- 2026-04-18: Outbreak manager slice first `./gradlew compileJava` run failed in 757 ms on a `ResourceKey` mapping mismatch; fixed by using `identifier()`.
- 2026-04-18: Outbreak manager slice `./gradlew compileJava` completed successfully in 709 ms.
- 2026-04-18: Outbreak manager slice `./gradlew compileClientJava` completed successfully in 516 ms with tasks up to date.
- 2026-04-18: Player-adjacent outbreak slice first `./gradlew compileJava` run failed in 706 ms on a missing double validation overload; fixed by adding `validatePositive(String, double)`.
- 2026-04-18: Player-adjacent outbreak slice `./gradlew compileJava` completed successfully in 676 ms.
- 2026-04-18: Player-adjacent outbreak slice `./gradlew compileClientJava` completed successfully in 535 ms with tasks up to date.
- 2026-04-18: Conversion manager slice `./gradlew compileJava` completed successfully in 708 ms.
- 2026-04-18: Conversion manager slice first `./gradlew compileClientJava` completed successfully in 816 ms.
- 2026-04-18: Placeholder Agent renderer `./gradlew compileJava` completed successfully in 491 ms with tasks up to date.
- 2026-04-18: Placeholder Agent renderer `./gradlew compileClientJava` completed successfully in 706 ms with a deprecation note from Fabric's entity renderer registry API.
- 2026-04-18: Interruptible transformation slice `./gradlew compileJava` completed successfully in 716 ms.
- 2026-04-18: Interruptible transformation slice `./gradlew compileClientJava` completed successfully in 517 ms with tasks up to date.
- 2026-04-18: Transformation presentation slice `./gradlew compileJava` completed successfully in 750 ms.
- 2026-04-18: Transformation presentation slice `./gradlew compileClientJava` completed successfully in 694 ms.
- 2026-04-18: Agent placeholder rendering slice `./gradlew compileClientJava` completed successfully in 702 ms with a deprecation note from Fabric's entity renderer registry API.
- 2026-04-18: Agent placeholder rendering slice `./gradlew compileJava` completed successfully in 468 ms with tasks up to date.
- 2026-04-18: Agent placeholder rendering slice first `./gradlew build` attempt failed because the sandbox blocked the Gradle wrapper cache lock file.
- 2026-04-18: Agent placeholder rendering slice escalated `./gradlew build` completed successfully in 708 ms; Gradle reported deprecations from build tooling/plugins.
- 2026-04-18: First `./gradlew runClient` check failed in 3 seconds because `EntityBreachDataMixin` injected into abstract `Entity#defineSynchedData`; fixed by moving synced presentation field definition to `LivingEntity#defineSynchedData`.
- 2026-04-18: Follow-up `./gradlew compileJava` completed successfully in 633 ms.
- 2026-04-18: Follow-up `./gradlew compileClientJava` completed successfully in 631 ms.
- 2026-04-18: Follow-up `./gradlew runClient` reached Minecraft render thread, created config, registered mod systems, initialized resources/audio, and was then terminated manually after verification.
- 2026-04-18: World-open validation found `Duplicate id value for 15` and `Invalid player data`; root cause was extending vanilla `SynchedEntityData` after subclass data IDs were already assigned.
- 2026-04-18: Replaced synced entity-data transformation presentation with a custom clientbound play packet and client-side presentation tracker.
- 2026-04-18: Packet presentation fix `./gradlew compileJava` completed successfully in 797 ms.
- 2026-04-18: Packet presentation fix `./gradlew compileClientJava` completed successfully in 867 ms with a deprecation note from Fabric's entity renderer registry API.
- 2026-04-18: Packet presentation fix `./gradlew runClient` opened world `T1`, logged in player entity id 5, ran gameplay, saved worlds, and exited successfully in 37 minutes 29 seconds.
- 2026-04-18: Debug command and spread tuning slice `./gradlew compileJava` completed successfully in 865 ms.
- 2026-04-18: Debug command and spread tuning slice `./gradlew compileClientJava` completed successfully in 739 ms with the existing Fabric renderer deprecation note.
- 2026-04-18: Debug command and spread tuning slice final `./gradlew compileJava` completed successfully in 769 ms; final `./gradlew compileClientJava` completed successfully in 491 ms with tasks up to date.
- 2026-04-18: Agent XP and asset guidance slice first `./gradlew compileJava` completed successfully in 810 ms; first `./gradlew compileClientJava` failed in 787 ms on an `Identifier` factory mapping mismatch, then passed in 720 ms after switching to `Identifier.fromNamespaceAndPath`.
- 2026-04-18: Agent XP and asset guidance slice final `./gradlew compileJava` completed successfully in 502 ms with tasks up to date; final `./gradlew compileClientJava` completed successfully in 517 ms with tasks up to date.
- 2026-04-19: Transformation sound and linger pressure slice `./gradlew compileJava` completed successfully in 5 seconds.
- 2026-04-19: Transformation sound and linger pressure slice `./gradlew compileClientJava` completed successfully in 1 second with the existing Fabric renderer deprecation note.
- 2026-04-19: Transformation sound and linger pressure slice `./gradlew build` completed successfully in 905 ms; jar resource check confirmed `sounds.json` and `transform.ogg` are packaged.
- 2026-04-19: Post-cleanup rerun `./gradlew compileJava` completed successfully in 824 ms; `./gradlew compileClientJava` completed successfully in 605 ms; `./gradlew build` completed successfully in 667 ms.
- 2026-04-19: Agent voice and breach notice slice `./gradlew compileJava` completed successfully in 2 seconds.
- 2026-04-19: Agent voice and breach notice slice `./gradlew compileClientJava` completed successfully in 1 second with the existing Fabric renderer deprecation note.
- 2026-04-19: Agent voice and breach notice slice `./gradlew build` completed successfully in 673 ms; jar resource check confirmed `voice.ogg` is packaged.
- 2026-04-19: Release README and tamed spread slice `./gradlew compileJava` completed successfully in 757 ms.
- 2026-04-19: Release README and tamed spread slice `./gradlew compileClientJava` completed successfully in 561 ms.
- 2026-04-19: Release README and tamed spread slice `./gradlew build` completed successfully in 524 ms; Gradle reported existing deprecation warnings.
- 2026-04-20: Transformation sync and violence slice `./gradlew compileJava` completed successfully in 8 seconds after waiting on a Loom cache lock.
- 2026-04-20: Parallel `./gradlew compileClientJava` was invalid while `compileJava` held Gradle/Loom state and failed with missing project classes; sequential rerun completed successfully in 1 second.
- 2026-04-20: Transformation sync and violence slice `./gradlew build` completed successfully in 863 ms; Gradle reported existing deprecation warnings.

## 2026-04-18 Agent Terminology and Gameplay Spec Update

- [x] Search docs for remaining old entity terminology.
- [x] Update terminology to prefer The Agent throughout docs.
- [x] Add difficulty-scaled initial conversion chance requirements.
- [x] Add delayed shaking transformation requirements and sound plan.
- [x] Add Agent player-priority targeting requirements.
- [x] Verify docs and record results.

## 2026-04-18 First Code Slice

- [x] Choose a low-risk first implementation step: config defaults and pure rule helpers.
- [x] Add Java config/default classes matching `SPECS.md`.
- [x] Add difficulty-scaled initial Agent chance calculation.
- [x] Wire initialization logging to report the active default chance.
- [x] Compile and record performance.

## 2026-04-18 Config Loading Slice

- [x] Add config file loader for `config/simulation-breach.json`.
- [x] Write default config when the file does not exist.
- [x] Fall back safely to defaults when config parsing or validation fails.
- [x] Wire Fabric initialization to use loaded config values.
- [x] Record Agent graphics planning notes.
- [x] Compile and record performance.

## 2026-04-18 Identity Data Slice

- [x] Add origin disposition, infection stage, and transformation state enums.
- [x] Add stable storage keys for persisted breach data.
- [x] Add immutable breach entity data model with validation.
- [x] Add map-based serialization/deserialization helpers for later NBT or Bedrock storage adapters.
- [x] Compile and record performance.

## 2026-04-18 Agent Entity Shell Slice

- [x] Add registered `simulation-breach:agent` entity type.
- [x] Add basic hostile Agent entity class with player-priority target goals.
- [x] Register default Agent attributes.
- [x] Defer custom renderer/skin to the client presentation slice.
- [x] Compile and record performance.

## 2026-04-18 Transformation Logic Slice

- [x] Add transformation progress model with elapsed, remaining, completion, and shake intensity.
- [x] Add transformation manager helpers to begin, inspect, complete, and cancel transformations.
- [x] Keep presentation hooks deterministic for later sound, particle, and renderer work.
- [x] Compile and record performance.

## 2026-04-18 Entity Persistence Slice

- [x] Add `ValueInput`/`ValueOutput` storage adapter for breach data.
- [x] Persist Agent entity breach data through save/load hooks.
- [x] Provide a default Agent breach identity for spawned Agents.
- [x] Compile and record performance.

## 2026-04-18 Outbreak Manager Slice

- [x] Add concrete config defaults for scheduled initial outbreak checks.
- [x] Register a server tick outbreak scheduler.
- [x] Sample eligible passive mobs with per-level scan and roll limits.
- [x] Apply difficulty-scaled initial Agent chance when selecting candidates.
- [x] Compile and record performance.

## 2026-04-18 Player-Adjacent Outbreak Slice

- [x] Add spec requirements for nearby-player and reachable-route natural outbreak eligibility.
- [x] Add spec requirements for player action outbreak pressure, with village and villager actions weighted highest.
- [x] Add config defaults for initial outbreak player search radius and reachable-route checks.
- [x] Require natural outbreak candidates to be near a non-spectator player with a navigable route by default.
- [x] Compile and record performance.

## 2026-04-18 Conversion Manager Slice

- [x] Add mixin-backed breach data storage for vanilla entities.
- [x] Add conversion manager support for beginning delayed Agent transformations.
- [x] Complete ready transformations by spawning an Agent, transferring core state, and discarding the source without drops or XP.
- [x] Enforce the local Agent cap before starting initial Agent transformations.
- [x] Wire initial outbreak candidate selections into delayed Agent transformations.
- [x] Add placeholder Agent renderer registration so converted Agents can render client-side.
- [x] Compile and record performance.

## 2026-04-18 Interruptible Transformation Slice

- [x] Add explicit spec requirement that killing a transforming mob prevents Agent spawn.
- [x] Cancel pending transformations when the source entity dies or is removed before completion.
- [x] Compile and record performance.

## 2026-04-18 Transformation Presentation Slice

- [x] Add config support for the placeholder Creeper transformation sound.
- [x] Play the placeholder Creeper priming sound when a transformation starts.
- [x] Sync remaining transformation ticks to clients.
- [x] Reuse the vanilla living-entity shake path for transforming mobs.
- [x] Compile and record performance.

## 2026-04-18 Agent Placeholder Rendering Slice

- [x] Add dedicated Agent render state.
- [x] Replace the plain zombie passthrough with a distinct tinted humanoid placeholder render.
- [x] Keep vanilla texture/model use until final Agent art is available.
- [x] Run compile and build verification.

## 2026-04-18 Run Client Startup Fix

- [x] Reproduce the `runClient` startup failure.
- [x] Identify invalid mixin injection into abstract `Entity#defineSynchedData`.
- [x] Move synced transformation presentation fields to a concrete `LivingEntity` mixin.
- [x] Verify compile tasks.
- [x] Verify `runClient` reaches Minecraft startup and mod initialization.

## 2026-04-18 Invalid Player Data Fix

- [x] Reproduce and diagnose `Duplicate id value for 15` / `Invalid player data` when opening world `T1`.
- [x] Remove custom `SynchedEntityData` fields from vanilla entity hierarchy.
- [x] Add a clientbound transformation presentation payload.
- [x] Add client-side presentation tracker for transformation shake.
- [x] Send start/stop transformation presentation packets from the conversion manager.
- [x] Verify compile tasks.
- [x] Verify world `T1` opens, saves, and exits cleanly through `runClient`.

## 2026-04-18 Debug Command and Spread Tuning Slice

- [x] Add spec updates for higher natural outbreak throughput, longer transformations, Agent conversion sweeps, and deterministic debug testing.
- [x] Add operator-only `/simulationbreach transform_nearest [radius]` command.
- [x] Make Agents sweep all nearby eligible non-player mobs within the detour radius on a cooldown.
- [x] Route version 1 Agent spread attempts through delayed Agent transformation until corrupted-hostile conversion exists.
- [x] Raise transformation duration and spread/outbreak defaults for testability.
- [x] Compile and record performance.

## 2026-04-18 Agent XP and Asset Guidance Slice

- [x] Add configurable Agent XP reward.
- [x] Make the Agent renderer use `assets/simulation-breach/textures/entity/agent/agent.png` when present, with the existing zombie placeholder fallback.
- [x] Document Agent texture and transformation sound asset paths and formats.
- [x] Update specs for Agent reward tuning.
- [x] Compile and record performance.

## 2026-04-19 Transformation Sound and Linger Pressure Slice

- [x] Register and play `entity/agent/transform.ogg` for Agent transformations.
- [x] Stop applying placeholder tint to the authored Agent texture.
- [x] Add configurable outbreak pressure when players linger in one area.
- [x] Update specs and docs for the new sound and linger-pressure behavior.
- [x] Compile and record performance.

## 2026-04-19 Agent Voice and Breach Notice Slice

- [x] Register and play `entity/agent/voice.ogg` as the Agent ambient voice.
- [x] Add a computer-styled chat notice for natural random Agent assignments.
- [x] Add config/spec/docs coverage for the natural outbreak notice.
- [x] Compile, build, and record performance.

## 2026-04-19 Release README and Tamed Spread Slice

- [x] Update README status and gameplay wording for release readiness.
- [x] Add Modrinth/CurseForge-ready description copy.
- [x] Keep tamed animals excluded from random natural outbreaks but allow Agent spread conversion.
- [x] Update specs for the tamed-animal rule distinction.
- [x] Compile, build, and record performance.

## 2026-04-20 Transformation Sync and Violence Slice

- [x] Review reported transformation presentation bugs and current packet/sound flow.
- [x] Update specs for more violent, unnatural transformation motion.

## 2026-04-23 Agent Death, Reversion, and Block Pressure Slice

- [x] Inspect Agent entity, conversion, outbreak, config, and resource wiring.
- [x] Add player-facing Agent name and custom Agent player death message.
- [x] Revert Agents to their original mob after their tracked player dies when no other players are nearby.
- [x] Add local block-placement/mining outbreak pressure.
- [x] Update specs for the new behavior.
- [x] Compile, build, and record performance.
- [x] Start transformation sound from the same client presentation event that starts shaking.
- [x] Replace frozen-only shake with stronger client-side position and rotation offsets.
- [x] Compile, build, and record performance.

## Implementation Roadmap

- [x] Foundation: config defaults, validation, promotion mode, difficulty model, and pure outbreak chance rules.
- [x] Config loading: load/save a user-editable config file and fall back safely to defaults.
- [x] Identity data: define persisted origin disposition, original entity type, infection stage, and transformation state.
- [x] Agent entity shell: register the Agent entity type with basic hostile behavior.
- [x] Transformation system: add delayed transformation state, Creeper-like shaking progress, completion, and cancellation helpers.
- [x] Outbreak manager: schedule bounded natural outbreak checks without scanning every entity every tick.
- [x] Natural outbreak eligibility: require nearby-player context and reachable-route checks before candidate selection.
- [x] Player linger outbreak pressure: raise local natural outbreak chance when a player remains in one area.
- [ ] Player action outbreak pressure: raise local natural outbreak chance after specific player actions, especially village and villager changes.
- [x] Conversion manager: replace entities, transfer breach identity data, prevent duplicate drops/XP, and enforce cooldowns/caps.
- [x] Natural outbreak tamed animal protection: owned tamed animals are excluded from random chance selection.
- [x] Agent spread tamed animal risk: owned tamed animals can still be converted by nearby Agents.
- [x] Interruptible transformations: killing the source before completion prevents Agent spawn.
- [x] Agent targeting: prioritize nearby players while allowing short bounded conversion detours.
- [x] Transformation presentation: synced shake state and original Agent transformation sound with Creeper fallback.
- [x] Agent audio: ambient voice.
- [ ] Agent audio: additional action sounds.
- [x] Agent placeholder rendering: distinct tinted humanoid render using vanilla assets.
- [x] Agent reward tuning: configurable XP reward for killing Agents.
- [ ] Client presentation: final renderer, model/texture, particles, and any additional synced visual state.
- [ ] Balancing and verification: tune defaults, log performance, add tests or harnesses around pure rule code, and verify in-game behavior.

## Agent Graphics Notes

- The Agent should start as a humanoid skin/texture so players immediately read it as an intentional entity rather than a random vanilla mob.
- Version 1 can use a placeholder humanoid model and texture while mechanics are proven.
- The final skin should feel systematic and uncanny without direct references to protected characters or franchises.
- The transformation visual should be a separate concern from the Agent skin: shaking, sound, and possible particles sell the conversion while the Agent texture sells the completed identity.
- Later asset tasks should include Agent texture, transformation particles, original transformation sound, and optional corrupted mob visual treatment.
