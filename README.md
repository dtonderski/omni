# Omni

Omni is a modular Android app composed of independent mini-apps ("features") like goal tracking, workouts, and personal analytics. The project uses a vertical-slice architecture so each feature owns its UI, data, and logic while sharing common primitives via `core`.

## Goals
- Build a flexible personal "everything app" that can grow over time.
- Practice rapid iteration (vibe-coding) and design.
- Keep features decoupled so they can evolve independently.

## Tech Stack
- Jetpack Compose (UI)
- Room (local data)
- Hilt (DI)
- Gradle (build)
- Navigation 3 (navigation)

## Project Structure
- `app/` launcher, app manifest, app-level wiring
- `core/` shared UI, theming, navigation, scaffolding
- `feature/metrics/` metrics + objectives feature (first vertical slice)

## Build & Run
- `./gradlew :app:assembleDebug` build debug APK
- `./gradlew :app:installDebug` install on device/emulator
- `./gradlew :app:testDebugUnitTest` unit tests

## Feature Docs
Each feature can include a focused README:
- `feature/*/README.md`

## Notes
- This app is intended for personal use (no Play Store target yet).
- Data models are designed to support tiered goals, entries, and milestones.
