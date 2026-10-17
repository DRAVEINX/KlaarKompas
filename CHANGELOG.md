# Changelog

## 0.2.0 — Personal guides and improved help

- Added a dedicated “Gids maken” destination so users can create their own guide with a title, short summary, category, time estimate and up to 20 steps.
- Personal guides are saved on-device with DataStore, reloaded at startup, included in search, and listed with saved guides.
- Added a URL-safe Base64 codec so separators, accents and multiline text do not corrupt personal guide records.
- Improved “Ik loop vast” with three alternative approaches: break the step down, ask for focused help, or pause and restart safely.
- Added codec tests for multiline/unicode round trips, malformed entries and duplicate IDs.
- Bumped Android version to 0.2.0 (versionCode 2).

Status: preflight checks pass locally. Full Android compilation, unit tests, lint and APK/AAB builds must be confirmed by GitHub Actions.

## 0.1.0 — MVP source package

- Initial standalone Android app concept for KlaarKompas.
- Separate phone and tablet product flavors.
- Offline guide search and category filters.
- 33 built-in Dutch-language guides.
- Guided step reader, local progress, favourites and completion feedback.
- Text-to-speech button, larger-text preference, TalkBack labels.
- Phone bottom navigation and tablet sidebar / wider guide reader.
- GitHub Actions workflow for unit tests, lint, APKs and AABs.
- Product specification, roadmap and release checklist.

Status: source prepared; a real Android CI build and on-device validation remain required.

## Source package refinement
- Added a dependency-free preflight script for XML, application variants, guide IDs, privacy defaults, Gradle pinning and CI tasks.
- Added the preflight as the first automated GitHub Actions check.
- Re-ran local Kotlin guide-library smoke tests and XML/manifest/workflow checks; Android build still requires a network-enabled Android SDK runner.
