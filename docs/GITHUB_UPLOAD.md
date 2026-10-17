# Upload KlaarKompas as a separate GitHub repository

Keep this project separate from MoodPress. Create an empty repository named `KlaarKompas` under the correct GitHub account. Do not initialise it with an additional README, licence or `.gitignore`, because those files are already included here.

After extracting the ZIP, open a terminal in the `KlaarKompas` folder and run:

```bash
git init -b main
git add .
git commit -m "Initial KlaarKompas Android MVP"
git remote add origin https://github.com/DRAVEINX/KlaarKompas.git
git push -u origin main
```

If your GitHub account uses a different owner or you choose a different repository name, replace the URL in the `git remote add` command with that repository's HTTPS URL. GitHub may ask you to authenticate in a browser.

After the push, open the repository's **Actions** tab and select **KlaarKompas Android CI**. Wait for the run to finish and verify that all test, lint and build steps pass. Then open the successful run and download its `klaarkompas-android-<run number>` artifact ZIP. It contains separate phone/tablet APKs and release AAB/APK outputs. The release outputs are unsigned and must not be uploaded to Google Play until a secure signing setup and on-device validation are complete.
