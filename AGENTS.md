# Repository Guidelines

## Project Structure & Module Organization
The root Gradle project defines a single Android `:app` module (`app/build.gradle.kts`) that hosts the Compose UI, data layer, and navigation logic. UI screens live under `app/src/main/java/com/example/teamproject/ui`, stateful logic is in `viewmodel`, while repositories, Retrofit services, and models sit in `data/**`. Resources are in `app/src/main/res`, instrumentation specs inside `app/src/androidTest`, and JVM unit tests in `app/src/test`.

## Build, Test, and Development Commands
Use `./gradlew assembleDebug` to produce a debug APK and surface Kotlin/Compose compilation issues. Run `./gradlew testDebugUnitTest` for JVM tests, and `./gradlew connectedAndroidTest` on an attached emulator/device for Espresso + Compose UI suites. `./gradlew lint` catches style issues and missing translations before review. During local iteration, `./gradlew :app:installDebug` deploys the latest build to a device running API 24+.

## Coding Style & Naming Conventions
Kotlin files follow Jetpack Compose guidance: four-space indentation, trailing commas in multiline argument lists, and immutable `val` defaults. Screens and composables use `PascalCase` (e.g., `WaterTrackingScreen`), state holders end with `ViewModel`, and repositories use the `{Domain}Repository` suffix. Keep navigation destinations centralized in `navigation/NavGraph.kt`; extend sealed route definitions rather than hardcoding strings. Use Android Studio’s “Reformat Code” with Kotlin defaults and rely on `./gradlew lint` to flag styling regressions.

## Testing Guidelines
Favor small, deterministic tests: place business-logic specs in `app/src/test` using JUnit4, naming files `{Class}Test`. UI flows that touch Compose or navigation belong in `app/src/androidTest` and should wrap interactions with `createAndroidComposeRule`. Maintain coverage of ViewModel reducers and Retrofit mappers before adding new endpoints. When networking logic changes (e.g., `DrinkRepository`), provide a fake API layer to exercise success and failure paths.

## Commit & Pull Request Guidelines
Follow the existing Conventional Commit style (`feat:`, `fix:`, `chore:`) observed in `git log`. Each commit should focus on one deliverable and include English summaries even if in-app text is localized. Pull requests need: summary of changes, testing evidence (command output or screenshots for UI), linked issue or task reference, and callouts for any new permissions (e.g., network scopes in `AndroidManifest.xml`). Tag reviewers familiar with the touched layer (UI, data, or infra) and ensure CI (Gradle test + lint) is green before requesting merge.

## Configuration & Security Tips
Secrets such as auth tokens are managed via `TokenManager`; never commit plaintext credentials. Retrofit’s base URL (`RetrofitClient.BASE_URL`) targets `http://drinkflow.p-e.kr/api/`; update via build configs instead of editing the constant directly.
