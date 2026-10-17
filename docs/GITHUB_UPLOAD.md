# KlaarKompas repository and Android CI

The standalone repository is `DRAVEINX/KlaarKompas`. Do not place this project inside MoodPress.

The source tree should include the app module, `.github/workflows/android.yml`, `tools/validate_project.py`, the Gradle configuration and the project documentation.

## Start a build

1. Open [DRAVEINX/KlaarKompas on GitHub](https://github.com/DRAVEINX/KlaarKompas).
2. Open **Actions** and select **KlaarKompas Android CI**.
3. Select **Run workflow** on the main branch if no run started after the source update.
4. Open the run and confirm the preflight, unit tests, lint, phone/tablet APK builds and phone/tablet AAB builds are successful.
5. Download `klaarkompas-android-<run number>` from the run's **Artifacts** section.

## Local build

Install Android Studio, JDK 17 and Android SDK Platform 36 / Build Tools 36.0.0, then run from the project root:

```powershell
.\gradlew.bat testPhoneDebugUnitTest testTabletDebugUnitTest
.\gradlew.bat assemblePhoneDebug assembleTabletDebug assemblePhoneRelease assembleTabletRelease bundlePhoneRelease bundleTabletRelease
```

The first build needs network access for the official Gradle distribution and Android dependencies.

## Signing and Play Store

The CI release APKs and AABs are unsigned. Do not upload them to Google Play until signing and Play App Signing have been set up securely. Never commit keystores, passwords or signing keys to the repository.
