# Repository Guidelines

## Project Structure & Module Organization
- `app/` is the launcher module (MainActivity, app manifest, and app-level resources).
- `core/` holds shared UI, theming, and navigation primitives used across features.
- `feature/metrics/` is the first vertical slice feature module (metrics UI and models).
- `feature/*/README.md` documents feature-specific context and expectations.
- Source sets follow standard Android layout: `src/main`, `src/test` (unit), and `src/androidTest` (instrumented).

## Build, Test, and Development Commands
- `./gradlew :app:assembleDebug` (or `gradlew.bat :app:assembleDebug` on Windows) builds a debug APK.
- `./gradlew :app:installDebug` installs the debug build on a connected device/emulator.
- `./gradlew :app:testDebugUnitTest` runs JVM unit tests.
- `./gradlew :app:connectedDebugAndroidTest` runs instrumented tests on a device/emulator.

## Coding Style & Naming Conventions
- Kotlin + Jetpack Compose are the default; keep code Kotlin-first.
- Use 4-space indentation and standard Kotlin/Android naming (PascalCase types, camelCase functions/vars).
- Compose files should keep top-level composables near the top and preview helpers at the bottom.
- No formatter/linter is configured in the repo; follow existing patterns in `core/ui` and feature modules.

## Navigation
- The app uses Navigation 3 (Compose-first). Keep destinations and stack management in `core/` and expose feature entry points from `feature/*` modules.

## Testing Guidelines
- Unit tests live in `src/test` and use JUnit 4.
- Instrumented tests live in `src/androidTest` and use AndroidX test + Espresso.
- Compose UI tests can use `androidx.compose.ui.test` in `androidTest`.
- Name tests descriptively (e.g., `MetricsScreenTest`, `MetricDataTest`).

## Commit & Pull Request Guidelines
- Commit history follows Conventional Commits (e.g., `feat:`, `refactor:`, `build:`). Keep using that style.
- PRs should include a clear description, the motivation, and screenshots for UI changes.
- Link related issues or design notes when applicable.

## Configuration Notes
- Local SDK settings belong in `local.properties`; avoid committing machine-specific paths.
- This project targets Android SDK 36 and Java 17; keep module configs aligned.

## Maintenance
- Update this guide when module structure, build/test commands, or conventions change.
