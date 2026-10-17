# Secure Android release signing

This repository deliberately does not contain a keystore. Release signing is performed only by the manually started **KlaarKompas Signed Android Release** workflow on the `main` branch. Ordinary CI builds stay clearly marked as unsigned and never receive signing secrets.

## 1. Create and back up your own upload keystore

Use a private, trusted computer with JDK 17 installed. In a private directory, run:

```bash
keytool -genkeypair -v -keystore klaarkompas-upload.jks -alias klaarkompas-upload -keyalg RSA -keysize 2048 -validity 10000
```

Choose a strong, unique keystore password and key password. Store the keystore and passwords in a password manager and make an encrypted backup in a separate secure location. Do not send them in chat/email, commit them to Git, or put them in the project directory.

If a Play Console listing already exists, do not casually replace its registered upload key. Follow the key-reset process in Play Console instead. These instructions assume new listings or that this key is the intended registered upload key.

## 2. Convert the keystore to Base64

The original `klaarkompas-upload.jks` stays on your computer. Only its Base64 encoding is added as a protected GitHub secret.

PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes(".\klaarkompas-upload.jks")) | Set-Clipboard
```

This copies the long Base64 value into your clipboard. Paste it directly into the GitHub secret form; do not place it in a tracked text file.

## 3. Add four GitHub Actions secrets

In the repository, open **Settings → Secrets and variables → Actions → New repository secret** and add:

| Secret name | Value |
|---|---|
| `ANDROID_UPLOAD_KEYSTORE_BASE64` | Base64 content of the `.jks` file |
| `ANDROID_UPLOAD_KEYSTORE_PASSWORD` | Keystore password |
| `ANDROID_UPLOAD_KEY_ALIAS` | `klaarkompas-upload` (or the alias you chose) |
| `ANDROID_UPLOAD_KEY_PASSWORD` | Password for that alias |

Never put these values in source files, workflow YAML, issue comments or build logs. Keep the repository's access list limited to trusted maintainers.

## 4. Run the signed workflow

First merge the release-preparation changes into `main` and confirm that **KlaarKompas Android CI** is green. Then open **Actions → KlaarKompas Signed Android Release → Run workflow** and run it on `main`.

The workflow:
- verifies all four secrets exist;
- decodes the keystore only under the runner's temporary directory;
- reruns the phone/tablet tests and lint;
- builds the two release APKs and two release AABs;
- verifies APK and AAB signatures;
- writes release metadata and SHA-256 checksums; and
- uploads a 90-day artifact for manual download.

The workflow does not upload or publish anything to Google Play automatically.

## 5. First-time Play setup

For each separate listing, confirm the package ID and complete the Google Play App Signing setup. Google Play normally uses the upload key to authenticate uploads and manages the app-signing key for distribution. Record the upload certificate fingerprint securely and confirm Play Console recognises it. Never upload the `.jks` file itself to Play Console; upload the signed `.aab`.

The signing workflow uses the same upload key for both app flavors. Google Play App Signing can manage the distribution signing for each package separately. If you later choose to use different upload keys for the two listings, the workflow and Gradle signing configuration must be adapted first.

## Troubleshooting

- **Missing secret:** add the exact secret name above, then rerun the manual workflow.
- **Wrong password/alias:** correct the stored secret from your password manager; do not rotate the keystore blindly.
- **Signature verification failed:** do not upload that artifact. Inspect the job logs without printing or exposing secrets.
- **Already published app:** use the upload key registered for that Play Console app, not a newly created arbitrary key.
