# Simulation Breach

Simulation Breach is a Minecraft Java Edition Fabric mod about a rare breach that rewrites mob identity. The target gameplay centers on The Agent, a spreading entity that can emerge from ordinary non-hostile mobs, corrupt other mobs, and escalate into a world-level outbreak if left unchecked.

## Project Status

This project is in early development. The repository currently contains the Fabric mod scaffold, project metadata, and the gameplay specification. The full outbreak mechanics described here are the target design and may not be available in a playable release yet.

See [SPECS.md](SPECS.md) for the authoritative gameplay and cross-edition design.

## Planned Gameplay

- Non-hostile mobs can become The Agent, with initial outbreak chance scaling by world difficulty.
- Peaceful worlds have the lowest initial conversion chance, and Hardcore worlds have the highest where Hardcore is available.
- Agents spread through successful melee hits, not simple collision.
- Passive mobs become corrupted hostile mobs first.
- Originally hostile mobs can become additional Agents.
- Corrupted passive-origin mobs follow a configurable promotion rule.
- Transforming mobs shake for a short configurable delay before replacement. Version 1 may reuse the Creeper priming sound as a placeholder.
- Agents prioritize nearby players and move to attack them, with only short detours to convert nearby mobs.
- Spread is controlled by configurable chances, cooldowns, and local caps.
- Converted mobs remember their original entity type and infection stage.

## Java Edition Requirements

Current project metadata targets:

- Minecraft Java Edition `26.1.2`
- Fabric Loader `0.19.2` or newer
- Fabric API `0.146.1+26.1.2`
- Java `25` or newer

These values come from the current Gradle and Fabric metadata and may change as the mod matures.

## Installation

When a release jar is available:

1. Install the matching Minecraft Java Edition version.
2. Install Fabric Loader.
3. Install Fabric API.
4. Place the Simulation Breach jar in your Minecraft `mods` folder.
5. Start the game with the Fabric profile.

Back up important worlds before testing early builds. Outbreak mechanics are intended to change mob populations.

## Configuration

The planned configuration includes:

- Initial Agent outbreak chance.
- Difficulty multipliers for initial outbreak chance.
- Passive, hostile, and corrupted conversion chances.
- Conversion cooldown.
- Transformation duration.
- Maximum Agents in a local area.
- Passive-origin promotion mode.
- Tamed animal exclusions.
- Debug and performance logging.

The default design favors a rare start, visible escalation, and bounded spread. Details are tracked in [SPECS.md](SPECS.md).

## Bedrock Edition

This repository is for the Java Fabric mod. The specification is written so the same gameplay rules can later be implemented for Bedrock Edition through a behavior pack, resource pack, and Script API where needed. No Bedrock pack is included in this repository right now.

## Building From Source

Use the Gradle wrapper:

```bash
./gradlew build
```

Build outputs are written to `build/libs`.

For IDE setup, see the Fabric documentation:

<https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up>

## Development Notes

- Specifications live in [SPECS.md](SPECS.md).
- Implementation steps are tracked in [IMPLEMENTATION-PLAN.md](IMPLEMENTATION-PLAN.md).
- Issues should stay concise and action-oriented.
- Performance should be logged as outbreak and conversion systems are implemented.

## License

This project currently uses the `CC0-1.0` license declared in the Fabric mod metadata.
