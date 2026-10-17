# Release checklist

## Source and build
- [ ] CI workflow completes successfully.
- [ ] Phone and tablet unit tests pass.
- [ ] Phone and tablet lint reports reviewed; no unresolved serious issues.
- [ ] Debug APKs install and launch on a phone and a tablet.
- [ ] Release APK/AAB outputs are generated from the intended commit.
- [ ] Release artefacts are signed securely and signature is verified.

## Functional checks
- [ ] Search handles useful terms, mixed case and no-result queries.
- [ ] Category chips filter results correctly.
- [ ] Open, resume, previous, next, finish and restart paths work.
- [ ] Favourites and progress survive force-close/relaunch.
- [ ] Offline search and guide reading work with network disabled.
- [ ] Text-to-speech works on a device with Dutch speech resources installed.
- [ ] Larger text and TalkBack make all key actions usable.
- [ ] No clipped text on small phone, large font, tablet portrait and landscape.

## Content, privacy and Play Store
- [ ] Safety-critical wording reviewed by a qualified person.
- [ ] App name/logo/description and screenshots reviewed.
- [ ] Application IDs and version codes confirmed before first listing.
- [ ] Privacy policy matches actual data behavior and SDKs.
- [ ] Store data-safety form and target API requirements verified at publication time.
- [ ] Test track, release notes and rollback plan prepared.

## Added for 0.2.0 personal guides

- [ ] Create a guide with one step and with several steps.
- [ ] Create a guide with accents, punctuation and multiple lines; close and reopen the app and verify it is preserved.
- [ ] Confirm personal guides appear in search, category filters and “Mijn gidsen”.
- [ ] Confirm built-in guides cannot be overwritten by a personal guide ID.
- [ ] Verify the 20-step limit and reject an empty title or empty step list.
- [ ] Use the stuck-help options with TalkBack and larger text enabled.
- [ ] Confirm GitHub Actions reports green for unit tests, lint, phone/tablet APKs and phone/tablet AABs before calling the build verified.
