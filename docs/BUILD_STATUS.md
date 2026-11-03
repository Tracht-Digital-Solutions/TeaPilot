# Build status

The initial milestone is an installable debug APK, not a completed Gitea client.
Implemented: separate native Android/Wear Compose screens and shared HTTPS
server-address validation. Login and API operations remain outstanding.

Local execution encountered sandbox restrictions: Gradle cannot connect to its
local daemon and external download hosts cannot be resolved. A Linux Gitea
pipeline runs core tests, Android lint and debug assembly for both apps.

Debug APKs use a development signing key. Stable release signing, store delivery,
full accessibility/device tests and complete product quality gates remain open.
GitHub serves as a source mirror; CI runs on Gitea Linux runners.
