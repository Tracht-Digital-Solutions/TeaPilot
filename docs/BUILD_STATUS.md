# Build status

The initial milestone is an installable debug APK, not a completed Gitea client.
Verified baseline: Android session-only PAT sign-in and a paginated repository
list with refresh/logout, shared HTTPS transport and a Wear OS starting screen.
OAuth, persistent accounts and the remaining client workflows are outstanding.
Product scope and unresolved decisions are tracked in SPECIFICATION.md and
OPEN_DECISIONS.md.

Local execution encountered sandbox restrictions: Gradle cannot connect to its
local daemon and external download hosts cannot be resolved. A Linux Gitea
pipeline runs core tests, Android lint and debug assembly for both apps.

Debug APKs use a development signing key. Stable release signing, store delivery,
full accessibility/device tests and complete product quality gates remain open.
GitHub currently contains API-transferred sources; an automatic push mirror is
still open in issue #27. CI runs on Gitea Linux runners.

## Successful build

Gitea run 559, commit b5d4bb6ac88fd7b0b78ddec0e95a983958499783:
`:core:test`, both `lintDebug` tasks and both `assembleDebug` tasks passed.
Gradle reported BUILD SUCCESSFUL in 3m 43s. Artifact `teapilot-debug-apks`
contains two APKs, 45,247,277 raw bytes (15,700,356 uploaded bytes).
https://git.tracht-digital.de/Tracht-Digital-Solutions/TeaPilot/actions/runs/559

Gitea run 564 verified commit 7a73e575f9a4388978c2f696ef1c483728c4d474:
core and Android API unit tests, both lint checks, both debug APK assemblies
and artifact upload succeeded. This is the verified PAT milestone.
https://git.tracht-digital.de/Tracht-Digital-Solutions/TeaPilot/actions/runs/564

Login hardening (#7, PR #30) remains unmerged. Run 570 is queued without
executed checks. No new APK or successful regression run is claimed for that fix.
