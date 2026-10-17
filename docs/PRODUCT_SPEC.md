# KlaarKompas — product specification (MVP)

## Product promise

Help people move from “I don't know where to start” to a completed real-world activity with practical, step-based guidance that can be saved and resumed.

## Primary users

- People learning to do everyday tasks independently.
- Busy adults and families who need quick, ordered instructions.
- People who prefer larger text, audio instructions or fewer decisions per screen.
- People who may want a trusted person to help in a later release.

## MVP user journeys

1. **Find a guide:** open the app, type a query or browse a category, review duration and summary.
2. **Follow a guide:** read one step, optionally listen, use the help prompt when stuck, navigate backward/forward and finish.
3. **Return later:** progress persists locally; a user can resume a guide after closing the app.
4. **Personalise:** save favourites and enable larger text.
5. **Use a different device:** the same core library is available in a compact phone layout and a wide tablet layout; cross-device sync is a later feature, not part of this offline MVP.

## Navigation and screens

- **Ontdekken:** search field, category chips, resume row, guide cards and clear empty results.
- **Mijn gidsen:** favourites, active guides and completion confirmation.
- **Instellingen:** larger text and a concise explanation of local/offline data handling.
- **Guide player:** title/category, progress, a single current step, optional text-to-speech, safety note where applicable, “Loop je vast?” help and previous/next/finish controls.

## Design principles

- Dutch-language first release.
- One primary action at a time during a guide.
- Comfortable spacing and touch targets.
- Clear body text, high-contrast colours and semantic labels.
- TalkBack-compatible controls; text-to-speech is a convenience and does not replace accessibility services.
- Phone navigation uses a bottom bar; tablet navigation uses a sidebar and wider guide layout.
- Useful baseline functionality without an account or connection.

## Content rules

- Each guide has a unique ID, category, summary, estimated duration and sequential steps.
- Safety-related instructions include an explicit caution where relevant.
- Avoid presenting medical, legal or financial guidance as authoritative advice.
- Any future community submissions must go through review, attribution and a correction process.
- AI-generated guide content must not be published automatically. It must show source/context and pass quality and safety checks.

## Data and privacy

- Current local fields: saved guide IDs, completion IDs, next step for each active guide and larger-text preference.
- No account, analytics SDK, remote backend or Internet permission in the MVP.
- Android backup is disabled for this starter version.
- Future sync or sharing would need explicit opt-in, authentication, least-privilege access, retention/deletion controls and a privacy review.

## MVP acceptance criteria

- Guide search works case-insensitively and supports category filtering.
- Guide progress and favourites persist after app restarts.
- Guide library is available offline.
- Phone and tablet variants compile separately.
- Unit tests and Android lint complete successfully in CI.
- UI is checked with TalkBack, larger system font, portrait/landscape and common narrow/wide screens before publication.
- Release build is signed using a protected signing process before any store upload.

## 0.2.0 — Personal guides and stuck help

- Users can create personal guides with a title, optional summary, category, estimated duration and up to 20 steps.
- Personal guides are stored locally in Android DataStore and remain available offline without an account or network permission.
- Personal guide records are encoded with URL-safe Base64 to protect separators, punctuation and multiline text.
- Personal guides participate in search and appear in “Mijn gidsen” alongside built-in guides.
- The “Ik loop vast” helper offers three options: break the task down, ask for focused help, or pause and retry safely.

