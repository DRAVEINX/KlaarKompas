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
