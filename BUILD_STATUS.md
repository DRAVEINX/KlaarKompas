# Build status — source package 0.2.0

## Verified locally
- `python tools/validate_project.py`: 37 passed, 0 failed.
- The guide catalogue contains 33 unique starter guides with valid categories and searchable content.
- The local Kotlin compiler and codec smoke test passed for personal guide serialization, including Unicode, punctuation separators, multiline instructions and optional tips.
- XML files parsed successfully; the manifest does not request Internet access and disables Android backup.
- Separate phone and tablet product flavors, version 0.2.0 and their own application IDs are configured.

## Still needs GitHub Actions/device confirmation
- Full Android/Compose compilation of the app.
- Android unit-test and lint task results for phone and tablet.
- Phone/tablet debug APK, release APK and AAB builds.
- Installation and UI checks on real Android phone and tablet hardware.
- Release signing and Google Play submission.

No APK/AAB build is being called successful until a GitHub Actions run confirms the corresponding tasks. Release outputs must be signed through a secure, separate signing setup before Play Store publication.
