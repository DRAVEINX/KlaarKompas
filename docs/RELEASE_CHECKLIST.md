# Release checklist

## Source and build
- [ ] CI workflow completes successfully for the exact commit to be released.
- [ ] Phone and tablet unit tests pass.
- [ ] Phone and tablet lint reports reviewed; serious findings resolved or documented.
- [ ] Debug APKs install and launch on real phone and tablet devices.
- [ ] Signed release APK/AAB outputs are built by the manual signed-release workflow.
- [ ] Verify the APKs with Android `apksigner` and the AABs with `jarsigner -verify`.
- [ ] Keep the signed release artifact and SHA-256 checksums; never publish CI artifacts labelled `UNSIGNED`.
- [ ] Confirm the phone and tablet use their intended, separate application IDs and version codes.
- [ ] Test the exact signed release candidate on both devices before Play upload.

## Functional checks
- [ ] Search handles useful terms, mixed case and no-result queries.
- [ ] Category chips filter results correctly.
- [ ] Open, resume, previous, next, finish and restart paths work.
- [ ] Favourites and progress survive force-close/relaunch.
- [ ] Offline search and guide reading work with network disabled.
- [ ] Text-to-speech works on a device with Dutch speech resources installed.
- [ ] Larger text and TalkBack make all key actions usable.
- [ ] No clipped text on small phone, large font, tablet portrait and landscape.
- [ ] Personal guide creation, Unicode, punctuation, multiline text and the 20-step limit behave correctly.
- [ ] Physical-device checks cover rotation, process recreation and app data retention.

## Signing and repository security
- [ ] Upload keystore is controlled by the developer and has a separate secure backup.
- [ ] GitHub Actions secrets exist for `ANDROID_UPLOAD_KEYSTORE_BASE64`, `ANDROID_UPLOAD_KEYSTORE_PASSWORD`, `ANDROID_UPLOAD_KEY_ALIAS`, and `ANDROID_UPLOAD_KEY_PASSWORD`.
- [ ] No keystore, passwords, private keys or service-account credentials are committed to Git.
- [ ] Signed-release workflow only runs manually from `main` and does not publish automatically.
- [ ] The certificate fingerprint from the signed artifacts is recorded securely and matches the Play Console upload-key setup.
- [ ] Google Play App Signing setup has been completed for each separate app listing.

## Content, privacy and Play Store
- [ ] Review the Dutch store listing and screenshots for both the phone and tablet editions.
- [ ] Complete and publish the privacy policy at a public, stable HTTPS URL; open it in a signed-out browser to verify access.
- [ ] Verify the Data safety form against the exact released code and every included SDK.
- [ ] Complete content rating, app access, ads declarations and any other Play Console forms truthfully.
- [ ] Confirm app names, application IDs, version codes and version names before the first listing.
- [ ] Verify target API requirements at submission time; this project currently targets API 36.
- [ ] Review all safety-sensitive guide text with an appropriate human reviewer.
- [ ] Prepare release notes, testing instructions, support contact and a rollback plan.
- [ ] Internal test succeeds. If the Play Console account is a new personal account subject to the policy, complete the required closed test before requesting production access.

## 0.2.0 personal-guide checks
- [ ] Create a guide with one step and with several steps.
- [ ] Create a guide with accents, punctuation and multiple lines; close/reopen and verify preservation.
- [ ] Confirm personal guides appear in search, category filters and “Mijn gidsen”.
- [ ] Confirm built-in guides cannot be overwritten by a personal guide ID.
- [ ] Verify the 20-step limit and reject an empty title or empty step list.
- [ ] Use stuck-help options with TalkBack and larger text enabled.
- [ ] Confirm GitHub Actions is green for unit tests, lint and both form-factor builds before calling the source build verified.
