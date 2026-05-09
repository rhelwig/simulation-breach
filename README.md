# Simulation Breach

Simulation Breach is a Minecraft Java Edition Fabric mod where the world treats players like breaches in the simulation. The system answers by assigning Agents: hostile entities that can emerge from ordinary mobs, pursue players, and recruit more agents from other mobs.

## Project Status

Simulation Breach is at a releasable version 1 state for Java Edition. Core outbreak, Agent, transformation, audio, rendering, config, and debug-command systems are implemented and build successfully.

The mod is still expected to evolve. Later versions may add stronger corrupted-mob variants, more visuals, more audio, and additional outbreak triggers. See [SPECS.md](SPECS.md) for the broader design.

## Gameplay

Implemented in the current Java build:

- Rare natural outbreaks can turn eligible non-hostile mobs into The Agent.
- Initial outbreak chance scales by world difficulty.
- Natural outbreaks require nearby player context and, by default, a reachable route to a player.
- Lingering in one area gradually raises local natural outbreak pressure within configurable limits.
- Name-tagged mobs can be excluded from both natural outbreaks and Agent conversion.
- Tamed animals are excluded from random natural outbreak selection by default, with a separate config toggle for Agent spread.
- Agents prioritize nearby players while making short conversion detours.
- Agents can convert nearby non-player mobs during cooldown-limited sweeps unless config excludes that category.
- Conversions use a visible delayed transformation with shaking and a custom transformation sound.
- Killing a transforming mob before the delay completes prevents the Agent from spawning.
- Completed natural outbreaks send a computer-styled system notice.
- Agents have a custom skin, ambient voice, configurable XP reward, and bounded local spread.
- A debug command, `/simulationbreach transform_nearest [radius]`, can force a nearby mob into Agent transformation for testing.
- Converted entities persist breach identity data across replacement and saves.

Planned or future work:

- Dedicated corrupted-hostile variants instead of routing version 1 spread through Agent transformation.
- Additional particles or transformation visuals.
- More Agent action sounds.
- Player-action outbreak pressure, especially around village and villager disruption.
- Optional config screen.
- Bedrock behavior/resource pack implementation.

## Requirements

- Minecraft Java Edition `26.1.2`
- Fabric Loader `0.19.2` or newer
- Fabric API `0.146.1+26.1.2`
- Java `25` or newer

## Installation

1. Install the matching Minecraft Java Edition version.
2. Install Fabric Loader.
3. Install Fabric API.
4. Place the Simulation Breach jar in your Minecraft `mods` folder.
5. Start the game with the Fabric profile.

Back up important worlds before testing outbreak mods. Simulation Breach is designed to change mob populations.

## Configuration

On first launch, the mod writes:

```text
config/simulation-breach.json
```

The config includes:

- Initial Agent outbreak chance.
- Difficulty multipliers.
- Passive, hostile, and corrupted conversion chances.
- Conversion cooldown and local Agent cap.
- Transformation duration and sound toggles.
- Agent XP reward.
- Player linger outbreak pressure.
- Natural outbreak chat notice toggle.
- Passive-origin promotion mode.
- Villager, tamed-animal, and named-entity conversion exclusions.
- Debug and performance logging.

### Details

Unless noted otherwise, boolean settings use `true` to enable the behavior and `false` to disable it.

