# Changelog

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
