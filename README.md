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
- Tamed animals are excluded from random natural outbreak selection by default.
- Agents prioritize nearby players while making short conversion detours.
- Agents can convert nearby non-player mobs during cooldown-limited sweeps, including tamed animals they pass near.
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
- Villager and tamed-animal natural outbreak exclusions.
- Debug and performance logging.

## Storefront Description

Short summary:

```text
An eerie Fabric outbreak mod where the simulation assigns hostile Agents to hunt players and rewrite nearby mobs.
```

Long description:

```text
Simulation Breach turns survival into a quiet systems failure. Ordinary mobs can become Agents, hostile entities assigned by the simulation to correct the player-shaped breach. Agents pursue players, make short detours to convert nearby mobs, and can escalate a calm world into a spreading outbreak if ignored.

Conversions are not instant. A marked mob shakes, plays a custom transformation sound, and can still be killed before the Agent appears. Natural outbreaks are rare, difficulty-scaled, player-adjacent, and bounded by local caps. Staying in one area gradually increases local outbreak pressure, while tamed animals are protected from random outbreak selection but not from an Agent that gets too close.

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