| Key | Default | Meaning |
| --- | ---: | --- |
| `initialPassiveAgentChance` | `0.001` | Base chance for an eligible passive mob to start a natural outbreak before difficulty and pressure multipliers are applied. |
| `peacefulInitialAgentChanceMultiplier` | `0.10` | Outbreak multiplier used in Peaceful difficulty. |
| `easyInitialAgentChanceMultiplier` | `0.50` | Outbreak multiplier used in Easy difficulty. |
| `normalInitialAgentChanceMultiplier` | `1.00` | Outbreak multiplier used in Normal difficulty. |
| `hardInitialAgentChanceMultiplier` | `2.00` | Outbreak multiplier used in Hard difficulty. |
| `hardcoreInitialAgentChanceMultiplier` | `3.00` | Outbreak multiplier used in Hardcore worlds. |
| `agentConvertPassiveChance` | `1.0` | Chance that an Agent converts an eligible non-hostile mob when its spread attempt hits. |
| `agentConvertHostileToAgentChance` | `0.75` | Chance that an Agent converts an originally hostile mob into another Agent. |
| `agentConvertCorruptedPassiveToAgentChance` | `0.75` | Chance that an already-corrupted passive-origin mob gets promoted into an Agent when promotion is allowed. |
| `agentConversionCooldownTicks` | `20` | Minimum delay between an Agent's conversion sweeps. Lower values make spread more aggressive. |
| `transformationDurationTicks` | `180` | Time a mob spends transforming before the replacement Agent appears. |
| `agentExperienceReward` | `12` | XP dropped when an Agent dies. |
| `agentConversionDetourRadius` | `4` | Radius around an Agent where it can briefly divert to convert nearby mobs while chasing players. |
| `maxAgentsPerChunk` | `8` | Soft local cap on how many Agents can occupy the same area. |
| `enableInitialOutbreaks` | `true` | Enables or disables random natural outbreak starts entirely. |
| `outbreakCheckIntervalTicks` | `200` | Delay between scheduled natural outbreak checks. |
| `outbreakScanLimitPerLevel` | `768` | Maximum loaded entities inspected per level during one natural outbreak pass. |
| `outbreakEligibleRollsPerLevel` | `64` | Maximum eligible mobs that actually get outbreak chance rolls per level during one pass. |
| `initialOutbreakPlayerSearchRadius` | `48.0` | Maximum distance from a candidate mob to a non-spectator player for natural outbreak eligibility. |
| `initialOutbreakRequiresReachablePlayer` | `true` | Requires natural outbreak candidates to have a navigable route to a nearby player. |
| `enableAgentTransformSound` | `true` | Plays the custom Agent transformation sound during conversions. |
| `enablePlaceholderCreeperTransformSound` | `true` | Uses the Creeper priming sound as a fallback when the custom transform sound is disabled. |
| `enablePlayerLingerOutbreakPressure` | `true` | Enables extra local outbreak pressure when a player stays in one area for too long. |
| `playerLingerPressureRadius` | `48.0` | Radius covered by the linger-pressure zone around the player's anchor point. |
| `playerLingerPressureGraceTicks` | `1200` | Time a player can stay in one area before linger pressure starts ramping up. |
| `playerLingerPressureRampTicks` | `4800` | Time it takes for linger pressure to climb from the grace threshold to its maximum multiplier. |
| `maxPlayerLingerPressureMultiplier` | `4.0` | Maximum natural outbreak multiplier caused by player lingering. |
| `enablePlayerBlockChangeOutbreakPressure` | `true` | Enables outbreak pressure from player block placement and mining. |
| `playerBlockChangePressureRadius` | `48.0` | Radius around the block-change anchor affected by block-change pressure. |
| `playerBlockChangePressureDurationTicks` | `12000` | How long block-change pressure stays active after the last tracked block change. |
| `playerBlockChangesPerPressureStep` | `100` | Number of block changes required for one pressure increase step. |
| `playerBlockChangePressureStepMultiplier` | `0.01` | Relative outbreak multiplier added for each completed block-change pressure step. |
| `maxPlayerBlockChangePressureMultiplier` | `2.0` | Maximum natural outbreak multiplier caused by block-change pressure. |
| `agentDespawnNearbyPlayerRadius` | `48.0` | Radius used when deciding whether an Agent should remain after its tracked player dies. |
| `enableNaturalOutbreakChatNotice` | `true` | Sends the system-style breach notice when a natural outbreak completes. |
| `passivePromotionMode` | `PROMOTED_CORRUPTION` | Controls whether passive-origin mobs can ever become full Agents after being corrupted. See choices below. |
| `excludeVillagers` | `false` | Makes villagers immune to both natural outbreaks and Agent-driven conversion when enabled. |
| `excludeTamedAnimals` | `true` | Keeps owned tamed animals out of random natural outbreak selection when enabled. |
| `excludeTamedAnimalsFromAgentSpread` | `false` | Makes owned tamed animals immune to direct Agent spread when enabled. |
| `excludeNamedEntities` | `true` | Makes mobs with custom names immune to both natural outbreaks and Agent-driven conversion when enabled. |
| `debugLogging` | `false` | Enables extra logging around outbreak checks, conversions, and performance. |

`passivePromotionMode` choices:

- `PROMOTED_CORRUPTION`: corrupted passive-origin mobs can later become full Agents if the promotion chance succeeds.
- `STRICT_ORIGIN`: passive-origin mobs can be corrupted, but they can never be promoted into full Agents through ordinary Agent spread.

## Storefront Description

Short summary:

```text
An eerie Fabric outbreak mod where the simulation assigns hostile Agents to hunt players and rewrite nearby mobs.
```

Long description:

```text
Simulation Breach turns survival into a quiet systems failure. Ordinary mobs can become Agents, hostile entities assigned by the simulation to correct the player-shaped breach. Agents pursue players, make short detours to convert nearby mobs, and can escalate a calm world into a spreading outbreak if ignored.

Conversions are not instant. A marked mob shakes, plays a custom transformation sound, and can still be killed before the Agent appears. Natural outbreaks are rare, difficulty-scaled, player-adjacent, and bounded by local caps. Staying in one area gradually increases local outbreak pressure, while villagers, named mobs, and tamed animals can all be protected through config, including a separate toggle for tamed-animal Agent spread.

Version 1 includes The Agent, delayed transformations, custom Agent texture and voice, custom transformation audio, configurable spread rules, XP rewards, natural outbreak alerts, persistent breach data, and an operator debug command for testing.
```

Feature bullets:

```text
- Rare player-adjacent natural outbreaks
- Hostile Agents that prioritize players
- Delayed, interruptible transformations
- Custom Agent texture, voice, and transform sound
- Difficulty-scaled and configurable spread
- Local outbreak pressure when players linger
- Natural outbreak system alerts
- Debug command for testing transformations
```

## Assets

Agent texture:

```text
src/main/resources/assets/simulation-breach/textures/entity/agent/agent.png
```

Transformation sound:

```text
src/main/resources/assets/simulation-breach/sounds/entity/agent/transform.ogg
```

Agent ambient voice:

```text
src/main/resources/assets/simulation-breach/sounds/entity/agent/voice.ogg
```

Use mono Ogg Vorbis for positional entity sounds.

## Bedrock Edition

This repository is for the Java Fabric mod. The specification is written so the same gameplay rules can later be implemented for Bedrock Edition through a behavior pack, resource pack, and Script API where needed. No Bedrock pack is included right now.

## Building From Source

Use the Gradle wrapper:

```bash
./gradlew build
```

Build outputs are written to `build/libs`.

## Development Notes

- Specifications live in [SPECS.md](SPECS.md).
- Implementation steps are tracked in [IMPLEMENTATION-PLAN.md](IMPLEMENTATION-PLAN.md).
- Issues should stay concise and action-oriented.
- Performance should be logged as outbreak and conversion systems are implemented.

## License

This project uses the `CC0-1.0` license declared in the Fabric mod metadata.
