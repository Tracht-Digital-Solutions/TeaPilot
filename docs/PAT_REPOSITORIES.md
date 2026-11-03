# PAT and repository milestone

Android 0.2.0-dev connects to an HTTPS Gitea server (including subpath installs),
discovers its version, validates a personal token using `/api/v1/user`, and loads
`/api/v1/user/repos` in pages. Required read scopes: `read:user`, `read:repository`.
The token's selected repository permissions also constrain the result.

Pagination uses the Link relation flag and requests the next numeric page from
the configured server. It never follows URLs returned by the server. Redirects
are refused; the user must enter the final HTTPS address. HTTP and malformed
server addresses are rejected. TLS validation uses the platform defaults.
Requests have timeouts and a 4 MiB response limit. Error bodies are not shown or
logged. Tokens are sent only as Authorization headers, never in URLs.

Credentials and repository data are session-only. Rotation retains the signed-in
ViewModel, while process death requires sign-in again. Logout cancels the active
request and clears the session. No stored account or encrypted vault is claimed.

## Automated checks

Core: address/subpath validation, encoded traversal rejection, Authorization
headers, redirect refusal, disconnection and header injection rejection.
Android JVM: anonymous discovery, authenticated user parsing, private repository
metadata, pagination with server-side page caps, empty pages and non-JSON data.
Gitea CI runs these tests, both lint checks and both debug APK builds.

## Manual checks still required

1. Sign in to a test Gitea instance with a read-only token; verify private/public
   repository visibility against that token's permissions.
2. Load more than 50 repositories, refresh, and verify no duplicate rows.
3. Test invalid/revoked tokens, denied scopes, offline mode and redirect responses.
4. Rotate the device during a request; log out during loading; verify that stale
   responses never restore a previous account's data.
5. Kill/relaunch the app; verify that credentials are not restored.
6. Check TalkBack, keyboard navigation, large text and tablet layouts.

No credentialed device or emulator integration test has been performed yet.
Wear OS authentication, OAuth and persistent multiple accounts are later work.

## Login hardening (0.2.1-dev, issue #7)

If anonymous version discovery returns HTTP 401 or 403, discovery is retried
with the submitted token against the same configured server. Other failures,
including redirects, do not trigger that authenticated retry. Authentication
must succeed before repositories are requested. Raw server input and tokens
use only non-saveable Compose state; raw addresses may themselves contain
accidentally pasted credentials and must not enter saved-state Bundles.
