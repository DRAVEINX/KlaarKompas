# KlaarKompas for Android

**Kleine stappen. Echte vooruitgang.**

**Source version: 0.2.0**

KlaarKompas is een offline-first Android starter app with an independent phone variant and tablet variant. It helps people find a practical guide, follow one step at a time, listen to instructions, save favourites, create their own guides and resume progress.

> **Project status:** first MVP source package. The source and CI workflow are prepared; no build has been run in this environment because Android SDK/Gradle dependencies are not installed and outbound downloads are unavailable. The first GitHub Actions run must pass before the APK/AAB artifacts can be called verified.

## Included in this starter build

- 33 bundled Dutch-language guides across five subject categories, available offline.
- Search across guide titles, summaries, categories and tags.
- Guided one-step-at-a-time reading with progress, previous/next, completion and an “I’m stuck” helper with three alternatives.
- Create up to 20-step personal guides with a title, category and estimated time; personal guides are saved locally and included in search and “Mijn gidsen”.
- URL-safe Base64 persistence for personal guides protects punctuation and multiline instructions from corrupting saved records.
- Text-to-speech for a guide step, TalkBack-friendly labelled controls, and a larger-text setting.
- Local favourites and progress storage through Android DataStore; no login or network permission is requested.
- Adaptive layouts: compact bottom navigation on phones and a navigation sidebar / wider guided layout on larger screens.
- Separate `phone` and `tablet` product flavors.
- Unit tests plus GitHub Actions tasks for both phone and tablet debug builds, release APKs, and release AABs.

## Open and run locally

1. Install Android Studio, JDK 17 and Android SDK Platform 36 / Build Tools 36.0.0.
2. Extract this folder, then open the `KlaarKompas` project directory in Android Studio.
3. Let Gradle sync and install the Android SDK packages if prompted.
4. Choose a phone or tablet run configuration (or run the commands below).

On Windows PowerShell:

```powershell
.\gradlew.bat testPhoneDebugUnitTest testTabletDebugUnitTest
.\gradlew.bat assemblePhoneDebug assembleTabletDebug
```

On macOS/Linux:

```bash
./gradlew testPhoneDebugUnitTest testTabletDebugUnitTest
./gradlew assemblePhoneDebug assembleTabletDebug
```

The bootstrap scripts download the pinned official Gradle 8.13 distribution on first use. The first build also needs network access to resolve Android Gradle Plugin and Maven dependencies.

## Build outputs

- Phone debug APK: `app/build/outputs/apk/phone/debug/`
- Tablet debug APK: `app/build/outputs/apk/tablet/debug/`
- Phone release APK: `app/build/outputs/apk/phone/release/`
- Tablet release APK: `app/build/outputs/apk/tablet/release/`
- Phone release AAB: `app/build/outputs/bundle/phoneRelease/`
- Tablet release AAB: `app/build/outputs/bundle/tabletRelease/`

Release outputs produced by the current workflow are **not signed for Google Play**. Configure a controlled signing process and Play App Signing before uploading. Do not commit a keystore, passwords, or signing keys to this repository.

## GitHub Actions

The workflow in `.github/workflows/android.yml` first runs `python tools/validate_project.py`, a dependency-free preflight that checks XML, the phone/tablet variants, guide-library IDs, personal-guide storage, privacy defaults, Gradle pinning and expected CI tasks. It then runs the Android unit tests and lint tasks and builds both flavors. Open **Actions → KlaarKompas Android CI**. A successful run provides the phone/tablet build artifacts; if no run appears, check the Actions tab and the workflow file under `.github/workflows/android.yml`. Download the artifact named `klaarkompas-android-<run number>` from a successful run.

## App identifiers

- Phone: `nl.draveinx.klaarkompas.phone`
- Tablet: `nl.draveinx.klaarkompas.tablet`

These IDs intentionally allow side-by-side installations. If the long-term product should be one Play Store listing for both screen sizes, agree on that release strategy before the first public listing; application IDs should not be changed after publication without a migration plan.

## Privacy and content quality

This first version contains bundled content and saves only preferences/progress on the device. `INTERNET` permission is not declared and Android backup is disabled. The starter guides are general practical prompts, not professional legal, medical, financial, electrical or other safety-critical advice. Important instructions should be reviewed by a suitable human expert before public distribution.

## Product docs

- [Product specification](docs/PRODUCT_SPEC.md)
- [Phased roadmap](docs/ROADMAP.md)
- [Release checklist](docs/RELEASE_CHECKLIST.md)
