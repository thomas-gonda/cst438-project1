# CST438 Project 1 — Alcohol Tracker

An Android app (Kotlin + Jetpack Compose) for looking up, saving, and reviewing alcoholic beverages. Users sign up / log in, search for a product, and see the results they've searched saved locally in a timeline.

## What it does

- **Login / Sign-up** — accounts are stored in a local Room database (seeded on first launch from `user_seed.json`).
- **Search** — searches an alcohol product database as you type, with local autocomplete suggestions pulled from previously-seen products.
- **Remote lookup** — "Search API" queries the [Open Food Facts](https://world.openfoodfacts.org/) API first; if that returns no results (or fails), it falls back to the [LCBO](https://api.lcbo.dev/) GraphQL API. Both are public and don't require an API key.
- **Product details** — tap a result to see name, brand, category, country, size, and ABV.
- **Timeline** — every product you look up is saved to a local database and shown in a per-user history/timeline screen.

Everything is persisted locally with Room (SQLite); there is no backend server for this app.

## Requirements

- **Android Studio** (a recent version — e.g. Ladybug/2024.x or newer — that supports AGP 9.3.2 and compileSdk 37)
- **JDK 25** — the project's Gradle daemon config (`gradle/gradle-daemon-jvm.properties`) requests toolchain version 25. Android Studio's bundled JDK usually satisfies this, or Gradle will auto-provision it via [Foojay](https://foojay.io) if you have internet access.
- An Android device or emulator running **API 24 (Android 7.0)** or higher
- Internet access on the device/emulator (required for the "Search API" feature; the app requests the `INTERNET` permission)

No API keys, secrets, or `local.properties` entries are needed — the app talks directly to the public Open Food Facts and LCBO endpoints.

## Getting the code

```bash
git clone https://github.com/thomas-gonda/cst438-project1.git
cd cst438-project1
```

## Building

### Option A — Android Studio (recommended)

1. Open Android Studio → **Open** → select the cloned `cst438-project1` folder.
2. Let Gradle sync (it will download AGP, Kotlin, Compose, and Room dependencies automatically).
3. Build → **Make Project**, or just click **Run**.

### Option B — Command line

From the repo root:

```bash
# macOS/Linux
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

The debug APK will be output to `app/build/outputs/apk/debug/app-debug.apk`.

## Running

### From Android Studio
Select a device/emulator from the target dropdown and click **Run ▶**. The app launches on the **Login** screen.

### From the command line

```bash
# Install the debug build to a connected device/emulator
./gradlew installDebug

# Or install the APK directly
adb install app/build/outputs/apk/debug/app-debug.apk
```

On first launch, the app seeds its local database with sample users and alcohol data from `app/src/main/assets/user_seed.json` and `alcohol_seed.json`.

## Running the tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (needs a connected device/emulator)
./gradlew connectedAndroidTest
```

## Project structure

```
app/src/main/java/com/example/cst438_project1/
├── MainActivity.kt        # Alcohol search screen + API search
├── LoginPage.kt            # Login screen (launcher activity)
├── SignUpPage.kt           # Account creation
├── LandingPage.kt          # Post-login landing/menu
├── TimelinePage.kt         # Per-user search history
├── AlcoholDetails.kt       # Product detail screen
├── data/local/              # Room entities, DAOs, database, seeding
├── data/model/              # API response models
├── data/remote/AlcoholApi.kt  # Open Food Facts + LCBO lookups
└── ui/theme/                 # Compose theming
```

## Notes

- Code quality is checked with **Detekt** and **PMD** (`./gradlew detekt pmd` to run locally); configs live under `config/`.
- This project has no README committed to the repo at the time of writing — see the repo's [commit history](https://github.com/thomas-gonda/cst438-project1/commits/main) for the latest changes.
