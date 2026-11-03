# TeaPilot

Native Gitea client for Android 10+ and Wear OS by Tracht Digital Solutions.

Fresh Kotlin implementation. Shared core validates HTTPS server addresses;
Android and Wear OS have initial native Compose screens. Login, API access,
offline storage and notifications are not implemented yet.

Build prerequisites: JDK 17 and Android SDK 36. Gradle 8.14.3 is pinned.
Run `bash gradlew :core:test :androidApp:assembleDebug :wearApp:assembleDebug`.
On Windows use `gradlew.bat`.

Android and Wear OS only. No Apple scaffolding, telemetry or integrated AI.
The complete previous interview specification was not present in this repository.
Next: verified build, server discovery, secure PAT/OAuth login, account isolation,
repository browser, issues/PRs, notifications and offline support.

Validation: Gitea run 559 passed core tests, lint and debug APK assembly for
both apps. Both APKs were uploaded as `teapilot-debug-apks`.
https://git.tracht-digital.de/Tracht-Digital-Solutions/TeaPilot/actions/runs/559
No device or emulator test has been performed yet.
