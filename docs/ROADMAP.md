# KlaarKompas roadmap

## Phase 0 — Product baseline (included in this source package)

- Standalone Android project and brand starter.
- 33 bundled Dutch-language guide entries.
- Offline guide search and categories.
- Guided steps, completion, local progress and favourites.
- Text-to-speech button and larger-text setting.
- Separate phone/tablet product flavors, workflow, tests and release checklist.

## Phase 1 — Validate and stabilise

- Run CI; repair any compiler, test or lint findings.
- Install on a physical phone and tablet; validate split-screen, rotation, large font and TalkBack.
- Review guide wording and safety notices with suitable reviewers.
- Add UI automation for search, save, resume, next/back, completion and empty results.
- Add product privacy/terms pages and prepare store listing assets.

## Phase 2 — Make guide content genuinely strong

- Expand reviewed library to additional everyday categories.
- Add richer guide media/illustrations with clear source and licence metadata.
- Add difficulty level, tools/materials, expected time and last-reviewed date.
- Add guide feedback: helpful/not helpful, correction suggestion and report-content option.
- Define editorial moderation and a publishing workflow.

## Phase 3 — Personalisation and collaboration

- User-created private guides and reusable templates.
- Step alternatives (“I don't have this item”, “this looks different”) with non-destructive backtracking.
- Explicitly invited helper links or accounts, revocation, least-privilege access and audit trail.
- Optional encrypted sync after threat model, backend design and privacy review.

## Phase 4 — Responsible intelligent guidance

- Search expansion and plain-language rewrites.
- Answers grounded in reviewed guide material with visible references and freshness metadata.
- Never automatically publish AI-generated instructions.
- Safety classification and clear escalation paths for high-risk topics.

## Release gates

Do not mark a phase complete solely because workflow YAML exists. Require passing CI, manual phone/tablet checks, content review, privacy/security checks and a signed release candidate. Public-release signing must use protected GitHub secrets or another secure signing environment; secrets must never be committed.

## Shipped in source package 0.2.0

- Personal guide creation and local persistence.
- Search and saved-guide integration for user-created guides.
- Serialization unit tests for Unicode, multiline text, delimiters, malformed records and duplicate IDs.
- Expanded in-context stuck-help alternatives.

## Next implementation priorities

1. Run GitHub Actions to confirm Android compilation, tests, lint and both form-factor build variants.
2. Install the phone and tablet debug APKs on real devices and correct any layout or accessibility regressions.
3. Add edit/delete for personal guides and confirmations for destructive changes.
4. Add progress indicators for each guide and improve input validation.
5. After real-world testing, consider import/export and a reviewed content contribution workflow.
