# KlaarKompas for Android

**Kleine stappen. Echte vooruitgang.**

**Source version: 0.2.0**

KlaarKompas is an offline-first Dutch-language Android app with separate phone and tablet editions. It helps people find practical guides, follow one step at a time, listen to instructions, save favourites, create personal guides and resume progress.

## Included in this MVP

- More than 30 bundled Dutch-language guides across five categories, available offline.
- Search across guide titles, summaries, categories and tags.
- One-step-at-a-time guide flow with progress, previous/next, completion and an “Ik loop vast” helper with three alternatives.
- Create personal guides with up to 20 steps; personal guides are saved locally and included in search and “Mijn gidsen”.
- URL-safe Base64 persistence for personal guides to protect punctuation and multiline instructions from corrupting saved records.
- Text-to-speech, TalkBack-friendly labelled controls and a larger-text setting.
- Local favourites and progress storage through Android DataStore; no login or network permission is requested by the current MVP.
- Responsive layouts: compact bottom navigation on phones and a navigation sidebar / wider guided layout on larger screens.
- Separate `phone` and `tablet` product flavors.

## Current build status

The latest recorded GitHub Actions build on 17 October 2026 (run 17) completed successfully for validation, unit tests, lint and both phone/tablet debug, release APK and AAB build tasks. This confirms the source builds in CI; it does **not** mean the release outputs are signed or approved by Google Play.

The ordinary CI artifact is named `klaarkompas-android-<run-number>-UNSIGNED`. Its debug APKs are for testing, and its release APK/AAB outputs are unsigned. **Do not upload these release files to Google Play.**

## Build outputs

- Phone debug APK: `app/build/outputs/apk/phone/debug/`
- Tablet debug APK: `app/build/outputs/apk/tablet/debug/`
- Phone release APK: `app/build/outputs/apk/phone/release/`
- Tablet release APK: `app/build/outputs/apk/tablet/release/`
- Phone release AAB: `app/build/outputs/bundle/phoneRelease/`
- Tablet release AAB: `app/build/outputs/bundle/tabletRelease/`

## Release signing

A separate manual workflow, **KlaarKompas Signed Android Release**, builds signed phone and tablet APK/AAB files from `main`, verifies signatures and writes SHA-256 checksums. It requires a developer-owned upload keystore stored in protected GitHub Actions secrets. It does not submit files to Google Play automatically.

Follow [the signing setup guide](docs/RELEASE_SIGNING.md), then complete the [release checklist](docs/RELEASE_CHECKLIST.md) and use the [Dutch Play Store listing draft](docs/PLAY_STORE_LISTING_NL.md). Read the [privacy policy draft](docs/PRIVACY_POLICY_NL.md) and confirm a public URL and support contact before using it in Play Console.

Never commit a keystore, password, private key or signing secret to this repository.

## App identifiers

- Phone: `nl.draveinx.klaarkompas.phone`
- Tablet: `nl.draveinx.klaarkompas.tablet`

These IDs intentionally allow side-by-side installations and separate Play Store listings. Keep them unchanged after publication unless there is a deliberate migration plan.

## Local development

Install Android Studio, JDK 17 and Android SDK Platform 36 / Build Tools 36.0.0, then open the project in Android Studio and allow Gradle sync.

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

## Privacy and content quality

The current MVP stores preferences, favourites, guide progress and personal guides locally. The main manifest does not request Internet permission and Android backup is disabled. When text-to-speech is used, the selected text is handed to the TTS engine configured on the device; review that engine provider's privacy information. The app has no account, cloud sync or online portal in this version.

Guides are general practical prompts, not professional legal, medical, financial, electrical or other safety-critical advice. Important instructions should be reviewed by a suitable human expert before public distribution.
