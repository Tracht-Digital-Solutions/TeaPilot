# TeaPilot

Native Gitea client for Android 10+ and Wear OS by Tracht Digital Solutions.

Android now supports HTTPS server discovery, personal access token sign-in and
a paginated repository list with refresh and logout. Credentials remain in
memory for the session; they are never saved to disk or Android saved state.
Wear OS still has its initial native Compose screen.
OAuth, persistent accounts, issue/PR workflows, offline storage and notifications
remain outstanding.

Build prerequisites: JDK 17 and Android SDK 36. Gradle 8.14.3 is pinned.
Run `bash gradlew :core:test :androidApp:testDebugUnitTest :androidApp:assembleDebug :wearApp:assembleDebug`.
On Windows use `gradlew.bat`.

Android and Wear OS only. No Apple scaffolding, telemetry or integrated AI.
The complete previous interview specification was not present in this repository.
Next: Keystore-backed account persistence, OAuth, repository detail views,
issues/PRs, notifications and offline support.

Validation: Gitea run 559 passed core tests, lint and debug APK assembly for
both apps. Both APKs were uploaded as `teapilot-debug-apks`.
https://git.tracht-digital.de/Tracht-Digital-Solutions/TeaPilot/actions/runs/559
No device or emulator test has been performed yet.

The PAT/repository milestone adds targeted transport and API tests. Its updated
APK must pass Gitea CI before merge; run 559 validates the earlier bootstrap.
See docs/PAT_REPOSITORIES.md for behavior and manual validation steps.
