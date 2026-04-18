# Implementation Plan

## 2026-04-18 Documentation Baseline

- [x] Read project instructions and repository metadata.
- [x] Retrieve and extract the shared design thread.
- [x] Inspect current README, Gradle metadata, Fabric mod metadata, and source layout.
- [x] Create `SPECS.md` from the shared design thread with Java and Bedrock edition guidance.
- [x] Update `README.md` with user-facing information for the Java Fabric mod.
- [x] Run a lightweight verification pass and record results.

## Performance Log

- 2026-04-18: Direct shared-thread fetch via `curl -sL` completed in about 1 second after sandbox network approval.
- 2026-04-18: Repository inspection commands completed in under 1 second each.
- 2026-04-18: `./gradlew compileJava` completed successfully in 9 seconds.
- 2026-04-18: Terminology grep and `git diff --check` completed in under 1 second each.

## 2026-04-18 Agent Terminology and Gameplay Spec Update

- [x] Search docs for remaining old entity terminology.
- [x] Update terminology to prefer The Agent throughout docs.
- [x] Add difficulty-scaled initial conversion chance requirements.
- [x] Add delayed shaking transformation requirements and sound plan.
- [x] Add Agent player-priority targeting requirements.
- [x] Verify docs and record results.
